package Manager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import Faction.Faction;
import GameObjects.*;
import Panel.GamePanel;
import Unit.AbstractUnit;
import Unit.CombatUnit;
import Unit.IControllable;
import Unit.WorkerUnit;
import Util.*;

public class PlayerUnitManager extends UnitManager {

    public static List<AbstractUnit> unitList;
    public static List<AbstractUnit> selectedUnitList;
    public static AbstractUnit quickSelection;

	public List<Tile> tempTileList;
	public List<Vector2Int> tempTileIndex;

	// Constructor
	public PlayerUnitManager(TileMap map) {
        super(map);

		unitList = new ArrayList<AbstractUnit>();
		selectedUnitList = new ArrayList<AbstractUnit>();

		tempTileList = new ArrayList<Tile>();
		tempTileIndex = new ArrayList<Vector2Int>();

		//System.out.println("is map null? "+map.intArr.length);
		//buildUnitSquad();
	}

	@Override
	public void draw(Graphics g, Camera c) {
		for (IEntity unit : unitList) {
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

	// Visualize allowed target in tile map overlay
	public void movementHelper(Vector2Int startPos){
		clearMovementHelper(); // reset
		Vector2 worldPos = GamePanel.convertCellToWorld(startPos.x, startPos.y);
		Color tempColor;
		if (map.intArr[startPos.y][startPos.x] != 0) {
			tempColor = Color.DARK_GRAY;
		}else {
			tempColor = GameColors.MOVEMENT_TILE_HELPER;
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
		        	CombatUnit unit = new CombatUnit(map, ownerFaction, posX, posY, (int)GamePanel.TILE_SIZE, (int)GamePanel.TILE_SIZE);
					int index = GamePanel.COLS*i + j;
					unit.setID(index);
					unit.setTag("Combat");
					unitList.add(unit);
		        }
				if (map.intArr[i][j] == WorkerUnit.TOKEN) {
					WorkerUnit unit = new WorkerUnit(map, ownerFaction, posX, posY, (int)GamePanel.TILE_SIZE, (int)GamePanel.TILE_SIZE);
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

}
