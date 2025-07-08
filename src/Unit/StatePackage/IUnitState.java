package Unit.StatePackage;

import Unit.WorkerUnit;

public interface IUnitState {
    void onEnter(WorkerUnit unit);
    void update(WorkerUnit unit);
    void onExit(WorkerUnit unit);
}

