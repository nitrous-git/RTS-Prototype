package EventHandler;

import java.awt.event.*;
import java.util.List;
import javax.swing.SwingUtilities;

import Building.AbstractBuilding;
import Command.CommandContext;
import Command.CommandType;
import Manager.ResourceManager;
import Building.BuildingType;
import Unit.AbstractUnit;
import Panel.CommandPanel;
import Panel.GamePanel;
import Manager.BuildingManager;
import Manager.PlayerUnitManager;
import Unit.IControllable;
import Unit.WorkerUnit;
import Util.SelectionBox;
import Util.Vector2Int;


public class MouseEventHandler implements MouseListener, MouseMotionListener {

    public enum Mode { SELECTION, PLACEMENT, MOVE, ATTACK, GATHER, REPAIR }
    private Mode currentMode = Mode.SELECTION;

    private final GamePanel gamePanel;
    private final CommandPanel commandPanel;

    public BuildingType currentBuildingType;

    private final List<SelectionHandler> handlers;

    // Constructor 
    public MouseEventHandler(GamePanel gamePanel, CommandPanel commandPanel) {    
        this.gamePanel = gamePanel;
        this.commandPanel = commandPanel;

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
			gamePanel.SB.startSelection(e.getX(), e.getY());
			gamePanel.repaint();
		}
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    	if (SwingUtilities.isLeftMouseButton(e)) {
    		gamePanel.SB.finishSelection(e.getX(), e.getY());
    		gamePanel.repaint();
    	}
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
        float worldX = cx + gamePanel.camera.getX();
        float worldY = cy + gamePanel.camera.getY();

        // If the unit is selected, move it 
        if (SwingUtilities.isRightMouseButton(e)) {
        	List<AbstractUnit> units = PlayerUnitManager.getSelectedUnitList();
            for (AbstractUnit unit : units) {
                if (unit instanceof IControllable controllable) {
                    CommandContext ctx;
                    //System.out.println(currentMode);
                    switch (currentMode){
                        case SELECTION:
                            spawnQuickBoxSelection(cx, cy);
                            PlayerUnitManager.quickSelection = null;
                            BuildingManager.quickSelection = null;
                            ResourceManager.quickSelection = null;
                            gamePanel.PUM.checkQuickBoxSelection(gamePanel.SB);
                            gamePanel.BM.checkQuickBoxSelection(gamePanel.SB);
                            gamePanel.RM.checkQuickBoxSelection(gamePanel.SB);

                            if (PlayerUnitManager.quickSelection == null
                                && BuildingManager.quickSelection == null
                                && ResourceManager.quickSelection == null) {
                                // normal moveTo single or multiple units
                                ctx = new CommandContext().setDestination(worldX, worldY, gamePanel.camera);
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
                            if (BuildingManager.quickSelection != null){
                                if (unit instanceof WorkerUnit wu) {
                                    Vector2Int cellStartPos = GamePanel.convertWorldToCell(worldX, worldY);
                                    // back to delivery
                                    // back to delivery
                                    ctx = new CommandContext().setDelivery(wu.currentGatherType, cellStartPos);
                                    controllable.issueCommand(CommandType.DELIVER, ctx);
                                    wu.setConstructionBuildingRef(BuildingManager.quickSelection);
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
                            commandPanel.setCommandsForUnit(PlayerUnitManager.getSelectedUnitList());
                            break;
                        case MOVE:
                            //System.out.println("UnitMove : "+ unit.toString());
                            ctx = new CommandContext().setDestination(worldX, worldY, gamePanel.camera);
                            controllable.issueCommand(CommandType.MOVE, ctx);
                            setMode(Mode.SELECTION);
                            commandPanel.setCommandsForUnit(PlayerUnitManager.getSelectedUnitList());
                            gamePanel.PUM.clearMovementHelper();
                            break;
                        case ATTACK: break;
                        case REPAIR:
                            // cleanup
                            setMode(Mode.SELECTION);
                            commandPanel.setCommandsForUnit(PlayerUnitManager.getSelectedUnitList());
                            gamePanel.PUM.clearMovementHelper();
                            break;
                    }
                }
            }
        }


        if (SwingUtilities.isRightMouseButton(e) && currentMode == Mode.PLACEMENT) {
        	// snap to grid by integer division
        	Vector2Int startPos = GamePanel.convertWorldToCell(worldX, worldY);
        	gamePanel.BM.construct(currentBuildingType, startPos);
            gamePanel.BM.clearPlacementHelper();
            setMode(Mode.SELECTION);
            commandPanel.setNoSelectionCommand();
		}

    }
      
    // --- MouseMotionListener interface method --- //

    @Override
    public void mouseDragged(MouseEvent e) {

        // Enforce the unit-over-building-resource policy in the selection box logic
        // just like Starcraft 1
        if (!SwingUtilities.isLeftMouseButton(e)) return;

        SelectionBox sb = gamePanel.SB;
        sb.updateSelection(e.getX(), e.getY());

        for (SelectionHandler h : handlers) {
            if (h.handle(sb)) break;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Vector2Int startPos = GamePanel.convertWorldToCell(e.getX()+gamePanel.camera.getX(), e.getY()+gamePanel.camera.getY());
        switch (currentMode) {
            case MOVE, REPAIR:
                gamePanel.PUM.movementHelper(startPos);
                return;
            case ATTACK:
                return;
            case PLACEMENT:
                gamePanel.BM.placementHelper(startPos, currentBuildingType);
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
                commandPanel.setCommandsForBuilding(b);
            } else {
                commandPanel.setWaitConstructionCommand();
            }
    }

    public void spawnQuickBoxSelection(int cx, int cy){
        gamePanel.SB.startSelection(cx - 8, cy - 8);
        gamePanel.SB.updateSelection(cx + 8, cy + 8);
        gamePanel.repaint();

        new javax.swing.Timer(200, ev -> {
            gamePanel.SB.finishSelection(cx + 1, cy + 1);
            gamePanel.repaint(); })
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
    * This is just the classic Chain-of-Responsibility pattern.
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
            gamePanel.PUM.checkSelection(sb);
            var units = PlayerUnitManager.getSelectedUnitList();
            if (!units.isEmpty()) {
                gamePanel.BM.clearSelectedBuilding();
                gamePanel.RM.clearSelectedResources();
                commandPanel.setCommandsForUnit(units);
                return true;
            }
            return false;
        }
    }

    private class BuildingSelectionHandler implements SelectionHandler {
        @Override
        public boolean handle(SelectionBox sb) {
            gamePanel.BM.checkSelection(sb);
            var b = BuildingManager.getSelectedBuilding();
            if (b != null) {
                gamePanel.RM.clearSelectedResources();
                handleBuildingSelectionBox((AbstractBuilding) b);
                return true;
            }
            return false;
        }
    }


    private class ResourceSelectionHandler implements SelectionHandler {
        @Override
        public boolean handle(SelectionBox sb) {
            gamePanel.RM.checkSelection(sb);
            var nodes = ResourceManager.getSelectedResourceNodeList();
            if (!nodes.isEmpty()) {
                commandPanel.setWaitCommand();
                return true;
            }
            return false;
        }
    }

    private class NoSelectionHandler implements SelectionHandler {
        @Override
        public boolean handle(SelectionBox sb) {
            commandPanel.setNoSelectionCommand();
            return true;
        }
    }

}
