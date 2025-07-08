package Unit;

import Building.AbstractBuilding;
import Building.Barracks;
import Command.CommandContext;
import Command.CommandType;
import Manager.BuildingManager;
import Manager.PlayerUnitManager;
import Panel.GamePanel;
import Pathfind.Pathfinder;
import Resource.BuildingType;
import Resource.Cost;
import Resource.ResourceType;
import Unit.StatePackage.*;
import Util.Camera;
import Util.TileMap;
import Util.Vector2;
import Util.Vector2Int;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WorkerUnit extends AbstractUnit implements IControllable {

    // Declarations
    protected GamePanel gp;
    protected BuildingManager BM;
    protected CommandType currentCommand;
    protected CommandContext ctx;

    private boolean isMoving;
    private float speed = 1.8f;
    public static final int TOKEN = 5;

    // Type of resource WorkerUnit is currently gathering
    public ResourceType currentGatherType;

    Pathfinder pf;
    TileMap map; // Reference to the game map
    List<Vector2Int> path; // Path to follow
    Vector2Int currentNode; // Current grid cell
    Vector2Int nextNode; // Next grid cell to move toward
    int currentIndex; // Current step in the path

    Vector2Int start, end; // Start and destination cells

    private long blockStartTime = 0; // Timer for handling blocked cells
    private boolean isWaiting = false; // Indicates if the unit is waiting for a cell to clear

    private AbstractBuilding constructionBuildingRef;
    private AbstractUnit repairUnitRef;
    //private AbstractResource resourceGatherRef; eventually...

    private IUnitState currentState;

    // Constructor
    public WorkerUnit(TileMap map, BuildingManager BM, GamePanel gp, float x, float y, int width, int height) {
        super(x, y, width, height);
        this.selected = false;
        this.isMoving = false;

        // boost player unit health
        setMaxHealth(300.0f);
        currentHealth = maxHealth;

        this.BM = BM;
        this.map = map;
        this.gp = gp;
        pf = new Pathfinder();

        this.currentNode = GamePanel.convertWorldToCell(x, y);
        this.currentState = new IdleState();
        issueCommand(CommandType.IDLE, null);
    }

    @Override
    public void draw(Graphics g, Camera camera) {
        if (camera.captures(this) && !isDead()) {

            // draw a highlight if selected
            if (selected) {
                // draw a border around the oval
                g.setColor(Color.CYAN);
                g.drawOval((int)(((x - 2) - camera.getX()) * camera.scaleX),
                        (int)(((y - 2) - camera.getY()) * camera.scaleY),
                        (int)((width + 4) * camera.scaleX),
                        (int)((height + 4) * camera.scaleY));
            }

            g.setColor(Color.LIGHT_GRAY);
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
            case IDLE -> setState(new IdleState());
            case MOVE -> setState(new MoveState(ctx.getX(), ctx.getY(), ctx.getCamera()));
            case HOLD_POSITION -> endPathEarly();
            case CONSTRUCT -> setState(new ConstructState(ctx.getBuildingType(), ctx.getCellPos()));
            case REPAIR -> setState(new RepairState(ctx.getCellPos()));
            //case GATHER -> setState(new GatherState(ctx.getTargetResource())); return;
        }
    }

    // update command on every ticks
    public void update() { currentState.update(this); }

    public void setState(IUnitState newState) {
        if (currentState != null) currentState.onExit(this);
        currentState = newState;
        currentState.onEnter(this);
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

    public void startRepair() {
        moveToRepairSite();
    }

    public void endRepair(){
        System.out.println("EndRepair from WorkerUnit");
        repairUnitRef = null;
    }

    public void moveToRepairSite(){
        List<Vector2Int> freeCellList = findAllAdjacentFree(map, ctx.getCellPos(), 1, 1);
        this.start = GamePanel.convertWorldToCell(this.x, this.y);
        this.end = freeCellList.get(2);

        if (map.intArr[end.y][end.x] == 1) {
            System.out.println("Construction site is blocked.");
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
                setState(new IdleState());
            }
        }
    }

    ///  ------------------------------

    /**
     * Construction section
     * handle the construction of building on command
     */
    public void updateConstruction() {
        updateMoveToLocation();
        if (!isMoving && constructionBuildingRef != null) {
            if (constructionBuildingRef.currentState != AbstractBuilding.State.IN_OPERATION) {
                constructionBuildingRef.currentState = AbstractBuilding.State.UNDER_CONSTRUCTION;
            }
        }
    }

    public void startConstruction() {
        moveToConstructionSite();
        // No reference = creating a new building
        // otherwise moveToConstructionSite and resume construction
        if (constructionBuildingRef == null) {
            switch (ctx.getBuildingType()){
                case BARRACKS -> constructBarracks(ctx.getCellPos());
                case SUPPLY_DEPOT -> constructSupplyDepot(ctx.getCellPos());
                case COMMAND_CENTER -> constructCommandCenter(ctx.getCellPos());
            }
        }
    }

    public void endConstruction(){
        System.out.println("EndConstruction from WorkerUnit");
        if (constructionBuildingRef.isUnderConstruction()) {
            System.out.println("UnderConstruction from WorkerUnit");
            constructionBuildingRef.currentState = AbstractBuilding.State.PRE_DEPLOYMENT;
        }
        constructionBuildingRef = null;
    }

    public void moveToConstructionSite(){
        List<Vector2Int> freeCellList = findAllAdjacentFree(map, ctx.getCellPos(), Barracks.WIDTH_TILES, Barracks.HEIGHT_TILES);
        this.start = GamePanel.convertWorldToCell(this.x, this.y);
        this.end = freeCellList.get(0);

        if (map.intArr[end.y][end.x] == 1) {
            System.out.println("Construction site is blocked.");
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

    // Collects all valid free cells around a w×h building footprint
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

    /**
     * Construct a building
     * If we can’t afford it -> do nothing
     */
    public void construct(AbstractBuilding b, Vector2Int startPos) {
        BuildingType type = b.TYPE;
        Cost cost = type.getCost();
        if (!gp.RM.canAfford(cost)) {
            System.out.println("Not enough resources for " + type + " building");
            constructionBuildingRef = null;
            return;
        }

        // spend resource, construct
        gp.RM.spend(cost);
        // change to .add to a list<AbstractBuilding>, not just Barracks cast
        BuildingManager.buildingList.add((Barracks) b);

        // If it's a supply‐providing building, bump the cap
        if (type.getSupplyProvided() > 0) {
            gp.RM.increaseMaxSupply(type.getSupplyProvided());
        }
        System.out.println("Built " + type
                + " | Minerals left: " + gp.RM.get(ResourceType.MINERAL)  + " | Supply: " + gp.RM.getUsedSupply() + "/" + gp.RM.getMaxSupply());
    }

    public void constructBarracks(Vector2Int startPos) {
        // convert back to world size after snap
        Vector2 startf = GamePanel.convertCellToWorld(startPos.x, startPos.y);
        if (BM.isValidPlacement(startPos, Barracks.WIDTH_TILES, Barracks.HEIGHT_TILES)) {
            constructionBuildingRef = new Barracks(map, gp, startf.x, startf.y);
            constructionBuildingRef.setTag("Barracks");
            constructionBuildingRef.setID(startPos.x*startPos.y); // ID is the index of start point inside the grid
            construct(constructionBuildingRef, ctx.getCellPos());
        }
    }

    public void constructSupplyDepot(Vector2Int startPos) {
        // pass
    }

    public void constructCommandCenter(Vector2Int startPos) {
        // pass
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
            System.out.println("Destination is blocked.");
            //end = getRandomNearbyPoint(end, 3); // Try within a range of 2
            return;
        }

        path = pf.FindPath(map.intArr, start, end);
        //map.highlightPath(path);
        //map.printer();

        if (path == null || path.isEmpty()) {
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
                    currentCommand = CommandType.IDLE;
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
    private void endPathEarly(){
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

    public void setConstructionBuildingRef(AbstractBuilding constructionBuildingRef){
        System.out.println("constructionBuildingRef set to : " + constructionBuildingRef.toString());
        this.constructionBuildingRef = constructionBuildingRef;
    }

    // -----------------------------------
    // pretty printing
    @Override
    public String toString() {
        return "tag : " + tag +" "+ ID + " currentState : " + currentCommand + " IsSelected : " + selected;
    }
}
