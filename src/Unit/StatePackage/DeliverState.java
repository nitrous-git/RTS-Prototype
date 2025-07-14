package Unit.StatePackage;

import Resource.ResourceType;
import Unit.WorkerUnit;
import Util.Vector2Int;

public class DeliverState implements IUnitState<WorkerUnit> {

    ResourceType resourceType;
    Vector2Int cellStartPos;

    public DeliverState(ResourceType resourceType, Vector2Int cellStartPos) {
        this.cellStartPos = cellStartPos;
        this.resourceType = resourceType;
    }

    @Override
    public void onEnter(WorkerUnit unit) {
        unit.startDelivery();
    }

    @Override
    public void update(WorkerUnit unit) {
        unit.updateDelivery();
    }

    @Override
    public void onExit(WorkerUnit unit) {
        unit.endDelivery();
    }

}
