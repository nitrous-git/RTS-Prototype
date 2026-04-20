package Unit.AIComponent.CombatUnitAIState;

import Building.AbstractBuilding;
import Building.CommandCenter;
import Command.CommandContext;
import Command.CommandType;
import Faction.Faction;
import Panel.GamePanel;
import Unit.AIComponent.CombatUnitAIComponent;
import Unit.AIComponent.CombatUnitMission;
import Unit.AIComponent.CombatUnitMissionType;
import Unit.AIComponent.CombatUnitRole;
import Unit.CombatUnit;
import Util.PlacementUtil;
import Util.Vector2;
import Util.Vector2Int;

public class BuildingAttackAIState implements ICombatUnitAIState{

    public enum BuildingAttackPhase {
        ACQUIRE,
        MOVE_TO,
        ENGAGE
    }

    private AbstractBuilding currentBuildingTarget;
    private BuildingAttackPhase phase = BuildingAttackPhase.ACQUIRE;

    @Override
    public boolean canRun(CombatUnitAIComponent component) {
        CombatUnitMission mission = component.getCurrentMission();
        return mission != null && mission.getType() == CombatUnitMissionType.WAVE_ATTACK;
    }

    @Override
    public int computePriority(CombatUnitAIComponent component) {
        CombatUnit unit = component.getUnit();

        int score = 200;
        if (unit.wasRecentlyDamaged(1200)){ score -= 100; }
        if (unit.getHealthRatio() < 0.3f){ score -= 500; }
        if (!hasAnyValidBuildingTarget(component)){ score -= 1000; }

        return score;
    }

    // ICombatUnitAIState FSM Methods
    // ---------------------------------------------------

    @Override
    public void onEnter(CombatUnitAIComponent component) {
        phase = BuildingAttackPhase.ACQUIRE;
        currentBuildingTarget = null;
    }

    @Override
    public void update(CombatUnitAIComponent component) {
        CombatUnit unit = component.getUnit();

        switch (phase){
            case ACQUIRE -> {
                currentBuildingTarget = findNextBuildingTarget(component);

                if (currentBuildingTarget == null){
                    unit.clearPreferredTarget();
                    return;
                }

                Vector2Int attackCell = findAttackCellAroundBuilding(unit, currentBuildingTarget);
                if (attackCell == null){
                    currentBuildingTarget = null;
                    return;
                }

                Vector2 worldPos = GamePanel.convertCellToWorld(attackCell.x, attackCell.y);
                CommandContext ctx = new CommandContext();
                ctx.setDestination(worldPos.x, worldPos.y, unit.getOwnerFaction().getController().getCamera());
                unit.issueCommand(CommandType.MOVE, ctx);

                phase = BuildingAttackPhase.MOVE_TO;
            }
            case MOVE_TO -> {
                if (currentBuildingTarget == null || currentBuildingTarget.isDestroyed()) {
                    unit.clearPreferredTarget();
                    currentBuildingTarget = null;
                    phase = BuildingAttackPhase.ACQUIRE;
                    return;
                }

                unit.setPreferredTarget(currentBuildingTarget);

                if (unit.getCurrentCommand() == CommandType.IDLE) {
                    phase = BuildingAttackPhase.ENGAGE;
                }
            }
            case ENGAGE -> {
                if (currentBuildingTarget == null || currentBuildingTarget.isDestroyed()) {
                    unit.clearPreferredTarget();
                    currentBuildingTarget = null;
                    phase = BuildingAttackPhase.ACQUIRE;
                    return;
                }

                unit.setPreferredTarget(currentBuildingTarget);

                if (unit.getCurrentCommand() != CommandType.ATTACK) {
                    unit.updateUnitSensing();
                }
            }
        }
    }

    @Override
    public void onExit(CombatUnitAIComponent component) {
        // we do not necessarily clear preferred target here
        // only clear if we want hard disengage on transition
    }

    // Helpers methods
    // ----------------------------------------

    private boolean hasAnyValidBuildingTarget(CombatUnitAIComponent component) {
        return findNextBuildingTarget(component) != null;
    }

    private AbstractBuilding findNextBuildingTarget(CombatUnitAIComponent component){
        CombatUnit unit = component.getUnit();
        Faction targetFaction = component.getCurrentMission().getTargetFaction();

        if (targetFaction == null){ return null; }

        AbstractBuilding best = null;
        float bestDistance = Float.MAX_VALUE;

        for (AbstractBuilding building : targetFaction.getBuildingManager().getBuildingList()) {
            if (building == null || building.isDestroyed()){ continue; }

            float d = unit.calculateDistance(building.getX(), building.getY());
            if (d < bestDistance) {
                bestDistance = d;
                best = building;
            }
        }

        return best;
    }

    private Vector2Int findAttackCellAroundBuilding(CombatUnit unit, AbstractBuilding building) {
        Vector2Int topLeft = GamePanel.convertWorldToCell(building.getX(), building.getY());
        int buildingW = Math.max(1, (int)(building.getWidth() / GamePanel.TILE_SIZE));
        int buildingH = Math.max(1, (int)(building.getHeight() / GamePanel.TILE_SIZE));

        return PlacementUtil.getPlacementAroundFootprintScoredWithFallback(
                unit.getOwnerFaction().getMap(),
                topLeft,
                buildingW,
                buildingH,
                4,
                6,
                null,
                PlacementUtil.PlacementPolicy.CLOSEST,
                0, 0, 0
        );
    }

}
