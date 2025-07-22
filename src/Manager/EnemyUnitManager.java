package Manager;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import Faction.Faction;
import Unit.AbstractUnit;
import Unit.EnemyUnit;
import Panel.GamePanel;
import Util.Camera;
import Util.TileMap;


public class EnemyUnitManager extends UnitManager {

    public static List<AbstractUnit> unitList;

	// Constructor
	public EnemyUnitManager(TileMap map) {
		super(map);
		unitList = new ArrayList<AbstractUnit>();
		buildUnitSquad();
	}

	@Override
	public void draw(Graphics g, Camera c) {
		for (AbstractUnit unit : unitList) {
			unit.draw(g, c);
		}
	}

	@Override
	public void update() {
		for (int i = 0; i < unitList.size(); i++) {
			unitList.get(i).update();
			if (unitList.get(i).isDead()) {
				unitList.remove(unitList.get(i));
			}
		}
	}


	public void buildUnitSquad() {	
		  float posX = 0;
		  float posY = 0;
		  
		  for (int i = 0; i < GamePanel.ROWS; i++) {
		    for (int j = 0; j < GamePanel.COLS; j++) {
		        if (map.intArr[i][j] == EnemyUnit.TOKEN) {
		        	EnemyUnit unit = new EnemyUnit(posX, posY, (int)GamePanel.TILE_SIZE, (int)GamePanel.TILE_SIZE);
					int index = GamePanel.COLS*i + j;
					unit.setID(index);
					unit.setTag("enemy");
					unitList.add(unit);
		        } 
		        posX += GamePanel.TILE_SIZE;
		    }
		    posY += GamePanel.TILE_SIZE;
		    posX = 0;
		  }
	}

}
