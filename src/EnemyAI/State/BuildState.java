package EnemyAI.State;

import Building.BuildingType;
import EnemyAI.AIStateContext;
import EnemyAI.AIManager;
import EnemyAI.AIState;
import Manager.BuildingManager;
import Unit.UnitType;
import Util.Vector2Int;

public class BuildState extends AIState {
    private BuildingType building;
    private boolean started = false;
    private BuildingManager BM;

    public BuildState(AIManager ai, AIStateContext aiStateContext, String name) {
        super(ai, name);
        super.timestamp = aiStateContext.getTimestamp();
        this.building = aiStateContext.getBuildingType();
        this.BM = ai.getAiFaction().getBuildingManager();
    }

    public void onEnter() {
        Vector2Int seed = ai.getAiFaction().getSpawnSeed();
        Vector2Int freeCell = ai.findNearbyBuildLocation(seed, 5, 5, 1);
        BM.construct(building, freeCell);
    }

    public void update() { }

    public void onExit() {
        System.out.println("EXIT : " + name);
    }

    public boolean isComplete() {
        return !started;
    }
}
