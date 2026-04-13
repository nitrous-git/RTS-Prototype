package EnemyAI.State;

import Building.AbstractBuilding;
import Command.CommandContext;
import Command.CommandType;
import EnemyAI.AIManager;
import EnemyAI.AIState;
import EnemyAI.AIStateContext;
import Manager.GameContext;
import Panel.GamePanel;
import Unit.AbstractUnit;
import Unit.CombatUnit;
import Util.PlacementUtil;
import Util.Vector2;
import Util.Vector2Int;

import java.util.List;

public class WaveAttackState extends AIState {
    private GameContext GC;
    private AbstractBuilding buildingTarget;
    private Vector2Int buildingTopLeft;
    private int buildingW;
    private int buildingH;
    private Vector2Int attackPlacement;
    private CommandContext ctx;
    private int unitAmount;

    public WaveAttackState(AIManager ai, AIStateContext aiStateContext, String name){
        super(ai, name);
        super.timestamp = aiStateContext.getTimestamp();
        this.GC = aiStateContext.getGameContext();
        this.unitAmount = aiStateContext.getUnitAmount();
        ctx = new CommandContext();
    }

    @Override
    public void onEnter() {
        System.out.println("Entering wave attack");
        for (AbstractBuilding ab : GC.getAllBuildings()){
            //System.out.println("Building found : " + ab.getTag() + ", "+ab.getOwnerFaction().getName());

            // Filter any building which doesn't belong to ownerFaction
            // No reference to specific enemy faction yet, attack first building found
            if ( !ab.getOwnerFaction().getName().equals(ai.getAiFaction().getName()) ) {
                 buildingTarget = ab;
            }
        }
        if (buildingTarget != null){
            buildingTopLeft = GamePanel.convertWorldToCell(buildingTarget.getX(), buildingTarget.getY());
            //System.out.println("Building to attack found at x,y : " + buildingTopLeft.x + ", " + buildingTopLeft.y);
            buildingW = (int) (buildingTarget.getWidth() / GamePanel.TILE_SIZE);
            buildingH = (int) (buildingTarget.getHeight() / GamePanel.TILE_SIZE);
            //System.out.println("Building size are W,H : " + buildingW + ", " + buildingH);

            attackPlacement = PlacementUtil.getPlacementAroundFootprintScoredWithFallback(
                                                                                        ai.getAiFaction().getMap(),
                                                                                        buildingTopLeft,
                                                                                        buildingW,
                                                                                        buildingH,
                                                                                        4,
                                                                                        6,
                                                                                        null, // should have the unit has seeker cell
                                                                                        PlacementUtil.PlacementPolicy.CLOSEST,
                                                                                        0,
                                                                                        0,
                                                                                        0
            );

            //System.out.println("Got Placement at x,y : " + attackPlacement.x + ", " + attackPlacement.y);
            List<AbstractUnit> units = ai.getAiFaction().getUnitManager().getUnitList();
            System.out.println("Units list size : " + units.size() );



            for (int i = 0; i < Math.min(unitAmount, units.size()); i++){
                //System.out.println("Found unit of Tag : "+units.get(i).getTag() + " is instance " + units.get(i).getClass() );

                if (units.get(i) instanceof CombatUnit cu) {
                    //System.out.println("Found unit of Tag : "+units.get(i).getTag());

                    Vector2 attackWorldPos = GamePanel.convertCellToWorld(attackPlacement.x, attackPlacement.y);
                    ctx.setDestination(attackWorldPos.x, attackWorldPos.y, ai.getAiFaction().getController().getCamera());
                    cu.issueCommand(CommandType.MOVE, ctx);
                    System.out.println(ai.getAiFaction().getName() + " moving for attack on " + buildingTarget.getClass().getName());
                }
            }
        }
    }

    @Override
    public void update() {
    }

    @Override
    public void onExit() {

    }

    @Override
    public boolean isComplete() {
        return false;
    }
}
