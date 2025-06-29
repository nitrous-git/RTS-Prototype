package Manager;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import Building.AbstractBuilding;
import Building.Barracks;
import GameObjects.IEntity;
import GameObjects.Tile;
import Panel.GamePanel;
import Util.Camera;
import Util.SelectionBox;
import Util.TileMap;
import Util.Vector2;
import Util.Vector2Int;

public class BuildingManager {

	public boolean inPlacementMode;

    public static List<Barracks> buildingList;
    //public static List<Barracks> selectedBuildingList;
    public static IEntity selectedBuilding; // only one selected building at the time
    TileMap map;
    
    public List<Tile> tempTileList;
    public List<Vector2Int> tempTileIndex;
	
	// Constructor
	public BuildingManager(TileMap map) {
		this.map = map;
		buildingList = new ArrayList<Barracks>();
		//selectedBuildingList = new ArrayList<Barracks>();
		tempTileList = new ArrayList<Tile>();
		tempTileIndex = new ArrayList<Vector2Int>();
		
		inPlacementMode = false;
	}
	
	public void draw(Graphics g, Camera c) {
		for (Barracks build : buildingList) {
			build.draw(g, c);
		}
	}
	
	public void update() {
		for (int i = 0; i < buildingList.size(); i++) {
			buildingList.get(i).update();
			if (buildingList.get(i).isDestroyed()) {
				buildingList.remove(buildingList.get(i));
			}
		}
		//handleCollisions();
	}
	
    public void addBarracks(Vector2Int startPos) {
    	// convert back to world size after snap
    	Vector2 startf = GamePanel.convertCellToWorld(startPos.x, startPos.y);
    	if (isValidPlacement(startPos, Barracks.WIDTH_TILES, Barracks.HEIGHT_TILES)) {
    		Barracks barracks = new Barracks(map, startf.x, startf.y);
    		barracks.setTag("Barracks");
    		barracks.setID(startPos.x*startPos.y); // ID is the index of start point inside the grid
    		buildingList.add( barracks ); 
		}
    }
	
	public boolean isValidPlacement(Vector2Int startPos, int tileWidth, int tileHeight) {	
		boolean isValid = true;
		for (int i = startPos.y; i < startPos.y+tileHeight; i++) {
			for (int j = startPos.x; j < startPos.x+tileWidth; j++) {
				if (map.intArr[i][j] != 0) {
					//System.out.println("Invalid Placement");
					isValid = false;
				}
			}
		}
		//System.out.println(isValid);
		//map.printer();
		return isValid;
	}
	
	// visualize allowed placement on mouseMoved event
	// we don't have the size of the building, call it in a wrapper
	// and check which type of building were constructing
	public void placementHelper(Vector2Int startPos){	
		clearPlacementHelper();
		Vector2 worldPos = GamePanel.convertCellToWorld(startPos.x, startPos.y);
		Color tempColor;
		
    	for (int i = 0; i < Barracks.HEIGHT_TILES; i++) {
		    for (int j = 0; j < Barracks.WIDTH_TILES; j++) {
		    	
		    	if (map.intArr[startPos.y+i][startPos.x+j] != 0) {
		    		tempColor = Color.DARK_GRAY;
		    	}else {
		    		tempColor = Color.GREEN;
				}
		    	map.tileArrOverlay[startPos.y+i][startPos.x+j] = new Tile(worldPos.x + j*(int)GamePanel.TILE_SIZE, 
																worldPos.y + i*(int)GamePanel.TILE_SIZE, 
																(int)GamePanel.TILE_SIZE, 
																(int)GamePanel.TILE_SIZE, 
																tempColor);	
		    	tempTileIndex.add(new Vector2Int(startPos.x+j, startPos.y+i));
		    }
    	}
		
	}
	
	public void clearPlacementHelper() {
		List<Vector2Int> indexCopy = List.copyOf(tempTileIndex);
		tempTileIndex.clear();
		for (Vector2Int vec : indexCopy) {
			map.tileArrOverlay[vec.y][vec.x] = null; 
		}
	}

	// update the unit selected states
	public void checkSelection(SelectionBox SB) {
	    // Clear all previous selections
	    for (AbstractBuilding b : buildingList) {
	        b.setSelected(false);
	    }

	    // Gather all buildings whose hitbox overlaps the selection
	    List<AbstractBuilding> tempList = new ArrayList<>();
	    for (AbstractBuilding b : buildingList) {
	        if (SB.intersects(b.hitbox)) {
	            tempList.add(b);
	        }
	    }

	    // If we found any, pick the first one and mark it selected
	    if (!tempList.isEmpty()) {
	        tempList.get(0).setSelected(true);
	        selectedBuilding = tempList.get(0);
	    }
	}

	public void clearSelectedBuilding() {
		AbstractBuilding ab = (AbstractBuilding)selectedBuilding;
		if (selectedBuilding != null) {
			ab.setSelected(false);
			selectedBuilding = null;
		}
	}
	
	
	// Getter & Setter 
	
	public void setInPlacementMode(boolean inPlacementMode) { this.inPlacementMode = inPlacementMode; }
	
	public boolean getInPlacementMode() { return inPlacementMode; }

	public static IEntity getSelectedBuilding() {
		for (Barracks building : buildingList) {
			if (building.isSelected()) {
				return (IEntity)building;
			}
		}
		return null;
	}
}
