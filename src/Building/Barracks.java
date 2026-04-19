package Building;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.Queue;

import Command.CommandContext;
import Command.CommandType;
import Faction.Faction;
import GameObjects.ITargetable;
import Resource.Cost;
import Resource.ResourceType;
import Unit.UnitType;
import Unit.AbstractUnit;
import Unit.CombatUnit;
import GameObjects.Tile;
import Panel.GamePanel;
import Manager.PlayerUnitManager;
import Util.*;

public class Barracks extends AbstractBuilding {

    ///  DECLARATIONS ///
	public static final int WIDTH_TILES  = 3;
    public static final int HEIGHT_TILES = 5;
    public static final int TOKEN = 2;

    // production queue
    //private static final int MAX_QUEUE      = 5;
    //private static final int COOLDOWN_TICKS = 480; // e.g. 120 updates ~2s at 60fps

    //private final Queue<AbstractUnit> productionQueue = new LinkedList<>();
    //private int cooldownTimer = 0;

    //public static final int CONSTRUCTION_TICKS = 720;
    //private int constructionTimer = 0;

    private float spawnOriginX;
    TileMap map;
    //Faction ownerFaction;
    private float spawnOriginY;
    private Vector2 worldPos;
    private Vector2Int cellPos;
    private Vector2Int rallyPoint;

    // Class Constructor
    public Barracks(TileMap map, Faction ownerFaction, float x, float y) {
		super(x, y, (int)(GamePanel.TILE_SIZE * WIDTH_TILES), (int)(GamePanel.TILE_SIZE * HEIGHT_TILES), ownerFaction);

        TYPE = BuildingType.BARRACKS;

		worldPos = new Vector2(x, y); 
		cellPos = GamePanel.convertWorldToCell(x, y);
		
		//System.out.println("World : " + worldPos.toString()+ " : Cell : " + cellPos.toString());
		
        // drop the unit just below the building
        this.spawnOriginX  = x + (GamePanel.TILE_SIZE * WIDTH_TILES - GamePanel.TILE_SIZE) / 2;
        this.spawnOriginY  = y +  GamePanel.TILE_SIZE * HEIGHT_TILES;

    	setMaxHealth(300.0f);
    	currentHealth = maxHealth; 
        
        this.map = map;
        //this.ownerFaction = ownerFaction;

        this.currentState = State.UNDER_CONSTRUCTION;
        generateBarracks(GameColors.BUILDING_BARRACKS_UNDER_CONSTRUCTION);

        //System.out.println(ownerFaction.getName() + "Faction has instantiate Barracks ID :" + this.getID());
        //System.out.printf("Barracks instantiated at location %d %d%n", cellPos.x, cellPos.y);

        // compute a rally point if faction has AIController (map must be instantiated)
        if (ownerFaction.isAI) {
            this.rallyPoint = PlacementUtil.getPlacementAroundFootprintScoredWithFallback(
                                                                                        map,
                                                                                        cellPos,
                                                                                        WIDTH_TILES,
                                                                                        HEIGHT_TILES,
                                                                                        8,
                                                                                        10,
                                                                                        null,
                                                                                        PlacementUtil.PlacementPolicy.OPEN_THEN_CLOSE,
                                                                                        4,
                                                                                        10,
                                                                                        5
            );
        }
        //computeRallyCell();
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
                    generateBarracks(GameColors.BUILDING_BARRACKS);
                    Logger.log("Construction Completed.");
                    System.out.println("Construction Completed...");

                    if (!ownerFaction.isAI) {
                        // activate commandPanel
                        ownerFaction.getCommandPanel().setCommandsForBuilding(this);
                    }
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
        if (!ownerFaction.getResourceManager().canAfford(cost)) {
            Logger.log("Not enough resources for " + type + "_UNIT");
            System.out.println("Not enough resources for " + type + "_UNIT");
            return;
        }

        // spend resource, queue new unit
        ownerFaction.getResourceManager().spend(cost);
        enqueueUnit(type);

        Logger.log("Built " + type  + " | Remaining minerals: " + ownerFaction.getResourceManager().get(ResourceType.MINERAL) +
                ", used supply: " + ownerFaction.getResourceManager().getUsedSupply() + "/"
                + ownerFaction.getResourceManager().getMaxSupply());

        System.out.println("Built " + type  + " | Remaining minerals: " + ownerFaction.getResourceManager().get(ResourceType.MINERAL) +
                            ", used supply: " + ownerFaction.getResourceManager().getUsedSupply() + "/"
                + ownerFaction.getResourceManager().getMaxSupply());
    }
    
