package Manager;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import Building.AbstractBuilding;
import Building.Barracks;
import Building.CommandCenter;
import Building.SupplyDepot;
import Faction.Faction;
import GameObjects.IEntity;
import GameObjects.Tile;
import Panel.GamePanel;
import Building.BuildingType;
import Resource.Cost;
import Resource.ResourceType;
import Unit.AbstractUnit;
import Util.*;

public class BuildingManager {

	public boolean inPlacementMode;

    public List<AbstractBuilding> buildingList;
    public AbstractBuilding selectedBuilding; // only one selected building at the time
	public AbstractBuilding quickSelection;
	int h, w = 0;

	public boolean transactionAllowed;

    public List<Tile> tempTileList;
    public List<Vector2Int> tempTileIndex;

	TileMap map;
	Faction ownerFaction;
	GameContext GC;

	// Constructor
	public BuildingManager(TileMap map, GameContext GC) {
		this.map = map;
		this.GC = GC;
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
//		for (int i = 0; i < buildingList.size(); i++) {
//			buildingList.get(i).update();
//			if (buildingList.get(i).isDestroyed()) {
//				buildingList.remove(buildingList.get(i));
//			}
//		}

		List<AbstractBuilding> destroyedBuildings = new ArrayList<>();

		for (AbstractBuilding building : buildingList) {
			building.update();
			if (building.isDestroyed()) {
				destroyedBuildings.add(building);
			}
		}

		for (AbstractBuilding building : destroyedBuildings) {
			removeBuilding(building);
		}
	}

	public void removeBuilding(AbstractBuilding building) {
		buildingList.remove(building);
		GC.unregisterBuilding(building);

		Vector2Int cellPos = GamePanel.convertWorldToCell(building.x, building.y);
		int building_width = (int)(building.getWidth()/GamePanel.TILE_SIZE);
		int building_height = (int)(building.getHeight()/GamePanel.TILE_SIZE);
		//System.out.println("building size is : " + building_width + "X" + building_height );

		for (int i = cellPos.y; i < cellPos.y+building_height; i++) {
			for (int j = cellPos.x; j < cellPos.x+building_width; j++) {
				map.intArr[i][j] = 0;
				map.tileArr[i][j] = null;
			}
		}
	}


	/**
	 * Construct a building
	 * If we can’t afford it -> do nothing
	 */
	public void construct(BuildingType type, Vector2Int startPos) {
		Cost cost = type.getCost();
		if (!ownerFaction.getResourceManager().canAfford(cost)) {
			Logger.log("Not enough resources for " + type + " building : " + ownerFaction.getName());
			System.out.println("Not enough resources for " + type + " building : " + ownerFaction.getName());
			return;
		}

		// spend resource, construct
		ownerFaction.getResourceManager().spend(cost);

		switch (type){
			case BARRACKS -> addBarracks(startPos);
			case SUPPLY_DEPOT -> addSupplyDepot(startPos);
			case COMMAND_CENTER -> addCommandCenter(startPos);
		}

		// If it's a supply‐providing building, bump the cap
		if (type.getSupplyProvided() > 0) {
			ownerFaction.getResourceManager().increaseMaxSupply(type.getSupplyProvided());
		}

		Logger.log("Built " + type
				+ " | Minerals left: " + ownerFaction.getResourceManager().get(ResourceType.MINERAL)
				+ " | Supply: " + ownerFaction.getResourceManager().getUsedSupply() + "/"
				+ ownerFaction.getResourceManager().getMaxSupply());

		System.out.println("Built " + type
				+ " | Minerals left: " + ownerFaction.getResourceManager().get(ResourceType.MINERAL)
				+ " | Supply: " + ownerFaction.getResourceManager().getUsedSupply() + "/"
				+ ownerFaction.getResourceManager().getMaxSupply());
	}

    public void addBarracks(Vector2Int startPos) {
    	// convert back to world size after snap
    	Vector2 startf = GamePanel.convertCellToWorld(startPos.x, startPos.y);
    	if (isValidPlacement(startPos, Barracks.WIDTH_TILES, Barracks.HEIGHT_TILES)) {
    		Barracks barracks = new Barracks(map, ownerFaction, startf.x, startf.y);
    		barracks.setTag("Barracks");
    		barracks.setID(startPos.x*startPos.y); // ID is the index of start point inside the grid
			addBuilding(barracks);
		}
    }

	public void addSupplyDepot(Vector2Int startPos) {
		// convert back to world size after snap
		Vector2 startf = GamePanel.convertCellToWorld(startPos.x, startPos.y);
		if (isValidPlacement(startPos, SupplyDepot.WIDTH_TILES, SupplyDepot.HEIGHT_TILES)) {
			SupplyDepot supplyDepot = new SupplyDepot(map, ownerFaction, startf.x, startf.y);
			supplyDepot.setTag("SupplyDepot");
			supplyDepot.setID(startPos.x*startPos.y); // ID is the index of start point inside the grid
			addBuilding(supplyDepot);
		}
	}

	public void addCommandCenter(Vector2Int startPos) {
		// convert back to world size after snap
		Vector2 startf = GamePanel.convertCellToWorld(startPos.x, startPos.y);
		if (isValidPlacement(startPos, CommandCenter.WIDTH_TILES, CommandCenter.HEIGHT_TILES)) {
			CommandCenter commandCenter = new CommandCenter(map, ownerFaction, this, startf.x, startf.y);
			commandCenter.setTag("CommandCenter");
			commandCenter.setID(startPos.x*startPos.y); // ID is the index of start point inside the grid
			addBuilding(commandCenter);
		}
	}

	public void addBuilding(AbstractBuilding building) {
		buildingList.add(building);
		GC.registerBuilding(building);
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

	public IEntity getSelectedBuilding() {
		for (AbstractBuilding building : buildingList) {
			if (building.isSelected()) {
				return (IEntity)building;
			}
		}
		return null;
	}

	public void setOwnerFaction(Faction ownerFaction){ this.ownerFaction = ownerFaction; }

	public GameContext getGameContext(){ return GC; }
}
