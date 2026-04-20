package EnemyAI.State;

import Building.AbstractBuilding;
import Command.CommandContext;
import Command.CommandType;
import EnemyAI.AIManager;
import EnemyAI.AIState;
import EnemyAI.AIStateContext;
import Faction.Faction;
import Manager.GameContext;
import Panel.GamePanel;
import Unit.AIComponent.CombatUnitMission;
import Unit.AIComponent.CombatUnitRole;
import Unit.AbstractUnit;
import Unit.CombatUnit;
import Util.PlacementUtil;
import Util.Vector2;
import Util.Vector2Int;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WaveAttackState extends AIState {

    private final GameContext GC;
    private final int unitAmount;

    private boolean complete = false;
    private int dispatchedCount = 0;
    private Faction targetFaction;

    private static final int MAX_BASE_DISTANCE_CELLS = 18;

    public WaveAttackState(AIManager ai, AIStateContext aiStateContext, String name){
        super(ai, name);
        super.timestamp = aiStateContext.getTimestamp();
        this.GC = aiStateContext.getGameContext();
        this.unitAmount = aiStateContext.getUnitAmount();
    }

    @Override
    public void onEnter() {
        System.out.println("Entering wave attack");

        targetFaction = findTargetFaction();
        if (targetFaction == null) {
            System.out.println("WaveAttackState: no valid enemy faction/building found.");
            complete = true;
            return;
        }

        List<CombatUnit> eligible = getEligibleWaveUnits();
        if (eligible.isEmpty()) {
            System.out.println("WaveAttackState: no eligible attacker units available.");
            complete = true;
            return;
        }

        CombatUnitMission mission = CombatUnitMission.waveAttack(targetFaction);

        for (CombatUnit cu : eligible) {
            cu.getAiComponent().startMission(mission);
            dispatchedCount++;
            System.out.println(ai.getAiFaction().getName()  + " dispatched combat unit " + cu.getID() + " on wave attack against faction " + targetFaction.getName());
        }

        System.out.println("WaveAttackState dispatched " + dispatchedCount + " units.");
        complete = true;

    }

    @Override
    public void update() {
        // Intentionally empty.
        // WaveAttackState only assigns the mission.
        // The CombatUnit AI component owns the ongoing behavior.
    }

    @Override
    public void onExit() {
        System.out.println("Exiting wave attack");
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    // ----------------------------------------------

    private Faction findTargetFaction() {
        Vector2Int aiBase = ai.getAiFaction().getSpawnSeed();

        AbstractBuilding bestBuilding = null;
        int bestDistance = Integer.MAX_VALUE;

        for (AbstractBuilding ab : GC.getAllBuildings()) {
            if (ab == null || ab.isDestroyed()) {
                continue;
            }

            if (ab.getOwnerFaction() == null) {
                continue;
            }

            if (ab.getOwnerFaction().getName().equals(ai.getAiFaction().getName())) {
                continue;
            }

            Vector2Int cell = GamePanel.convertWorldToCell(ab.getX(), ab.getY());
            int dist = manhattan(aiBase, cell);

            if (dist < bestDistance) {
                bestDistance = dist;
                bestBuilding = ab;
            }
        }

        return bestBuilding != null ? bestBuilding.getOwnerFaction() : null;
    }


    private List<CombatUnit> getEligibleWaveUnits() {
        List<CombatUnit> closeToBase = new ArrayList<>();
        List<CombatUnit> fallback = new ArrayList<>();

        Vector2Int aiBase = ai.getAiFaction().getSpawnSeed();

        List<AbstractUnit> units = ai.getAiFaction().getUnitManager().getUnitList();

        for (AbstractUnit unit : units) {
            if (!(unit instanceof CombatUnit cu)) {
                continue;
            }

            if (cu.getRole() != CombatUnitRole.ATTACKER) {
                continue;
            }

            if (cu.getAiComponent() == null) {
                continue;
            }

            if (cu.getAiComponent().isActive()) {
                continue;
            }

            int dist = distanceToBase(cu, aiBase);

            if (dist <= MAX_BASE_DISTANCE_CELLS) {
                closeToBase.add(cu);
            } else {
                fallback.add(cu);
            }
        }

        Comparator<CombatUnit> byBaseDistance = Comparator.comparingInt(cu -> distanceToBase(cu, aiBase));

        closeToBase.sort(byBaseDistance);
        fallback.sort(byBaseDistance);

        List<CombatUnit> selected = new ArrayList<>();

        for (CombatUnit cu : closeToBase) {
            if (selected.size() >= unitAmount) {
                break;
            }
            selected.add(cu);
        }

        for (CombatUnit cu : fallback) {
            if (selected.size() >= unitAmount) {
                break;
            }
            selected.add(cu);
        }

        return selected;
    }

    private int distanceToBase(CombatUnit cu, Vector2Int aiBase) {
        Vector2Int unitCell = GamePanel.convertWorldToCell(cu.getX(), cu.getY());
        return manhattan(unitCell, aiBase);
    }

    private int manhattan(Vector2Int a, Vector2Int b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }
}
