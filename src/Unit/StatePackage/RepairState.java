package Unit.StatePackage;

import Resource.BuildingType;
import Unit.WorkerUnit;
import Util.Vector2Int;

public class RepairState implements IUnitState {
    Vector2Int cellStartPos;

    public RepairState(Vector2Int cellStartPos){
        this.cellStartPos = cellStartPos;
    }

    @Override
    public void onEnter(WorkerUnit unit) {
        unit.startRepair();
    }

    @Override
    public void update(WorkerUnit unit) {
        unit.updateRepair();
    }

    @Override
    public void onExit(WorkerUnit unit) {
        unit.endRepair();
    }
}
