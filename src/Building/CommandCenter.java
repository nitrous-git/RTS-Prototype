package Building;

import GameObjects.Tile;
import Manager.BuildingManager;
import Manager.PlayerUnitManager;
import Panel.GamePanel;
import Resource.Cost;
import Resource.ResourceType;
import Resource.UnitType;
import Unit.AbstractUnit;
import Unit.CombatUnit;
import Unit.WorkerUnit;
import Util.*;

import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

public class CommandCenter extends AbstractBuilding {

    ///  DECLARATIONS ///
    public static final int WIDTH_TILES  = 4;
    public static final int HEIGHT_TILES = 4;
    public static final int TOKEN = 2;

    // production queue
    private static final int MAX_QUEUE      = 5;
    private static final int COOLDOWN_TICKS = 480; // e.g. 120 updates ~2s at 60fps

    private final Queue<AbstractUnit> productionQueue = new LinkedList<>();
    private int cooldownTimer = 0;

    //public static final int CONSTRUCTION_TICKS = 720;
    //private int constructionTimer = 0;

    private float spawnOriginX;
    TileMap map;
    GamePanel gp;
    BuildingManager bm;
    private float spawnOriginY;
    private Vector2 worldPos;
    private Vector2Int cellPos;

    // Class Constructor
    public CommandCenter(TileMap map, GamePanel gp, BuildingManager bm, float x, float y) {
        super(x, y, (int)(GamePanel.TILE_SIZE * WIDTH_TILES), (int)(GamePanel.TILE_SIZE * HEIGHT_TILES));

        TYPE = BuildingType.COMMAND_CENTER;

        worldPos = new Vector2(x, y);
        cellPos = GamePanel.convertWorldToCell(x, y);

        //System.out.println("World : " + worldPos.toString()+ " : Cell : " + cellPos.toString());

        // drop the unit just below the building
        this.spawnOriginX  = x + (GamePanel.TILE_SIZE * WIDTH_TILES - GamePanel.TILE_SIZE) / 2;
        this.spawnOriginY  = y +  GamePanel.TILE_SIZE * HEIGHT_TILES;

        setMaxHealth(300.0f);
        currentHealth = maxHealth;

        this.map = map;
        this.gp = gp;
        this.bm = bm;

        this.currentState = State.UNDER_CONSTRUCTION;
        generateCommandCenter(GameColors.BUILDING_COMMAND_CENTER_UNDER_CONSTRUCTION);
    }

    @Override
    public void update() {
        switch(currentState){
            case PRE_DEPLOYMENT:
                // wait for WorkerUnit call
                return;
            case State.UNDER_CONSTRUCTION:
                constructionTimer++;
                if (constructionTimer >= CONSTRUCTION_TICKS) {
                    currentState = State.IN_OPERATION;
                    generateCommandCenter(GameColors.BUILDING_COMMAND_CENTER);
                    Logger.log("Construction Completed.");
                    System.out.println("Construction Completed...");
                    // activate commandPanel
                    gp.CP.setCommandsForBuilding(this);
                }
                return;
            case State.IN_OPERATION:
                updateProductionQueue();
                return;
        }
    }

    @Override
    public void draw(Graphics g, Camera camera) {
        if (!camera.captures(this)) return;

        // highlight if selected
        if (selected) {
            g.setColor(GameColors.BUILDING_HIGHLIGHT);
            g.drawRect(
                    (int)((x - camera.getX()) * camera.scaleX),
                    (int)((y - camera.getY()) * camera.scaleY),
                    (int)(width * camera.scaleX),
                    (int)(height * camera.scaleY)
            );
        }
    }

    /// PRODUCTION CYCLE ///
    /**
     * Try to build one unit of the given type
     * If we can’t afford it -> do nothing
     */
    public void produce(UnitType type) {
        Cost cost = type.getCost();
        if (!gp.RM.canAfford(cost)) {
            Logger.log("Not enough resources for " + type + "_UNIT");
            System.out.println("Not enough resources for " + type + "_UNIT");
            return;
        }

        // spend resource, queue new unit
        gp.RM.spend(cost);
        enqueueUnit(type);

        Logger.log("Built " + type  + " | Remaining minerals: " + gp.RM.get(ResourceType.MINERAL) +
                ", used supply: " + gp.RM.getUsedSupply() + "/" + gp.RM.getMaxSupply());
        System.out.println("Built " + type  + " | Remaining minerals: " + gp.RM.get(ResourceType.MINERAL) +
                ", used supply: " + gp.RM.getUsedSupply() + "/" + gp.RM.getMaxSupply());
    }

