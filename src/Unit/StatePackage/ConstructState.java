package Unit.StatePackage;

import Resource.BuildingType;
import Unit.WorkerUnit;
import Util.Vector2Int;

public class ConstructState implements IUnitState {
    BuildingType currentBuildingType;
    Vector2Int cellStartPos;

    public ConstructState(BuildingType currentBuildingType, Vector2Int cellStartPos){
        this.cellStartPos = cellStartPos;
        this.currentBuildingType = currentBuildingType;
    }


    @Override
    public void onEnter(WorkerUnit unit) {
        unit.startConstruction();
    }

    @Override
    public void update(WorkerUnit unit) {
        unit.updateConstruction();
    }

    @Override
    public void onExit(WorkerUnit unit) {
        unit.endConstruction();
    }
}
