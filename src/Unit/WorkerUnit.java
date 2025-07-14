package Unit;

import Building.AbstractBuilding;
import Building.BuildingType;
import Building.CommandCenter;
import Command.CommandContext;
import Command.CommandType;
import Manager.BuildingManager;
import Manager.PlayerUnitManager;
import Manager.ResourceManager;
import Panel.GamePanel;
import Pathfind.Pathfinder;
import Resource.*;
import Unit.StatePackage.*;
import Util.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WorkerUnit extends AbstractUnit implements IControllable {

    /// Declarations
    protected GamePanel gp;
    protected BuildingManager BM;
    protected ResourceManager RM;
    protected CommandType currentCommand;
    protected CommandContext ctx;

    private boolean isMoving;
    private float speed = 1.8f;
    public static final int TOKEN = 5;

    // Type of resource WorkerUnit is currently gathering
    public ResourceType currentGatherType;
    public final int carryCapacity = 10;
    public int carryLoad = 0;
    public int quantity = 1;

    Pathfinder pf;
    TileMap map; // Reference to the game map
    List<Vector2Int> path; // Path to follow
    Vector2Int currentNode; // Current grid cell
    Vector2Int nextNode; // Next grid cell to move toward
    int currentIndex; // Current step in the path

    Vector2Int start, end; // Start and destination cells

    private long blockStartTime = 0; // Timer for handling blocked cells
    private boolean isWaiting = false; // Indicates if the unit is waiting for a cell to clear

    private AbstractBuilding buildingRef;
    private AbstractUnit repairUnitRef;
    private ResourceNode resourceNodeRef;
    private boolean commandCenterDeployed;

    private IUnitState<WorkerUnit> currentState;

    // Constructor
    public WorkerUnit(TileMap map, BuildingManager BM, ResourceManager RM, GamePanel gp, float x, float y, int width, int height) {
        super(x, y, width, height);
        this.selected = false;
        this.isMoving = false;

        // boost player unit health
        setMaxHealth(300.0f);
        currentHealth = maxHealth;

        this.BM = BM;
        this.RM = RM;
        this.map = map;
        this.gp = gp;
        pf = new Pathfinder();

        this.currentNode = GamePanel.convertWorldToCell(x, y);
        this.currentState = new IdleState<WorkerUnit>();
        issueCommand(CommandType.IDLE, null);
    }

    @Override
    public void draw(Graphics g, Camera camera) {
        if (camera.captures(this) && !isDead()) {

            // draw a highlight if selected
            if (selected) {
                // draw a border around the oval
                g.setColor(GameColors.UNIT_HIGHLIGHT);
                g.drawOval((int)(((x - 2) - camera.getX()) * camera.scaleX),
                        (int)(((y - 2) - camera.getY()) * camera.scaleY),
                        (int)((width + 4) * camera.scaleX),
                        (int)((height + 4) * camera.scaleY));
            }

            g.setColor(GameColors.UNIT_PLAYER_WORKER);
            g.fillOval( (int)((x - camera.getX()) * camera.scaleX),
                    (int)((y - camera.getY()) * camera.scaleY),
                    (int)(width * camera.scaleX),
                    (int)(height * camera.scaleY) );

            g.setColor(Color.GRAY);
            g.drawOval( (int)((visionbox.x - camera.getX()) * camera.scaleX),
                    (int)((visionbox.y - camera.getY()) * camera.scaleY),
                    (int)(visionbox.width * camera.scaleX),
                    (int)(visionbox.height * camera.scaleY) );

            g.fillRect( (int)((healthBar.x - camera.getX()) * camera.scaleX),
                    (int)((healthBar.y - camera.getY()) * camera.scaleY),
                    (int)(healthBar.width * camera.scaleX),
                    (int)(healthBar.height * camera.scaleY) );

        }
    }

    // issueCommand one time on call
    @Override
    public void issueCommand(CommandType command, CommandContext ctx) {
        this.currentCommand = command;
        this.ctx = ctx;

        switch (currentCommand) {
            case IDLE -> setState(new IdleState<WorkerUnit>());
            case MOVE -> setState(new MoveState<WorkerUnit>(ctx.getX(), ctx.getY(), ctx.getCamera()));
            case HOLD_POSITION -> setState(new HoldPositionState<WorkerUnit>());
            case REPAIR -> setState(new RepairState(ctx.getCellPos()));
            case GATHER -> setState(new GatherState(ctx.getResourceType(), ctx.getCellPos()));
            case DELIVER -> setState(new DeliverState(ctx.getResourceType(), ctx.getCellPos()));
        }
    }

    @Override
    // update command on every ticks
    public void update() { currentState.update(this); }

    public void setState(IUnitState<WorkerUnit> newState) {
        if (currentState != null) currentState.onExit(this);
        currentState = newState;
        currentState.onEnter(this);
    }

    /// -----------------------------------

    /**
     * Deliver section
     */
    public void updateDelivery() {
        updateMoveToLocation();
        if (!isMoving && commandCenterDeployed) {
            triggerTimedResourceDelivery();
        }
    }

    public void startDelivery() {
        buildingRef = findNearestCommandCenter();
        commandCenterDeployed = buildingRef != null;
        if (commandCenterDeployed) {
            Vector2Int cellPos = GamePanel.convertWorldToCell(buildingRef.getX(), buildingRef.getY());
            ctx = new CommandContext().setDelivery(ctx.getResourceType(), cellPos);
            moveToCommandCenterSite();
        }
        else {
            // back to idle
            Logger.log("No CommandCenter has been deployed.");
            System.out.println("No CommandCenter has been deployed.");
        }
    }

    public void endDelivery() {
        commandCenterDeployed = false;
        buildingRef = null;
    }

    public void moveToCommandCenterSite(){
        List<Vector2Int> freeCellList = findAllAdjacentFree(map, ctx.getCellPos(), CommandCenter.WIDTH_TILES, CommandCenter.HEIGHT_TILES);
        this.start = GamePanel.convertWorldToCell(this.x, this.y);
        this.end = freeCellList.get(ThreadLocalRandom.current().nextInt(freeCellList.size()));

        if (map.intArr[end.y][end.x] == 1) {
            Logger.log("CommandCenter site is blocked.");
            System.out.println("CommandCenter site is blocked.");
            return;
        }

        path = pf.FindPath(map.intArr, start, end);

        if (path == null || path.isEmpty()) {
            Logger.log("No path found.");
            System.out.println("No path found.");
            return;
        }

        currentIndex = 0;
        isMoving = true;
    }

    public void triggerTimedResourceDelivery(){
        deliverTimer++;
        if (deliverTimer%15 == 0) {
            if (carryLoad > 0) {
                carryLoad--;
                gp.RM.add(currentGatherType, 1);
            }
            else if (carryLoad <= 0) {
                //System.out.println("Delivery Over");
                if (resourceNodeRef != null) {
                    Vector2Int cellPos = GamePanel.convertWorldToCell(resourceNodeRef.getX(), resourceNodeRef.getY());
                    ctx = new CommandContext().setGathering(currentGatherType, cellPos);
                    issueCommand(CommandType.GATHER, ctx);
                } else {
                    issueCommand(CommandType.IDLE, null);
                    Logger.log("ResourceNode depleted, assign new location.");
                    System.out.println("ResourceNode depleted, assign new location.");
                }
            }
        }
    }

    private AbstractBuilding findNearestCommandCenter() {
        AbstractBuilding nearest = null;
        float bestDist = Float.MAX_VALUE;
        for (AbstractBuilding b : BuildingManager.buildingList) {
            if (b.TYPE == BuildingType.COMMAND_CENTER) {
                float d = calculateDistance(b.getX(), b.getY());
                if (d < bestDist) {
                    bestDist = d;
                    nearest = b;
                }
            }
        }
        return nearest;
    }



    /**
     * Gather section
     */
    public void updateGather() {
        updateMoveToLocation();
        if (!isMoving && resourceNodeRef != null) {
            triggerTimedResourceGathering();
        }
    }

    public void startGather() {
        setCurrentGatherType(ctx.getResourceType());
        moveToResourceNodeSite();
    }

    public void endGather() {
        if (resourceNodeRef.isDepleted() && resourceNodeRef != null) {
            resourceNodeRef = null;
        }
    }

    public void moveToResourceNodeSite(){
        List<Vector2Int> freeCellList = findAllAdjacentFree(map, ctx.getCellPos(), 1, 1);
        this.start = GamePanel.convertWorldToCell(this.x, this.y);
        this.end = freeCellList.get(2);

        if (map.intArr[end.y][end.x] == 1) {
            Logger.log("ResourceNode site is blocked.");
            System.out.println("ResourceNode site is blocked.");
            return;
        }

        path = pf.FindPath(map.intArr, start, end);

        if (path == null || path.isEmpty()) {
            Logger.log("No path found.");
            System.out.println("No path found.");
            return;
        }

        currentIndex = 0;
        isMoving = true;
    }

    public void triggerTimedResourceGathering(){
        gatherTimer++;
        if (gatherTimer%15 == 0) {
            if (carryLoad < carryCapacity && !resourceNodeRef.isDepleted()) {
                carryLoad++;
                resourceNodeRef.extract(quantity);
            }
            if (carryLoad >= carryCapacity || resourceNodeRef.isDepleted()) {
                // full -> time to deliver
                issueCommand(CommandType.DELIVER, ctx);
            }
        }
    }

    /**
     * Repair section
     */
    public void updateRepair() {
        updateMoveToLocation();
        if (!isMoving && repairUnitRef != null) {
            triggerTimedUnitRepair();
        }
    }

    public void startRepair() { moveToRepairSite(); }

    public void endRepair(){
        Logger.log("EndRepair from WorkerUnit.");
        System.out.println("EndRepair from WorkerUnit");
        repairUnitRef = null;
    }

    public void moveToRepairSite(){
        List<Vector2Int> freeCellList = findAllAdjacentFree(map, ctx.getCellPos(), 1, 1);
        this.start = GamePanel.convertWorldToCell(this.x, this.y);
        this.end = freeCellList.get(2);

        if (map.intArr[end.y][end.x] == 1) {
            System.out.println("Repair site is blocked.");
            return;
        }

        path = pf.FindPath(map.intArr, start, end);

        if (path == null || path.isEmpty()) {
            System.out.println("No path found.");
            return;
        }

        currentIndex = 0;
        isMoving = true;
    }

    public void triggerTimedUnitRepair(){
        repairTimer++;
        if (repairTimer%15 == 0) {
            if (repairUnitRef.currentHealth <= repairUnitRef.getMaxHealth()) {
                repairUnitRef.addHealth(3);
            }else {
                repairUnitRef.currentHealth = repairUnitRef.getMaxHealth();
                setState(new IdleState<WorkerUnit>());
            }
        }
    }

    // Collects all valid free cells around a w×h footprint
    private List<Vector2Int> findAllAdjacentFree(TileMap map, Vector2Int buildPos, int w, int h) {
        List<Vector2Int> border = new ArrayList<>();

        // top edge
        for (int x = buildPos.x; x < buildPos.x + w; x++) {
            border.add(new Vector2Int(x, buildPos.y - 1));
        }
        // right edge
        for (int y = buildPos.y; y < buildPos.y + h; y++) {
            border.add(new Vector2Int(buildPos.x + w, y));
        }
        // bottom edge
        for (int x = buildPos.x + w - 1; x >= buildPos.x; x--) {
            border.add(new Vector2Int(x, buildPos.y + h));
        }
        // left edge
        for (int y = buildPos.y + h - 1; y >= buildPos.y; y--) {
            border.add(new Vector2Int(buildPos.x - 1, y));
        }

        // filter into a new list
        List<Vector2Int> freeCells = new ArrayList<>();
        for (Vector2Int cell : border) {
            if (cell.x >= 0 && cell.x < map.column
                    && cell.y >= 0 && cell.y < map.row
                    && map.intArr[cell.y][cell.x] == 0) {
                freeCells.add(cell);
            }
        }

        return freeCells;
    }

    ///  ------------------------------

    /**
    * Movement section
    * Same code as CombatUnit handle basic movement
    */
    public void updateMoveToLocation() {
        if (isMoving) {

            // done with path
            if (path == null || currentIndex >= path.size())
            {
                isMoving = false;
                vel_x = 0;
                vel_y = 0;
                currentIndex = 0;

                path = null;
                // go back to IDL state
                //issueCommand(CommandType.IDLE, null);
                return;
            }

            // Next node to move toward
            nextNode = path.get(currentIndex);
            Vector2 targetPos = GamePanel.convertCellToWorld(nextNode.x, nextNode.y);

            // Check if the next cell is blocked
            if (isBlocked(nextNode.x, nextNode.y) && currentIndex > 0) {
                handleBlockedCell(nextNode);
                return;
            }

            float distance = calculateDistance(targetPos.x, targetPos.y);

            if (distance <= 2.0f)
            {
                // Snap to the target position
                snapToTarget(targetPos);

                currentIndex++;
                // Save current node as the old nextNode
                currentNode = nextNode;

                // Update last position in intArr
                map.intArr[currentNode.y][currentNode.x] = 9;
            }
            else
            {
                // Calculate the difference
                float dx = (targetPos.x - this.x);
                float dy = (targetPos.y - this.y);

                // Calculate the angle to the target
                float angle = (float) Math.atan2(dy, dx);

                // From the angle, derive the velocity components
                vel_x = (float) (Math.cos(angle) * speed);
                vel_y = (float) (Math.sin(angle) * speed);

                moveX();
                moveY();
                // update hitbox
                syncHitbox();
            }
        }
    }

    // Set a new target and compute velocity so we move toward it.
    public void moveTo(float newX, float newY, Camera camera ) {

        this.start = GamePanel.convertWorldToCell(this.x, this.y);
        this.end = GamePanel.convertWorldToCell(newX, newY);

        //System.out.println("Start : " + start.toString());
        //System.out.println("End : " + end.toString());
        if (PlayerUnitManager.getSelectedUnitList().size() > 1) {
            end = getRandomNearbyPoint(end, 3);
        }
        //System.out.println("End : " + end.toString());

        // Validate destination
        if (map.intArr[end.y][end.x] == 1) {
            Logger.log("Destination is blocked.");
            System.out.println("Destination is blocked.");
            //end = getRandomNearbyPoint(end, 3); // Try within a range of 2
            return;
        }

        path = pf.FindPath(map.intArr, start, end);
        //map.highlightPath(path);
        //map.printer();

        if (path == null || path.isEmpty()) {
            Logger.log("No path found.");
            System.out.println("No path found.");
            return;
        }

        currentIndex = 0;
        isMoving = true; // is moving command ongoing
        //currentCommand = CommandType.MOVE;
    }

    // Handles when the next cell is blocked
    private void handleBlockedCell(Vector2Int blockedNode) {
        if (!isWaiting) {
            isWaiting = true;
            blockStartTime = System.currentTimeMillis();
        } else {
            long elapsed = System.currentTimeMillis() - blockStartTime;
            if (elapsed > 1000) { // 500 ms threshold

                // Recalculate path
                start = GamePanel.convertWorldToCell(this.x, this.y);
                path = pf.FindPath(map.intArr, start, end);

                if (path == null || path.isEmpty()) {
                    stopMovement();
                    // the path might be blocked indefinitely
                    // go back to IDL command
                    issueCommand(CommandType.IDLE, null);
                    return;
                }

                // Reset waiting state
                isWaiting = false;
                blockStartTime = 0;
                currentIndex = 0;
            }
        }
    }

    // Checks if a cell is blocked
    private boolean isBlocked(int x, int y) {
        // Check if the cell is occupied but not the current cell
        return map.intArr[y][x] != 0;
    }

    // Snap to the target position
    private void snapToTarget(Vector2 targetPos) {
        this.x = targetPos.x;
        this.y = targetPos.y;

        // Mark the previous node as unoccupied
        if (currentNode != null) {
            map.intArr[currentNode.y][currentNode.x] = 0;
        }
    }

    private Vector2Int getRandomNearbyPoint(Vector2Int original, int range) {
        int newX, newY;
        Vector2Int randomPoint;

        do {
            newX = original.x + (int) (Math.random() * (2 * range + 1)) - range;
            newY = original.y + (int) (Math.random() * (2 * range + 1)) - range;
            randomPoint = new Vector2Int(newX, newY);
        } while (!isValidPoint(randomPoint));

        return randomPoint;
    }

    private boolean isValidPoint(Vector2Int point) {
        return point.x >= 0 && point.x < map.intArr[0].length &&
                point.y >= 0 && point.y < map.intArr.length &&
                map.intArr[point.y][point.x] == 0; // Check if walkable
    }

    // Stops movement and resets relevant states
    private void stopMovement() {
        isMoving = false;
        vel_x = 0;
        vel_y = 0;
        currentIndex = 0;

        path = null;
    }

    // End the path at next step
    @Override
    public void endPathEarly(){
        // if there's no “next” step, just bail out and stop completely
        if (path == null || currentIndex >= path.size() - 1) {
            stopMovement();
            return;
        }

        // build a new path list containing only that one step
        List<Vector2Int> newPath = new ArrayList<>();
        newPath.add(path.get(currentIndex));
        newPath.add(path.get(currentIndex + 1));
        path = newPath;

        // reset movement command and finish path
        currentIndex = 0;
        vel_x = 0;
        vel_y = 0;
        isMoving = true;
        currentCommand = CommandType.MOVE;
    }


    // -----------------------------------
    // Getter & Setter
    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    public void setRepairUnitRef(AbstractUnit repairUnitRef){
        System.out.println("repairUnitRef set to : " + repairUnitRef.toString());
        this.repairUnitRef = repairUnitRef;
    }

    public void setConstructionBuildingRef(AbstractBuilding buildingRef){
        System.out.println("constructionBuildingRef set to : " + buildingRef.toString());
        this.buildingRef = buildingRef;
    }

    public void setResourceNodeRef(ResourceNode resourceNodeRef){
        System.out.println("resourceNodeRef set to : " + resourceNodeRef.toString());
        this.resourceNodeRef = resourceNodeRef;
    }

    public void setCurrentGatherType(ResourceType currentGatherType){
        this.currentGatherType = currentGatherType;
    }

    public ResourceType getCurrentGatherType(){
        return currentGatherType;
    }


    // -----------------------------------
    // pretty printing
    @Override
    public String toString() {
        return "tag : " + tag +" "+ ID + " currentState : " + currentCommand + " IsSelected : " + selected;
    }
}
