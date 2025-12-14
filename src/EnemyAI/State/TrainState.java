package EnemyAI.State;

import Building.AbstractBuilding;
import Building.Barracks;
import Building.BuildingType;
import Building.CommandCenter;
import EnemyAI.AIManager;
import EnemyAI.AIState;
import EnemyAI.AIStateContext;
import Manager.BuildingManager;
import Unit.UnitType;

public class TrainState extends AIState {

    private UnitType unitType;
    private BuildingManager BM;

    public TrainState(AIManager ai, AIStateContext aiStateContext, String name) {
        super(ai, name);
        super.timestamp = aiStateContext.getTimestamp();
        this.unitType = aiStateContext.getUnitType();
        this.BM = ai.getAiFaction().getBuildingManager();
    }


    @Override
    public void onEnter() {
        for (int i = 0; i < BM.buildingList.size(); i++) {
            // Produce worker unit from the first available command center
            if (BM.buildingList.get(i) instanceof CommandCenter &&
                BM.buildingList.get(i).productionQueue.size() < AbstractBuilding.MAX_QUEUE) {
                ((CommandCenter) BM.buildingList.get(i)).produce(UnitType.WORKER);
                break;
            }
        }
    }

    @Override
    public void update() {

    }

    @Override
    public void onExit() {
        System.out.println("EXIT : " + name);
    }

    @Override
    public boolean isComplete() {
        return false;
    }
}
