package Unit.StatePackage;

import Command.CommandType;
import Unit.CombatUnit;

public class AttackState implements IUnitState<CombatUnit> {

    public AttackState() {
    }

    @Override
    public void onEnter(CombatUnit unit) {

    }

    @Override
    public void update(CombatUnit unit) {
        unit.checkForNewTarget();

        if (unit.getCurrentCommand() != CommandType.ATTACK) {
            return;
        }

        unit.automateShooting();
    }

    @Override
    public void onExit(CombatUnit unit) {

    }
}
