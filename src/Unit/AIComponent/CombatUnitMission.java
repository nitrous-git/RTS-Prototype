package Unit.AIComponent;

import Faction.Faction;

public class CombatUnitMission {

    private final CombatUnitMissionType type;
    private final Faction targetFaction;

    public CombatUnitMission(CombatUnitMissionType type, Faction targetFaction) {
        this.type = type;
        this.targetFaction = targetFaction;
    }

    public static CombatUnitMission waveAttack(Faction targetFaction){
        return new CombatUnitMission(CombatUnitMissionType.WAVE_ATTACK, targetFaction);
    }

    public Faction getTargetFaction() {
        return targetFaction;
    }

    public CombatUnitMissionType getType() {
        return type;
    }
}
