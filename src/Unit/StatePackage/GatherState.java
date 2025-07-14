package Unit.StatePackage;

import Resource.ResourceType;
import Unit.WorkerUnit;
import Util.Vector2Int;

public class GatherState implements IUnitState<WorkerUnit>{

    ResourceType resourceType;
    Vector2Int cellStartPos;

    public GatherState(ResourceType resourceType, Vector2Int cellStartPos) {
        this.cellStartPos = cellStartPos;
        this.resourceType = resourceType;
    }

    @Override
    public void onEnter(WorkerUnit unit) {
        unit.startGather();
    }

    @Override
    public void update(WorkerUnit unit) {
        unit.updateGather();
    }

    @Override
    public void onExit(WorkerUnit unit) {
        unit.endGather();
    }
}
