package Unit.StatePackage;

import Unit.CombatUnit;
import Util.Camera;

public class AttackState implements IUnitState<CombatUnit> {

    public AttackState() {
    }

    @Override
    public void onEnter(CombatUnit unit) {

    }

    @Override
    public void update(CombatUnit unit) {
        unit.automateShooting();
        unit.checkForNewTarget();
    }

    @Override
    public void onExit(CombatUnit unit) {

    }
}
