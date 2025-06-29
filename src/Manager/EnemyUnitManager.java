package Manager;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import Unit.EnemyUnit;
import Panel.GamePanel;
import Util.Camera;
import Util.TileMap;


public class EnemyUnitManager {
	
    static final int UNITS_WIDTH = (int)GamePanel.TILE_SIZE;
    static final int UNITS_HEIGHT = (int)GamePanel.TILE_SIZE;
    
    public static List<EnemyUnit> unitList;
    //int maxSquadUnit = 8;
    
    TileMap map;
    
	// Constructor
	public EnemyUnitManager(TileMap map) {
		this.map = map;
		unitList = new ArrayList<EnemyUnit>();
		buildUnitSquad();
	}
	
	public void draw(Graphics g, Camera c) {
		for (EnemyUnit unit : unitList) {
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
	}
    
	public void buildUnitSquad() {	
		  float posX = 0;
		  float posY = 0;
		  
		  for (int i = 0; i < GamePanel.ROWS; i++) {
		    for (int j = 0; j < GamePanel.COLS; j++) {
		        if (map.intArr[i][j] == 8) {
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
	
	/*
	public void buildUnitSquad() {
		int startPosX = 400;
		int startPosY = 80;
		
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < maxSquadUnit/2; j++) {
				EnemyUnit unit = new EnemyUnit(startPosX+j*60, startPosY+i*60, UNITS_WIDTH, UNITS_HEIGHT);
				int index = (maxSquadUnit/2)*i + j;
				unit.setID(index);
				unit.setTag("enemy");
				unitList.add(unit);
			}
		}
	}*/
	
	
	

}
