package Unit.AIComponent.CombatUnitAIState;

import Unit.AIComponent.CombatUnitAIComponent;

public interface ICombatUnitAIState {
    boolean canRun(CombatUnitAIComponent component);
    int computePriority(CombatUnitAIComponent component);
    void onEnter(CombatUnitAIComponent component);
    void update(CombatUnitAIComponent component);
    void onExit(CombatUnitAIComponent component);
}
