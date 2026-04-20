package Unit.AIComponent;

import Unit.AIComponent.CombatUnitAIState.*;
import Unit.CombatUnit;

import java.util.List;

public class CombatUnitAIComponent {

    private final CombatUnit unit;
    private CombatUnitMission currentMission;
    private ICombatUnitAIState currentState;
    private final List<ICombatUnitAIState> states;

    private boolean active;

    public CombatUnitAIComponent(CombatUnit unit) {
        this.unit = unit;
        this.states = List.of(
                new BuildingAttackAIState(),
                new UnitAttackAIState(),
                new RetreatToBaseAIState()
        );
    }

    public void update() {
        if (!active || currentMission == null) { return; }
        ICombatUnitAIState bestState = selectBestState();

        if (bestState != currentState) {
            if (currentState != null) {
                currentState.onExit(this);
            }
            currentState = bestState;
            if (currentState != null) {
                currentState.onEnter(this);
            }
        }

        if (currentState != null) {
            currentState.update(this);
        }
    }

    // Small UtilityAI implementation
    // -------------------------------------------------

    private ICombatUnitAIState selectBestState() {
        ICombatUnitAIState best = null;
        int bestScore = Integer.MIN_VALUE;

        for (ICombatUnitAIState state : states) {
            if (!state.canRun(this)) continue;

            int score = state.computePriority(this);
            if (score > bestScore) {
                bestScore = score;
                best = state;
            }
        }

        return best;
    }

    public void startMission(CombatUnitMission mission) {
        this.currentMission = mission;
        this.active = true;
    }

    public void clearMission() {

        if (currentState != null) {
            currentState.onExit(this);
        }

        currentState = null;
        currentMission = null;
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    public CombatUnit getUnit() {
        return unit;
    }

    public CombatUnitMission getCurrentMission() {
        return currentMission;
    }

}
