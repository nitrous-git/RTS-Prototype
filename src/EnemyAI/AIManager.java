package EnemyAI;

import Building.BuildingType;
import EnemyAI.State.BuildState;
import EnemyAI.State.TrainState;
import EnemyAI.State.WaveAttackState;
import Faction.Faction;
import Unit.UnitType;
import Util.TileMap;
import Util.Vector2Int;

import java.util.LinkedList;
import java.util.Queue;

public class AIManager {
    private Queue<AIState> stateQueue = new LinkedList<>();
    private AIState currentState = null;
    private Faction aiFaction;
    private AIStateContext aiStateContext;
    private TileMap map;
    private long startTime;     // Time at instantiation, in milliseconds
    private long elapsed;      // Elapsed time since start, in milliseconds

    public AIManager(Faction aiFaction) {
        this.aiFaction = aiFaction;
        aiStateContext = new AIStateContext();
        this.startTime = System.currentTimeMillis();
        this.map = aiFaction.getMap();
        scheduleActions();   // Comment this line out for debug
    }

    public void update() {
        elapsed = System.currentTimeMillis() - startTime;

        // Trigger new state if a scheduled action is ready
        while (!stateQueue.isEmpty() && elapsed >= stateQueue.peek().timestamp) {
            System.out.println("AIManager elapsed : " + elapsed);

            currentState = stateQueue.poll();
            currentState.onEnter();
        }

        if (currentState != null) {
            currentState.update();
            if (currentState.isComplete()) {
                currentState.onExit();
                currentState = null; // Wait for next scheduled action
            }
        }
    }

    private void scheduleActions() {
        float timeFactor = 1000f;
        ////aiStateContext.setBuildContext(0f*timeFactor, BuildingType.COMMAND_CENTER);
        ////stateQueue.add( new BuildState(this, aiStateContext, "buildCommandCenter"));
        //currentState = stateQueue.poll();
        //System.out.println("stateQueue peek at NAME : " + currentState.name);
        //System.out.println("currentState timestamp : " + currentState.timestamp);

        ////aiStateContext.setTrainUnitContext(10f*timeFactor, UnitType.WORKER);
        ////stateQueue.add( new TrainState(this, aiStateContext, "trainWorkerUnit"));
        ////aiStateContext.setTrainUnitContext(11f*timeFactor, UnitType.WORKER);
        ////stateQueue.add( new TrainState(this, aiStateContext, "trainWorkerUnit"));
        ////aiStateContext.setTrainUnitContext(12f*timeFactor, UnitType.WORKER);
        ////stateQueue.add( new TrainState(this, aiStateContext, "trainWorkerUnit"));
        //currentState = stateQueue.poll();
        //System.out.println("stateQueue peek at NAME : " + currentState.name);
        //System.out.println("currentState timestamp : " + currentState.timestamp);

        //aiStateContext.setTrainUnitContext(100f, UnitType.WORKER));
        //stateQueue.add( new TrainState(this, aiStateContext));


        ////aiStateContext.setBuildContext(15f*timeFactor, BuildingType.SUPPLY_DEPOT);
        //System.out.println("Building Type : "+  aiStateContext.getBuildingType() + ", Timestamp : " + aiStateContext.getTimestamp());
        ////stateQueue.add( new BuildState(this, aiStateContext, "buildSupplyDepot"));
        //currentState = stateQueue.poll();
        //System.out.println("currentState NAME : " + currentState.name);
        //System.out.println("currentState timestamp : " + currentState.timestamp);


        aiStateContext.setBuildContext(0f*timeFactor, BuildingType.BARRACKS);
        //System.out.println("Building Type : "+  aiStateContext.getBuildingType() + ", Timestamp : " + aiStateContext.getTimestamp());
        stateQueue.add( new BuildState(this, aiStateContext, "buildBarracks"));
        //currentState = stateQueue.poll();
        //System.out.println("currentState NAME : " + currentState.name);
        //System.out.println("currentState timestamp : " + currentState.timestamp);

        aiStateContext.setTrainUnitContext(10f*timeFactor, UnitType.COMBAT); // 40
        stateQueue.add( new TrainState(this, aiStateContext, "trainCombatUnit"));
        aiStateContext.setTrainUnitContext(11f*timeFactor, UnitType.COMBAT); // 42
        stateQueue.add( new TrainState(this, aiStateContext, "trainCombatUnit"));
        aiStateContext.setTrainUnitContext(13f*timeFactor, UnitType.COMBAT); // 44
        stateQueue.add( new TrainState(this, aiStateContext, "trainCombatUnit"));

        aiStateContext.setWaveAttackContext(15f*timeFactor, aiFaction.getBuildingManager().getGameContext(), 2);  // 25
        stateQueue.add( new WaveAttackState(this, aiStateContext, "sendWaveAttack"));

    }

    public Vector2Int findNearbyBuildLocation(Vector2Int seed, int w, int h, int padding) {
        boolean[][] visited = new boolean[map.row][map.column];
        Queue<Vector2Int> queue = new LinkedList<>();
        queue.add(seed);
        visited[seed.y][seed.x] = true;

        // 4-direction BFS
        int[] dx = { 1, -1,  0,  0 };
        int[] dy = { 0,  0,  1, -1 };

        while (!queue.isEmpty()) {
            Vector2Int current = queue.poll();

            if (canPlaceBuilding(current.x, current.y, w, h, padding)) {
                return current;
            }

            for (int i = 0; i < 4; i++) {
                int nx = current.x + dx[i];
                int ny = current.y + dy[i];

                if (inBounds(nx, ny) && !visited[ny][nx]) {
                    visited[ny][nx] = true;
                    queue.add(new Vector2Int(nx, ny));
                }
            }
        }

        // fallback if nothing found
        return seed;
    }

    public boolean canPlaceBuilding(int x, int y, int w, int h, int padding) {
        for (int dy = -padding; dy < h + padding; dy++) {
            for (int dx = -padding; dx < w + padding; dx++) {
                int tx = x + dx;
                int ty = y + dy;
                if (!inBounds(tx, ty)) return false;
                if (map.intArr[ty][tx] != 0) return false;
            }
        }
        return true;
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < map.column && y < map.row;
    }

    public Faction getAiFaction() { return aiFaction; }
}
