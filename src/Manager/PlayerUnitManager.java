package Manager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import GameObjects.*;
import Panel.GamePanel;
import Unit.AbstractUnit;
import Unit.CombatUnit;
import Unit.IControllable;
import Unit.WorkerUnit;
import Util.*;

public class PlayerUnitManager {
	
    static final int UNITS_WIDTH = (int)GamePanel.TILE_SIZE;
    static final int UNITS_HEIGHT = (int)GamePanel.TILE_SIZE;
    
    public static List<AbstractUnit> unitList;
    public static List<AbstractUnit> selectedUnitList;
    public static AbstractUnit quickSelection;

    TileMap map;
	public List<Tile> tempTileList;
	public List<Vector2Int> tempTileIndex;

	public BuildingManager BM;
	public GamePanel GP;

	// Constructor
	public PlayerUnitManager(TileMap map, GamePanel GP) {
		this.map = map;
		this.GP = GP;
		unitList = new ArrayList<AbstractUnit>();
		selectedUnitList = new ArrayList<AbstractUnit>();
		//buildUnitSquad();

		tempTileList = new ArrayList<Tile>();
		tempTileIndex = new ArrayList<Vector2Int>();
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
	}

	// Visualize allowed target in tile map overlay
	public void movementHelper(Vector2Int startPos){
		clearMovementHelper(); // reset
		Vector2 worldPos = GamePanel.convertCellToWorld(startPos.x, startPos.y);
		Color tempColor;
		if (map.intArr[startPos.y][startPos.x] != 0) {
			tempColor = Color.DARK_GRAY;
		}else {
			tempColor = Color.GREEN;
		}
		map.tileArrOverlay[startPos.y][startPos.x] = new Tile(worldPos.x, worldPos.y, (int)GamePanel.TILE_SIZE, (int)GamePanel.TILE_SIZE, tempColor);
		tempTileIndex.add(new Vector2Int(startPos.x, startPos.y));
	}

	public void clearMovementHelper() {
		List<Vector2Int> indexCopy = List.copyOf(tempTileIndex);
		tempTileIndex.clear();
		for (Vector2Int vec : indexCopy) {
			map.tileArrOverlay[vec.y][vec.x] = null;
		}
	}





	// Build unit squad from intArr
	public void buildUnitSquad() {	
		  float posX = 0;
		  float posY = 0;
		  
		  for (int i = 0; i < GamePanel.ROWS; i++) {
		    for (int j = 0; j < GamePanel.COLS; j++) {
		        if (map.intArr[i][j] == CombatUnit.TOKEN) {
		        	CombatUnit unit = new CombatUnit(map, posX, posY, (int)GamePanel.TILE_SIZE, (int)GamePanel.TILE_SIZE);
					int index = GamePanel.COLS*i + j;
					unit.setID(index);
					unit.setTag("Combat");
					unitList.add(unit);
		        }
				if (map.intArr[i][j] == WorkerUnit.TOKEN) {
					WorkerUnit unit = new WorkerUnit(map, BM, GP, posX, posY, (int)GamePanel.TILE_SIZE, (int)GamePanel.TILE_SIZE);
					int index = GamePanel.COLS*i + j;
					unit.setID(index);
					unit.setTag("Worker");
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
			if (unit instanceof IControllable) {
				IControllable cu = (IControllable)unit;
				boolean unitSelected;
				// Check if the unit is inside the selection box
				unitSelected = SB.intersects(unit.hitbox);
				cu.setSelected(unitSelected);
			}
		}
	}

	public void checkQuickBoxSelection(SelectionBox SB){
		for (AbstractUnit unit : unitList) {
			if (unit instanceof IControllable) {
				IControllable cu = (IControllable)unit;
				if (SB.intersects(unit.hitbox)) {
					quickSelection = (AbstractUnit) cu;
				}
			}
		}
	}

	public static List<AbstractUnit> getSelectedUnitList() {
		List<AbstractUnit> su = new ArrayList<>();
		for (AbstractUnit unit : unitList) {
			if (unit instanceof IControllable) {
				IControllable cu = (IControllable)unit;
				if (cu.isSelected()) {
					su.add(unit);
				}
			}
		}
		selectedUnitList = su;
		return su;
	}

	// set ref to building manager
	public void setBuildingManager(BuildingManager BM) {
		this.BM = BM;
		// we cannot build unit squad in the constructor, because we delay the set of BM
		// ie : BM is null inside the constructor, we'll fix this later...
		// we don't necessarily want to build unit directly at the start of the game
		buildUnitSquad();
	}
}
