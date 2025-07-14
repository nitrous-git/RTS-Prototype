package Unit.StatePackage;

import Unit.AbstractUnit;

public class HoldPositionState<U extends AbstractUnit> implements IUnitState<U> {

    public HoldPositionState() { }

    @Override
    public void onEnter(U unit) {
        unit.endPathEarly();
    }

    @Override
    public void update(U unit) {
    }

    @Override
    public void onExit(U unit) { }

}
