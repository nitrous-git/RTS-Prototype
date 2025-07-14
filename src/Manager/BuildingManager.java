package Manager;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import Building.AbstractBuilding;
import Building.Barracks;
import Building.CommandCenter;
import Building.SupplyDepot;
import GameObjects.IEntity;
import GameObjects.Tile;
import Panel.GamePanel;
import Building.BuildingType;
import Resource.Cost;
import Resource.ResourceType;
import Util.*;

public class BuildingManager {

	public boolean inPlacementMode;

    public static List<AbstractBuilding> buildingList;

	// *** could probably be a AbstractBuilding instead of IEntity
    public static IEntity selectedBuilding; // only one selected building at the time
	public static AbstractBuilding quickSelection;
	int h, w = 0;

	public boolean transactionAllowed;

    public List<Tile> tempTileList;
    public List<Vector2Int> tempTileIndex;

	TileMap map;
	GamePanel gp;

	// Constructor
	public BuildingManager(TileMap map, GamePanel gp) {
		this.map = map;
		this.gp = gp;
		buildingList = new ArrayList<AbstractBuilding>();
		tempTileList = new ArrayList<Tile>();
		tempTileIndex = new ArrayList<Vector2Int>();
		
		inPlacementMode = false;
	}
	
	public void draw(Graphics g, Camera c) {
		for (AbstractBuilding build : buildingList) {
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

	/**
	 * Construct a building
	 * If we can’t afford it -> do nothing
	 */
	public void construct(BuildingType type, Vector2Int startPos) {
		Cost cost = type.getCost();
		if (!gp.RM.canAfford(cost)) {
			Logger.log("Not enough resources for " + type + " building");
			System.out.println("Not enough resources for " + type + " building");
			return;
		}

		// spend resource, construct
		gp.RM.spend(cost);

		switch (type){
			case BARRACKS -> addBarracks(startPos);
			case SUPPLY_DEPOT -> addSupplyDepot(startPos);
			case COMMAND_CENTER -> addCommandCenter(startPos);
		}

		// If it's a supply‐providing building, bump the cap
		if (type.getSupplyProvided() > 0) {
			gp.RM.increaseMaxSupply(type.getSupplyProvided());
		}
		Logger.log("Built " + type
				+ " | Minerals left: " + gp.RM.get(ResourceType.MINERAL)  + " | Supply: " + gp.RM.getUsedSupply() + "/" + gp.RM.getMaxSupply());
		System.out.println("Built " + type
				+ " | Minerals left: " + gp.RM.get(ResourceType.MINERAL)  + " | Supply: " + gp.RM.getUsedSupply() + "/" + gp.RM.getMaxSupply());
	}

    public void addBarracks(Vector2Int startPos) {
    	// convert back to world size after snap
    	Vector2 startf = GamePanel.convertCellToWorld(startPos.x, startPos.y);
    	if (isValidPlacement(startPos, Barracks.WIDTH_TILES, Barracks.HEIGHT_TILES)) {
    		Barracks barracks = new Barracks(map, gp, startf.x, startf.y);
    		barracks.setTag("Barracks");
    		barracks.setID(startPos.x*startPos.y); // ID is the index of start point inside the grid
    		buildingList.add( barracks ); 
		}
    }

	public void addSupplyDepot(Vector2Int startPos) {
		// convert back to world size after snap
		Vector2 startf = GamePanel.convertCellToWorld(startPos.x, startPos.y);
		if (isValidPlacement(startPos, SupplyDepot.WIDTH_TILES, SupplyDepot.HEIGHT_TILES)) {
			SupplyDepot supplyDepot = new SupplyDepot(map, gp, startf.x, startf.y);
			supplyDepot.setTag("SupplyDepot");
			supplyDepot.setID(startPos.x*startPos.y); // ID is the index of start point inside the grid
			buildingList.add( supplyDepot );
		}
	}

	public void addCommandCenter(Vector2Int startPos) {
		// convert back to world size after snap
		Vector2 startf = GamePanel.convertCellToWorld(startPos.x, startPos.y);
		if (isValidPlacement(startPos, CommandCenter.WIDTH_TILES, CommandCenter.HEIGHT_TILES)) {
			CommandCenter commandCenter = new CommandCenter(map, gp, this, startf.x, startf.y);
			commandCenter.setTag("CommandCenter");
			commandCenter.setID(startPos.x*startPos.y); // ID is the index of start point inside the grid
			buildingList.add( commandCenter );
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
	public void placementHelper(Vector2Int startPos, BuildingType type){
		clearPlacementHelper();
		Vector2 worldPos = GamePanel.convertCellToWorld(startPos.x, startPos.y);
		Color tempColor;
		Vector2Int dim = getPlacementDimension(type);

    	for (int i = 0; i < dim.y; i++) {
		    for (int j = 0; j < dim.x; j++) {
		    	
		    	if (map.intArr[startPos.y+i][startPos.x+j] != 0) {
		    		tempColor = Color.DARK_GRAY;
		    	}else {
		    		tempColor = GameColors.BUILDING_PLACEMENT_TILE_HELPER;
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

	public Vector2Int getPlacementDimension (BuildingType type){
		Vector2Int dim = null;
		switch (type) {
			case BARRACKS:
				h = Barracks.HEIGHT_TILES;
				w = Barracks.WIDTH_TILES;
				dim = new Vector2Int(w, h);
				return dim;
			case SUPPLY_DEPOT:
				h = SupplyDepot.HEIGHT_TILES;
				w = SupplyDepot.WIDTH_TILES;
				dim = new Vector2Int(w, h);
				return dim;
			case COMMAND_CENTER:
				h = CommandCenter.HEIGHT_TILES;
				w = CommandCenter.WIDTH_TILES;
				dim = new Vector2Int(w, h);
				return dim;
		}
		return dim;
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

	public void checkQuickBoxSelection(SelectionBox SB){
		for (AbstractBuilding building : buildingList) {
			if (SB.intersects(building.hitbox)) {
				quickSelection = building;
			}
		}
	}

	public void clearSelectedBuilding() {
		AbstractBuilding ab = (AbstractBuilding)selectedBuilding;
		if (selectedBuilding != null) {
			ab.setSelected(false);
			selectedBuilding = null;
		}
	}

	/// Getter & Setter
	
	public void setInPlacementMode(boolean inPlacementMode) { this.inPlacementMode = inPlacementMode; }
	
	public boolean getInPlacementMode() { return inPlacementMode; }

	public static IEntity getSelectedBuilding() {
		for (AbstractBuilding building : buildingList) {
			if (building.isSelected()) {
				return (IEntity)building;
			}
		}
		return null;
	}
}
