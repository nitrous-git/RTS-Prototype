package Unit.AIComponent.CombatUnitAIState;

import Building.AbstractBuilding;
import Command.CommandContext;
import Command.CommandType;
import Panel.GamePanel;
import Unit.AIComponent.CombatUnitAIComponent;
import Unit.AIComponent.CombatUnitRole;
import Unit.CombatUnit;
import Util.PlacementUtil;
import Util.Vector2;
import Util.Vector2Int;

public class RetreatToBaseAIState implements ICombatUnitAIState{


    @Override
    public boolean canRun(CombatUnitAIComponent component) {
        return true;
    }

    @Override
    public int computePriority(CombatUnitAIComponent component) {
        CombatUnit unit = component.getUnit();
        return unit.getHealthRatio() < 0.30f ? 1000 : 0;
    }

    // ICombatUnitAIState FSM Methods
    // ---------------------------------------------------

    @Override
    public void onEnter(CombatUnitAIComponent component) {
        CombatUnit unit = component.getUnit();
        unit.clearPreferredTarget();

        Vector2Int baseCell = unit.getOwnerFaction().getSpawnSeed();
        Vector2Int freeBaseCell = findFreeCellAroundSpawnSeed(unit, baseCell);

        Vector2 worldPos = GamePanel.convertCellToWorld(freeBaseCell.x, freeBaseCell.y);
        CommandContext ctx = new CommandContext();
        ctx.setDestination(worldPos.x, worldPos.y, unit.getOwnerFaction().getController().getCamera());
        unit.issueCommand(CommandType.MOVE, ctx);
    }

    @Override
    public void update(CombatUnitAIComponent component) {

    }

    @Override
    public void onExit(CombatUnitAIComponent component) {

    }


    private Vector2Int findFreeCellAroundSpawnSeed(CombatUnit unit, Vector2Int baseCell) {

        return PlacementUtil.getPlacementAroundFootprintScoredWithFallback(
                unit.getOwnerFaction().getMap(),
                baseCell,
                1,
                1,
                5,
                7,
                null,
                PlacementUtil.PlacementPolicy.CLOSEST,
                0, 0, 0
        );
    }
}
