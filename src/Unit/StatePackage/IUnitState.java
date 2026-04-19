package Unit.StatePackage;

import Unit.AbstractUnit;

public interface IUnitState<U extends AbstractUnit> {
    void onEnter(U unit);
    void update(U unit);
    void onExit(U unit);
}
