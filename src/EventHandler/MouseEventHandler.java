package EventHandler;

import java.awt.event.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.SwingUtilities;

import Building.AbstractBuilding;
import Command.CommandContext;
import Command.CommandType;
import Faction.FactionManager;
import Faction.Faction;
import Manager.*;
import Building.BuildingType;
import Unit.AbstractUnit;
import Panel.CommandPanel;
import Panel.GamePanel;
import Unit.IControllable;
import Unit.WorkerUnit;
import Util.Camera;
import Util.SelectionBox;
import Util.Vector2Int;


public class MouseEventHandler implements MouseListener, MouseMotionListener {

    public enum Mode { SELECTION, PLACEMENT, MOVE, ATTACK, GATHER, REPAIR }
    private Mode currentMode = Mode.SELECTION;

    private final GamePanel GP;
    private final CommandPanel CP;
    private final SelectionBox SB;
    private final GameContext GC;
    private Faction playerFaction;
    private Camera camera;
    private PlayerUnitManager PUM;

    public BuildingType currentBuildingType;
    //private List<AbstractBuilding> allBuildingsCache;

    private final List<SelectionHandler> handlers;


    // Constructor 
    public MouseEventHandler(GamePanel GP, CommandPanel CP, SelectionBox SB,
                             GameContext GC,
                             Faction playerFaction,
                             Camera camera) {
        this.GP = GP;
        this.CP = CP;
        this.SB = SB;
        this.GC = GC;
        this.playerFaction = playerFaction;
        this.camera = camera;

        PUM = ((PlayerUnitManager)playerFaction.getUnitManager());

        this.handlers = List.of(
                new UnitSelectionHandler(),
                new BuildingSelectionHandler(),
                new ResourceSelectionHandler(),
                new NoSelectionHandler()
        );
    }

