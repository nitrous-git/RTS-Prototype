package Manager;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import Unit.AbstractUnit;
import Unit.CombatUnit;
import Unit.EnemyUnit;
import Panel.GamePanel;
import Util.Camera;
import Util.TileMap;


public class EnemyUnitManager extends UnitManager {

	//public static List<AbstractUnit> unitList;
	//public List<AbstractUnit> selectedUnitList;

	// Constructor
	public EnemyUnitManager(TileMap map, GameContext GC) {
		super(map, GC);
		unitList = new ArrayList<AbstractUnit>();
		//buildUnitSquad();
	}

	@Override
	public void draw(Graphics g, Camera c) {
		for (AbstractUnit unit : unitList) {
			unit.draw(g, c);
		}
	}

	@Override
	public void update() {
//		for (int i = 0; i < unitList.size(); i++) {
//			unitList.get(i).update();
//			if (unitList.get(i).isDead()) {
//				unitList.remove(unitList.get(i));
//			}
//		}

		//  2 pass for the update
		// Read from A -> collect removals into B -> iterate B -> mutate A
		List<AbstractUnit> deadUnits = new ArrayList<>();

		for (AbstractUnit unit : unitList) {
			unit.update();
			if (unit.isDestroyed()) {
				deadUnits.add(unit);
			}
		}

		for (AbstractUnit dead : deadUnits) {
			removeUnit(dead);
		}
	}

	public void removeUnit(AbstractUnit unit) {
		unitList.remove(unit);
		GC.unregisterUnit(unit);
	}



	public void buildUnitSquad() {	
		  float posX = 0;
		  float posY = 0;
		  
		  for (int i = 0; i < GamePanel.ROWS; i++) {
		    for (int j = 0; j < GamePanel.COLS; j++) {
		        if (map.intArr[i][j] == EnemyUnit.TOKEN) {
		        	CombatUnit unit = new CombatUnit(map, ownerFaction, posX, posY, (int)GamePanel.TILE_SIZE, (int)GamePanel.TILE_SIZE);
					int index = GamePanel.COLS*i + j;
					unit.setID(index);
					unit.setTag("Combat");
					//unitList.add(unit);
					addUnit(unit);
		        } 
		        posX += GamePanel.TILE_SIZE;
		    }
		    posY += GamePanel.TILE_SIZE;
		    posX = 0;
		  }
	}

}
