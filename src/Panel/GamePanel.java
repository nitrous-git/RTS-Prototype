package Panel;
import javax.swing.*;

import Manager.BuildingManager;
import Manager.EnemyUnitManager;
import Manager.PlayerUnitManager;
import Manager.ResourceManager;
import Util.*;

import java.awt.*;

@SuppressWarnings("serial")
public class GamePanel extends JPanel {

    // window
    public static int WIDTH = 1000;
    public static int HEIGHT = 500;
    public static float SCALE = 1.0f;
    
    // game grid
    public static int ROWS = 69; 
    public static int COLS = 155; 
    public static float TILE_SIZE = 13;
    
    // camera
    public Camera camera;
    public int camera_scaling = 0;
    
    // SelectionBox unit 
    public SelectionBox SB = new SelectionBox(this);
    
    // Managers
    public PlayerUnitManager PUM;
    public EnemyUnitManager EUM;
    public BuildingManager BM;
    public ResourceManager RM;

    public SelectionPanel SP;
    public CommandPanel CP;
    
    // tiled map
    TileMap map = new TileMap();
    
    // --- Constructor --- //
    public GamePanel() {
      setFocusable(true);
      setBackground(Color.BLACK);
      //System.out.println(TILE_SIZE);
      setPreferredSize(new Dimension(WIDTH, HEIGHT));
      
      // camera
      camera = new Camera(0, 0, WIDTH, HEIGHT, this);
      
      // map 
      map.generateTileMap();
      
      // managers
      PUM = new PlayerUnitManager(map, this);
      EUM = new EnemyUnitManager(map);
      BM = new BuildingManager(map, this);
      RM = new ResourceManager(map, this);

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
    	PUM.draw(g, camera);
    	EUM.draw(g, camera);
    	BM.draw(g, camera);
        RM.draw(g, camera);

        Logger.render(g);
    }
    
    public void update() {
    	PUM.update();
    	EUM.update();
    	BM.update();
        RM.update();
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
}
