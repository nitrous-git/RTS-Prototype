package Unit.StatePackage;

import Unit.AbstractUnit;
import Unit.WorkerUnit;
/*
* Generalize IdleState over any AbstractUnit
* */
public class IdleState<U extends AbstractUnit> implements IUnitState<U> {


    public IdleState() { }

    @Override
    public void onEnter(U unit) {
    }

    @Override
    public void update(U unit) {
        unit.updateSensing();
    }

    @Override
    public void onExit(U unit) { }
}
