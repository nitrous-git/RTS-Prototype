package Unit.AIComponent.CombatUnitAIState;

import Building.AbstractBuilding;
import Command.CommandType;
import Unit.AIComponent.CombatUnitAIComponent;
import Unit.AIComponent.CombatUnitMission;
import Unit.AIComponent.CombatUnitMissionType;
import Unit.CombatUnit;

public class UnitAttackAIState implements ICombatUnitAIState {

    @Override
    public boolean canRun(CombatUnitAIComponent component) {
        CombatUnit unit = component.getUnit();
        return unit.wasRecentlyDamaged(1200) && unit.getHealthRatio() >= 0.30f;
    }

    @Override
    public int computePriority(CombatUnitAIComponent component) {
        return 150;
    }

    // ICombatUnitAIState FSM Methods
    // ---------------------------------------------------

    @Override
    public void onEnter(CombatUnitAIComponent component) {
        CombatUnit unit = component.getUnit();
        unit.clearPreferredTarget();
        unit.updateUnitSensing();
    }

    @Override
    public void update(CombatUnitAIComponent component) {
        CombatUnit unit = component.getUnit();

        if (unit.getCurrentCommand() != CommandType.ATTACK) {
            unit.updateUnitSensing();
        }
    }

    @Override
    public void onExit(CombatUnitAIComponent component) { }

}
