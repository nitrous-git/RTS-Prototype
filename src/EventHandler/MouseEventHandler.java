package EventHandler;

import java.awt.event.*;
import java.util.List;
import javax.swing.SwingUtilities;

import Building.AbstractBuilding;
import Command.CommandContext;
import Command.CommandType;
import Resource.BuildingType;
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

    // Constructor 
    public MouseEventHandler(GamePanel gamePanel, CommandPanel commandPanel) {    
        this.gamePanel = gamePanel;
        this.commandPanel = commandPanel;
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
        	List<AbstractUnit> units = PlayerUnitManager.unitList;
            for (AbstractUnit unit : units) {
                if (unit instanceof IControllable controllable && controllable.isSelected()) {
                    CommandContext ctx;
                    //System.out.println(currentMode);
                    switch (currentMode){
                        case SELECTION:
                            spawnQuickBoxSelection(cx, cy);
                            PlayerUnitManager.quickSelection = null;
                            BuildingManager.quickSelection = null;
                            gamePanel.PUM.checkQuickBoxSelection(gamePanel.SB);
                            gamePanel.BM.checkQuickBoxSelection(gamePanel.SB);

                            if (PlayerUnitManager.quickSelection == null && BuildingManager.quickSelection == null ) {
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
                                    ctx = new CommandContext().setConstruction(currentBuildingType, cellStartPos);
                                    controllable.issueCommand(CommandType.CONSTRUCT, ctx);
                                    wu.setConstructionBuildingRef(BuildingManager.quickSelection);
                                }
                            }

                            // cleanup
                            setMode(Mode.SELECTION);
                            commandPanel.setCommandsForUnit(PlayerUnitManager.getSelectedUnitList());
                            return;
                        case MOVE:
                            ctx = new CommandContext().setDestination(worldX, worldY, gamePanel.camera);
                            controllable.issueCommand(CommandType.MOVE, ctx);
                            setMode(Mode.SELECTION);
                            commandPanel.setCommandsForUnit(PlayerUnitManager.getSelectedUnitList());
                            gamePanel.PUM.clearMovementHelper();
                            return;
                        case ATTACK: return;
                        case PLACEMENT:
                            Vector2Int cellStartPos = GamePanel.convertWorldToCell(worldX, worldY);
                            ctx = new CommandContext().setConstruction(currentBuildingType, cellStartPos);
                            controllable.issueCommand(CommandType.CONSTRUCT, ctx);
                            // reset back to selection mode
                            //commandPanel.setCommandsForUnit(PlayerUnitManager.getSelectedUnitList());
                            gamePanel.BM.clearPlacementHelper();
                            setMode(Mode.SELECTION);
                            // wait after construction for gamePanel.BM.clearPlacementHelper(); inside WorkerUnit class
                            return;
                        case REPAIR:

                            // cleanup
                            setMode(Mode.SELECTION);
                            commandPanel.setCommandsForUnit(PlayerUnitManager.getSelectedUnitList());
                            gamePanel.PUM.clearMovementHelper();
                            return;
                    }
                }
            }
        }

        /*
        if (SwingUtilities.isLeftMouseButton(e) && gamePanel.BM.getInPlacementMode()) {
        	// snap to grid by integer division
        	Vector2Int startPos = GamePanel.convertWorldToCell(worldX, worldY);
        	gamePanel.BM.construct(BuildingType.BARRACKS, startPos);
		}
        */
    }
      
    // --- MouseMotionListener interface method --- //

    @Override
    public void mouseDragged(MouseEvent e) {
    	if (SwingUtilities.isLeftMouseButton(e)) {
    		// Enforce the unit-over-building policy in the selection box logic
    		// just like Starcraft 1
    		gamePanel.SB.updateSelection(e.getX(), e.getY());
    		gamePanel.PUM.checkSelection(gamePanel.SB);
            if (!PlayerUnitManager.getSelectedUnitList().isEmpty()){
                // keep unit selection, set commands
                commandPanel.setCommandsForUnit(PlayerUnitManager.getSelectedUnitList());
            }

			if (PlayerUnitManager.getSelectedUnitList().isEmpty()) {
                // keep building selection, set commands
                gamePanel.BM.checkSelection(gamePanel.SB);
                // Building are always single
                AbstractBuilding b = (AbstractBuilding) BuildingManager.getSelectedBuilding();
                handleBuildingSelectionBox(b);
			}

			if (!PlayerUnitManager.getSelectedUnitList().isEmpty() && BuildingManager.getSelectedBuilding() != null) {
                // clear building, prioritize units
				gamePanel.BM.clearSelectedBuilding();
			}
			 
			gamePanel.repaint();
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
                gamePanel.BM.placementHelper(startPos);
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
                commandPanel.setWaitCommand();
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


}