    // Called by CommandActionListener
    public void enqueueUnit(UnitType type) {
    	
        if (productionQueue.size() >= MAX_QUEUE) 
        	return;

        AbstractUnit unit = null;
        if (type == UnitType.COMBAT) {
            unit = new CombatUnit(map, ownerFaction, spawnOriginX, spawnOriginY, (int)GamePanel.TILE_SIZE, (int)GamePanel.TILE_SIZE );
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
        unit.setTag("Combat");
        unit.syncHitbox();
        //System.out.println("Added combat unit in faction : " + ownerFaction.getName() + ", list size : " + ownerFaction.getUnitManager().getUnitList().size());
        ownerFaction.getUnitManager().addUnit(unit);

        // now mark it occupied
        map.intArr[free.y][free.x] = CombatUnit.TOKEN;


        if (rallyPoint != null){
            Vector2Int nextFreeAtRallyPoint = findNearestFreeCell(rallyPoint);
            Vector2 rallyDestination = GamePanel.convertCellToWorld(nextFreeAtRallyPoint.x, nextFreeAtRallyPoint.y);  // might produce null, check that later... ouff
            CommandContext ctx = new CommandContext().setDestination(rallyDestination.x, rallyDestination.y, ownerFaction.getController().getCamera());
            ((CombatUnit) unit).issueCommand(CommandType.MOVE, ctx);
        }
    }

    /// -----------------------------------------

    ///  UTILITY AND HELPER ///
    // Add to tileArr and intArr 
    // Check for validity in BuildingManager, not here 
    public void generateBarracks(Color color) {
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

    static final int[][] OFFSETS = {
            // SOUTH
            { 0,  8}, { 2,  8}, {-2,  8},
            { 0, 12}, { 2, 12}, {-2, 12},

            // NORTH
            { 0, -8}, { 2, -8}, {-2, -8},
            { 0,-12}, { 2,-12}, {-2,-12},

            // EAST
            { 8,  0}, { 8,  2}, { 8, -2},
            {12,  0}, {12,  2}, {12, -2},

            // WEST
            {-8,  0}, {-8,  2}, {-8, -2},
            {-12, 0}, {-12, 2}, {-12,-2},
    };

    // Compute a rally point automatically from a list of offsets candidates
    // fast deterministic approach, not a very heavy calculation
    private Vector2Int computeRallyCell(){
        Vector2Int topLeftCell = GamePanel.convertWorldToCell(this.x, this.y);
        Vector2Int centerCell = new Vector2Int(
                (int) (topLeftCell.x + WIDTH_TILES / 2),
                (int) (topLeftCell.y + HEIGHT_TILES / 2)
        );

        Vector2Int best = null;
        int bestScore = Integer.MIN_VALUE;

        for (int[] off : OFFSETS) {
            //System.out.printf("Testing offset %d, %d%n", centerCell.x + off[0], centerCell.y + off[1]);
            Vector2Int desired = new Vector2Int(centerCell.x + off[0], centerCell.y + off[1]);
            if (!inBounds(desired)) continue;

            Vector2Int free = findNearestFreeCell(desired);
            if (free == null) continue;

            int score = freeCountInRect(free, 2, 2); // 5x5 openness
            if (score >= 20) return free;            // early accept

            if (score > bestScore) {
                bestScore = score;
                best = free;
            }
        }

        Vector2Int fallbackCell = GamePanel.convertWorldToCell(spawnOriginX, spawnOriginY);
        //System.out.println(ownerFaction.getName() + "Faction has compute rally point (" + best.x + ", " + best.y + ") for Baracks ID :" + this.getID());
        //Logger.log(ownerFaction.getName() + "Faction has set rally point to " + best.x + ", " + best.y);
        return (best != null) ? best : findNearestFreeCell(fallbackCell);
    }

    private int freeCountInRect(Vector2Int origin, int size_x, int size_y) {
        int score = 0;
        for (int i = 0; i < size_y; i++) {
            for (int j = 0; j < size_x; j++) {
                Vector2Int cell = new Vector2Int(origin.x + j, origin.y + i);

                // If the rectangle goes out of bounds, is invalid
                if (!inBounds(cell)) {
                    return -1; // "invalid" sentinel value
                }

                // Free = 0, anything else is occupied/blocked
                if (map.intArr[cell.y][cell.x] == 0) {
                    score++;
                }
            }
        }
        return score;
    }

    private boolean inBounds(Vector2Int vec) {
        return vec.x >= 0 && vec.y >= 0 && vec.x < map.column && vec.y < map.row;
    }



    /// -----------------------------------------

    ///  GETTER AND SETTER ///
	// Utility method 
	//public int getCooldownTimer() { return cooldownTimer; }
	//public static int getCooldownTicks() { return COOLDOWN_TICKS; }
    //public int getConstructionTimer() { return constructionTimer; }
    //public static int getConstructionTicks() { return CONSTRUCTION_TICKS; }
	//public boolean isProducing() { return !productionQueue.isEmpty(); }
	//public int getQueueSize() { return productionQueue.size(); }
    /// -----------------------------------------

    // -----------------------------------
    // pretty printing
    @Override
    public String toString() {
        return "tag : " + tag +" "+ ID + " IsSelected : " + selected;
    }
}
