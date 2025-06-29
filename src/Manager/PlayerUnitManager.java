package Manager;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import GameObjects.*;
import Panel.GamePanel;
import Unit.AbstractUnit;
import Unit.CombatUnit;
import Util.Camera;
import Util.SelectionBox;
import Util.TileMap;

public class PlayerUnitManager {
	
    static final int UNITS_WIDTH = (int)GamePanel.TILE_SIZE;
    static final int UNITS_HEIGHT = (int)GamePanel.TILE_SIZE;
    
    public static List<AbstractUnit> unitList;
    public static List<AbstractUnit> selectedUnitList;
    
    TileMap map;
    
	// Constructor
	public PlayerUnitManager(TileMap map) {
		this.map = map;
		unitList = new ArrayList<AbstractUnit>();
		selectedUnitList = new ArrayList<AbstractUnit>();
		buildUnitSquad();
	}
	
	public void draw(Graphics g, Camera c) {
		for (IEntity unit : unitList) {
			unit.draw(g, c);
		}
	}
	
	public void update() {
		for (int i = 0; i < unitList.size(); i++) {
			unitList.get(i).update();
			if (unitList.get(i).isDead()) {
				unitList.remove(unitList.get(i));
			}
		}
		//handleCollisions();
	}
	
	public void buildUnitSquad() {	
		  float posX = 0;
		  float posY = 0;
		  
		  for (int i = 0; i < GamePanel.ROWS; i++) {
		    for (int j = 0; j < GamePanel.COLS; j++) {
		        if (map.intArr[i][j] == 9) {
		        	CombatUnit unit = new CombatUnit(map, posX, posY, (int)GamePanel.TILE_SIZE, (int)GamePanel.TILE_SIZE);
					int index = GamePanel.COLS*i + j;
					unit.setID(index);
					unit.setTag("Combat");
					unitList.add(unit);
		        } 
		        posX += GamePanel.TILE_SIZE;
		    }
		    posY += GamePanel.TILE_SIZE;
		    posX = 0;
		  }
	}
	
	// update the unit selected states
	public void checkSelection(SelectionBox SB) {
		for (AbstractUnit unit : unitList) {
			boolean unitSelected;
	        // Check if the unit is inside the selection box
	        unitSelected = SB.intersects(unit.hitbox);
	        unit.setSelected(unitSelected);
	        //if (unitSelected) {
	        //	System.out.println(unit.toString());
			//}
		}
	}
	
	public static List<AbstractUnit> getSelectedUnitList() {
		List<AbstractUnit> su = new ArrayList<>();
		for (AbstractUnit playerUnit : unitList) {
			if (playerUnit.isSelected()) {
				su.add(playerUnit);
			}
		}
		return su;
	}
	
}
