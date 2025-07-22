package EventHandler;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import Faction.Faction;
import Panel.GamePanel;

public class KeyEventHandler implements KeyListener {
	
    private GamePanel gamePanel;

    // Constructor 
    public KeyEventHandler(GamePanel panel) {
        this.gamePanel = panel;
    }

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
			// move pressed
			case KeyEvent.VK_LEFT:
				gamePanel.camera.setLeft(true);
				break;
			case KeyEvent.VK_RIGHT:
				gamePanel.camera.setRight(true);
				break;
			case KeyEvent.VK_UP:
				gamePanel.camera.setUp(true);
				break;
			case KeyEvent.VK_DOWN:
				gamePanel.camera.setDown(true);
				break;
			/*
			case KeyEvent.VK_1:
				if (!playerFaction.BM.getInPlacementMode()) {
					gamePanel.BM.setInPlacementMode(true);
				}else {
					gamePanel.BM.setInPlacementMode(false);
					gamePanel.BM.clearPlacementHelper();
				}
				System.out.println("In Placement Mode : " + gamePanel.BM.getInPlacementMode());
				break;	

			// zoom in, zoom out 
			case KeyEvent.VK_1:
				  gamePanel.camera_scaling += 5;
				  gamePanel.camera.setScale(GamePanel.WIDTH+gamePanel.camera_scaling, GamePanel.HEIGHT+gamePanel.camera_scaling, gamePanel);
				break;
			case KeyEvent.VK_2:
				   gamePanel.camera_scaling -= 5;
				   gamePanel.camera.setScale(GamePanel.WIDTH+gamePanel.camera_scaling, GamePanel.HEIGHT+gamePanel.camera_scaling, gamePanel);
				break;
			*/
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()) {
			// move release
			case KeyEvent.VK_LEFT:
				gamePanel.camera.setLeft(false);
				break;
			case KeyEvent.VK_RIGHT:
				gamePanel.camera.setRight(false);
				break;
			case KeyEvent.VK_UP:
				gamePanel.camera.setUp(false);
				break;
			case KeyEvent.VK_DOWN:
				gamePanel.camera.setDown(false);
				break;
		}
	}
	
	
	@Override
	public void keyTyped(KeyEvent e) { }


}