	// --- Event Handler interface method --- //
	@Override
    public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			SB.startSelection(e.getX(), e.getY());
			GP.repaint();
		}

        //allBuildingsCache = FM.getAllBuildings();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    	if (SwingUtilities.isLeftMouseButton(e)) {
    		SB.finishSelection(e.getX(), e.getY());
    		GP.repaint();
    	}

        //allBuildingsCache = null;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // not used here
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // not used here
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int cx = e.getX(), cy = e.getY();
        float worldX = cx + camera.getX();
        float worldY = cy + camera.getY();

        // If the unit is selected, move it 
        if (SwingUtilities.isRightMouseButton(e)) {
        	List<AbstractUnit> units = getFilteredUnitList();
            for (AbstractUnit unit : units) {
                if (unit instanceof IControllable controllable) {
                    CommandContext ctx;
                    //System.out.println(currentMode);
                    switch (currentMode){
                        case SELECTION:
                            spawnQuickBoxSelection(cx, cy);
                            PlayerUnitManager.quickSelection = null;
                            playerFaction.getBuildingManager().quickSelection = null;
                            ResourceManager.quickSelection = null;
                            PUM.checkQuickBoxSelection(SB);
                            playerFaction.getBuildingManager().checkQuickBoxSelection(SB);
                            playerFaction.getResourceManager().checkQuickBoxSelection(SB);

                            if (PlayerUnitManager.quickSelection == null
                                && playerFaction.getBuildingManager().quickSelection == null
                                && ResourceManager.quickSelection == null) {
                                // normal moveTo single or multiple units
                                ctx = new CommandContext().setDestination(worldX, worldY, camera);
                                controllable.issueCommand(CommandType.MOVE, ctx);
                            }
                            if (PlayerUnitManager.quickSelection != null) {
                                if (unit instanceof WorkerUnit wu) {
                                    //System.out.println("unit quick selected");
                                    wu.setRepairUnitRef(PlayerUnitManager.quickSelection);
                                    Vector2Int cellStartPos = GamePanel.convertWorldToCell(worldX, worldY);
                                    ctx = new CommandContext().setRepair(cellStartPos);
                                    controllable.issueCommand(CommandType.REPAIR, ctx);
                                }
                            }
                            if (playerFaction.getBuildingManager().quickSelection != null){
                                if (unit instanceof WorkerUnit wu) {
                                    Vector2Int cellStartPos = GamePanel.convertWorldToCell(worldX, worldY);
                                    // back to delivery
                                    // back to delivery
                                    ctx = new CommandContext().setDelivery(wu.currentGatherType, cellStartPos);
                                    controllable.issueCommand(CommandType.DELIVER, ctx);
                                    wu.setConstructionBuildingRef(playerFaction.getBuildingManager().quickSelection);
                                }
                            }
                            if (ResourceManager.quickSelection != null){
                                if (unit instanceof WorkerUnit wu) {
                                    Vector2Int cellStartPos = GamePanel.convertWorldToCell(worldX, worldY);
                                    ctx = new CommandContext().setGathering(ResourceManager.quickSelection.getType(), cellStartPos);
                                    controllable.issueCommand(CommandType.GATHER, ctx);
                                    wu.setResourceNodeRef(ResourceManager.quickSelection);
                                }
                            }

                            // cleanup
                            setMode(Mode.SELECTION);
                            CP.setCommandsForUnit(units);
                            break;
                        case MOVE:
                            //System.out.println("UnitMove : "+ unit.toString());
                            ctx = new CommandContext().setDestination(worldX, worldY, camera);
                            controllable.issueCommand(CommandType.MOVE, ctx);
                            setMode(Mode.SELECTION);
                            CP.setCommandsForUnit(units);
                            PUM.clearMovementHelper();
                            break;
                        case ATTACK: break;
                        case REPAIR:
                            // cleanup
                            setMode(Mode.SELECTION);
                            CP.setCommandsForUnit(units);
                            PUM.clearMovementHelper();
                            break;
                    }
                }
            }
        }


        if (SwingUtilities.isRightMouseButton(e) && currentMode == Mode.PLACEMENT) {
        	// snap to grid by integer division
        	Vector2Int startPos = GamePanel.convertWorldToCell(worldX, worldY);
        	playerFaction.getBuildingManager().construct(currentBuildingType, startPos);
            playerFaction.getBuildingManager().clearPlacementHelper();
            setMode(Mode.SELECTION);
            CP.setNoSelectionCommand();
		}

    }
      
    // --- MouseMotionListener interface method --- //

    @Override
    public void mouseDragged(MouseEvent e) {

        // Enforce the unit-over-building-resource policy in the selection box logic
        // just like Starcraft 1
        if (!SwingUtilities.isLeftMouseButton(e)) return;

        SB.updateSelection(e.getX(), e.getY());

        for (SelectionHandler h : handlers) {
            if (h.handle(SB)) break;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Vector2Int startPos = GamePanel.convertWorldToCell(e.getX() + camera.getX(), e.getY() + camera.getY());
        switch (currentMode) {
            case MOVE, REPAIR:
                PUM.movementHelper(startPos);
                return;
            case ATTACK:
                return;
            case PLACEMENT:
                playerFaction.getBuildingManager().placementHelper(startPos, currentBuildingType);
                return;
        }

        /*
    	if (gamePanel.BM.getInPlacementMode()) {
        	//Vector2Int startPos = GamePanel.convertWorldToCell(e.getX()+gamePanel.camera.getX(), e.getY()+gamePanel.camera.getY());
        	gamePanel.BM.placementHelper(startPos);	
		}
        */
    }

    // ---- Getter and Setter --- //

    public void setMode(Mode mode) {
        this.currentMode = mode;
    }

    public void setCurrentBuildingType(BuildingType currentBuildingType) {
        this.currentBuildingType = currentBuildingType;
    }

    // ---- Helper and Utility --- //

    public void handleBuildingSelectionBox(AbstractBuilding b){
        if (b != null)
            if (b.currentState == AbstractBuilding.State.IN_OPERATION) {
                CP.setCommandsForBuilding(b);
            } else {
                CP.setWaitConstructionCommand();
            }
    }

    public void spawnQuickBoxSelection(int cx, int cy){
        SB.startSelection(cx - 8, cy - 8);
        SB.updateSelection(cx + 8, cy + 8);
        GP.repaint();

        new javax.swing.Timer(200, ev -> {
            SB.finishSelection(cx + 1, cy + 1);
            GP.repaint(); })
        {{
            setRepeats(false);
            start();
        }};

    }

    public void DoQuickSelection(){

    }



    /*
    * Turning each “priority” into a small handler,
    * and then walking them in order until one “claims” the click.
    * This is just the classic Chain-of-Responsibility design pattern.
    * used by the mouseDragged(MouseEvent e) mouse event method
    * */

    /// ---- inner classes ---- ///

    private interface SelectionHandler {
        /** @return true if this handler performed a selection (and thus stops the chain) */
        boolean handle(SelectionBox sb);
    }

    private class UnitSelectionHandler implements SelectionHandler {
        @Override
        public boolean handle(SelectionBox sb) {
            GC.checkSelection(sb);
            var units = GC.constructSelectedUnitList();

            if (!units.isEmpty()) {
                //playerFaction.getBuildingManager().clearSelectedBuilding();
                GC.clearSelectedBuilding();
                playerFaction.getResourceManager().clearSelectedResources();
                CP.setCommandsForUnit(units);
                return true;
            }
            return false;
        }
    }

    private class BuildingSelectionHandler implements SelectionHandler {
        @Override
        public boolean handle(SelectionBox sb) {
            GC.checkBuildingSelection(sb);
            var b = GC.getSelectedBuilding();
            if (b != null) {
                playerFaction.getResourceManager().clearSelectedResources();
                if (b.getOwnerFaction().getName().equals("Player")) {
                    //System.out.println("PLAYER BUILDING");
                    handleBuildingSelectionBox(b);
                }
                else {
                    //System.out.println("OTHER FACTION BUILDING");
                }
                return true;
            }
            return false;
        }
    }

    private class ResourceSelectionHandler implements SelectionHandler {
        @Override
        public boolean handle(SelectionBox sb) {
            playerFaction.getResourceManager().checkSelection(sb);
            var nodes = playerFaction.getResourceManager().getSelectedResourceNodeList();
            if (!nodes.isEmpty()) {
                CP.setWaitCommand();
                return true;
            }
            return false;
        }
    }

    private class NoSelectionHandler implements SelectionHandler {
        @Override
        public boolean handle(SelectionBox sb) {
            CP.setNoSelectionCommand();
            return true;
        }
    }

    private List<AbstractUnit> getFilteredUnitList(){
          //return GC.constructSelectedUnitList();
        return GC.constructSelectedUnitList()
                .stream()
                .filter(u -> u.getOwnerFaction().getName().equals(playerFaction.getName()))
                .collect(Collectors.toList());
    }

}