    // Called by CommandActionListener
    public void enqueueUnit(UnitType type) {

        if (productionQueue.size() >= MAX_QUEUE)
            return;

        AbstractUnit unit = null;
        if (type == UnitType.WORKER) {
            unit = new WorkerUnit(map, bm, gp.RM, gp, spawnOriginX, spawnOriginY, (int)GamePanel.TILE_SIZE, (int)GamePanel.TILE_SIZE );
        }
        productionQueue.add(unit);
    }

    public void updateProductionQueue(){
        // If we have units queued, advance cooldown
        if (!productionQueue.isEmpty()) {
            cooldownTimer++;
            if (cooldownTimer >= COOLDOWN_TICKS) {
                spawnUnit(productionQueue.poll());
                cooldownTimer = 0;
            }
        }
    }

    private void spawnUnit(AbstractUnit unit) {

        // find nearest free cell at this moment
        Vector2Int base = GamePanel.convertWorldToCell(spawnOriginX, spawnOriginY);
        Vector2Int free = findNearestFreeCell(base);
        if (free == null) {
            // map totally clogged... drop spawn or requeue
            return;
        }

        // move unit into that free tile
        Vector2 world = GamePanel.convertCellToWorld(free.x, free.y);
        unit.setX(world.x);
        unit.setY(world.y);
        unit.setID(free.x*free.y); // ID must be the index of spawn (might not be unique... fix this)
        unit.setTag("Worker");
        unit.syncHitbox();
        PlayerUnitManager.unitList.add(unit);

        // now mark it occupied
        map.intArr[free.y][free.x] = CombatUnit.TOKEN;
    }

    /// -----------------------------------------

    ///  UTILITY AND HELPER ///
    // Add to tileArr and intArr
    // Check for validity in BuildingManager, not here
    public void generateCommandCenter(Color color) {
        for (int i = 0; i < HEIGHT_TILES; i++) {
            for (int j = 0; j < WIDTH_TILES; j++) {
                map.tileArr[cellPos.y+i][cellPos.x+j] = new Tile(worldPos.x + j*(int)GamePanel.TILE_SIZE,
                                                                worldPos.y + i*(int)GamePanel.TILE_SIZE,
                                                                (int)GamePanel.TILE_SIZE,
                                                                (int)GamePanel.TILE_SIZE,
                                                                color);
                map.intArr[cellPos.y+i][cellPos.x+j] = Barracks.TOKEN;
            }
        }
    }

    // Small BFS helper to find the nearest free cell when spawning
    private Vector2Int findNearestFreeCell(Vector2Int start) {
        int rows = map.intArr.length;
        int cols = map.intArr[0].length;
        boolean[][] seen = new boolean[rows][cols];
        Queue<Vector2Int> q = new LinkedList<>();

        q.add(start);
        seen[start.y][start.x] = true;

        // 4-way neighbors (up, down, left, right)
        int[] dx = { 1, -1,  0,  0 };
        int[] dy = { 0,  0,  1, -1 };

        while (!q.isEmpty()) {
            Vector2Int cur = q.poll();

            // if that cell is free, return it
            if (map.intArr[cur.y][cur.x] == 0) {
                return cur;
            }

            // otherwise enqueue its unvisited neighbors
            for (int i = 0; i < 4; i++) {
                int nx = cur.x + dx[i];
                int ny = cur.y + dy[i];
                if (nx < 0 || ny < 0 || nx >= cols || ny >= rows) continue;
                if (!seen[ny][nx]) {
                    seen[ny][nx] = true;
                    q.add(new Vector2Int(nx, ny));
                }
            }
        }

        // no free cell found
        return null;
    }

    /// -----------------------------------------

    ///  GETTER AND SETTER ///
    // Utility method
    public int getCooldownTimer() { return cooldownTimer; }
    public static int getCooldownTicks() { return COOLDOWN_TICKS; }
    public boolean isProducing() { return !productionQueue.isEmpty(); }
    public int getQueueSize() { return productionQueue.size(); }
    /// -----------------------------------------

    // -----------------------------------
    // pretty printing
    @Override
    public String toString() {
        return "tag : " + tag +" "+ ID + " IsSelected : " + selected;
    }
}
