package EventHandler;

import java.awt.event.*;
import java.util.List;
import javax.swing.SwingUtilities;

import Unit.AbstractUnit;
import Unit.CombatUnit;
import Panel.CommandPanel;
import Panel.GamePanel;
import Manager.BuildingManager;
import Manager.PlayerUnitManager;
import Util.Vector2Int;


public class MouseEventHandler implements MouseListener, MouseMotionListener {
	
    private GamePanel gamePanel;
    private CommandPanel commandPanel;

    // Constructor 
    public MouseEventHandler(GamePanel gamePanel, CommandPanel commandPanel) {    
        this.gamePanel = gamePanel;
        this.commandPanel = commandPanel;
    }

	// --- Event Handler --- //
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
        // If the unit is selected, move it 
        if (SwingUtilities.isRightMouseButton(e)) {
        	List<AbstractUnit> units = PlayerUnitManager.unitList;
        	for (AbstractUnit unit : units) {
                // we only have combatUnit for now
                CombatUnit combatUnit = (CombatUnit)unit;
                if (combatUnit.isSelected()) {
                    combatUnit.moveTo(e.getX()+gamePanel.camera.getX(), e.getY()+gamePanel.camera.getY(), gamePanel.camera);
                    gamePanel.repaint();
                }
			} 
        }
        
        if (SwingUtilities.isLeftMouseButton(e) && gamePanel.BM.getInPlacementMode()) {
        	// snap to grid
        	Vector2Int startPos = GamePanel.convertWorldToCell(e.getX()+gamePanel.camera.getX(), e.getY()+gamePanel.camera.getY());
        	gamePanel.BM.addBarracks(startPos);
		}
        
    }
      
    // --- MouseMotionListener methods ---

    @Override
    public void mouseDragged(MouseEvent e) {
    	if (SwingUtilities.isLeftMouseButton(e)) {
    		// Enforce the unit-over-building policy in the selection box logic
    		// just like Starcraft 1 
    		gamePanel.SB.updateSelection(e.getX(), e.getY());
    		gamePanel.PUM.checkSelection(gamePanel.SB); 

			if (PlayerUnitManager.getSelectedUnitList().isEmpty()) {
				gamePanel.BM.checkSelection(gamePanel.SB);
				commandPanel.setCommandsFor(BuildingManager.getSelectedBuilding());
			}
			if (!PlayerUnitManager.getSelectedUnitList().isEmpty() 
					 && BuildingManager.getSelectedBuilding() != null) { 
				gamePanel.BM.clearSelectedBuilding();
				commandPanel.setCommandsFor(BuildingManager.getSelectedBuilding());
			}
			 
			gamePanel.repaint();
    	}
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    	if (gamePanel.BM.getInPlacementMode()) {
        	Vector2Int startPos = GamePanel.convertWorldToCell(e.getX()+gamePanel.camera.getX(), e.getY()+gamePanel.camera.getY());
        	gamePanel.BM.placementHelper(startPos);	
		}
    }
}
