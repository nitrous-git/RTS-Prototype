package Panel;
import javax.swing.*;

import Faction.FactionManager;
import Resource.ResourceNodeRepository;
import Util.*;

import java.awt.*;

public class GamePanel extends JPanel {

    // window
    public final static int WIDTH = 1080;
    public final static int HEIGHT = 620;
    public final static float SCALE = 1.0f;
    
    // game grid
    public final static int ROWS = 69;
    public final static int COLS = 155;
    public final static float TILE_SIZE = 13;

    public Camera camera;
    public TileMap map;
    public SelectionBox SB;
    public FactionManager FM;
    public ResourceNodeRepository RNR;

    
    // --- Constructor --- //
    public GamePanel(TileMap map, Camera camera, SelectionBox SB, FactionManager FM, ResourceNodeRepository RNR) {
        setFocusable(true);
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        this.camera = camera;
        this.map = map;
        this.SB = SB;
        this.FM = FM;
        this.RNR = RNR;
    }

    // --- Graphics --- //
  	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw_grid(g);
        draw(g);
    
        if (SB.dragging) {
           SB.drawSelectionBox((Graphics2D) g);
        }
    }

    public void draw(Graphics g) {
        FM.drawAll(g, camera);
        RNR.draw(g, camera);
        Logger.render(g);
    }
    
    public void update() {
        FM.updateAll();
        RNR.update();
    	camera.update();
		repaint();
	}
    
    // generate map -------------------------------------------------------------
    
	public void draw_grid(Graphics g) {
	    for (int i = 0; i < ROWS; i++) {
	        for (int j = 0; j < COLS; j++) {
	          if (map.tileArr[i][j] != null) {
	        	  map.tileArr[i][j].draw(g, camera);
	          }
	          if (map.tileArrOverlay[i][j] != null) {
        		  map.tileArrOverlay[i][j].draw(g, camera);
			  }
			  g.setColor(GameColors.GRID_LINES);
			  g.drawRect((int)(j*TILE_SIZE - camera.getX()), (int)(i*TILE_SIZE - camera.getY()), (int)TILE_SIZE, (int)TILE_SIZE);
	        }
	    }
	}
	
    /**
     * Converts grid cell coordinates (cellX, cellY) to world coordinates.
     */
    public static Vector2 convertCellToWorld(int cellX, int cellY) {
        float worldX = cellX * TILE_SIZE;
        float worldY = cellY * TILE_SIZE;
        return new Vector2(worldX, worldY);
    }

    /**
     * Converts world coordinates (x, y) to grid cell coordinates.
     */
    public static Vector2Int convertWorldToCell(float x, float y) {
        int cellX = (int) Math.floor(x / TILE_SIZE);
        int cellY = (int) Math.floor(y / TILE_SIZE);
        return new Vector2Int(cellX, cellY);
    }

    ///  GETTER AND SETTER ///

    /*
    public SelectionPanel getSP() {
        return SP;
    }

    public void setSP(SelectionPanel SP) {
        this.SP = SP;
    }

    public CommandPanel getCP() {
        return CP;
    }

    public void setCP(CommandPanel CP) {
        this.CP = CP;
    }
    */
}
