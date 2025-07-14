package Unit.StatePackage;

import GameObjects.Entity;
import Unit.AbstractUnit;
import Unit.WorkerUnit;

public interface IUnitState<U extends AbstractUnit> {
    void onEnter(U unit);
    void update(U unit);
    void onExit(U unit);
}

