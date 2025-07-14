package Command;

import GameObjects.IEntity;
import Building.BuildingType;
import Resource.ResourceType;
import Util.Camera;
import Util.Vector2Int;

public class CommandContext {
    // MOVE
    private float x, y;
    private Camera camera;

    // ATTACK
    private IEntity target;

    // CONSTRUCT
    BuildingType buildingType;
    Vector2Int cellPos;

    // GATHER
    ResourceType resourceType;

    // Setters
    public CommandContext setDestination(float x, float y, Camera cam) {
        this.x = x;
        this.y = y;
        this.camera = cam;
        return this;
    }

    public CommandContext setRepair(Vector2Int cellPos) {
        this.cellPos = cellPos;
        return this;
    }

    public CommandContext setConstruction(BuildingType buildingType, Vector2Int cellPos){
        this.cellPos = cellPos;
        this.buildingType = buildingType;
        return this;
    }

    public CommandContext setGathering(ResourceType resourceType, Vector2Int cellPos){
        this.cellPos = cellPos;
        this.resourceType = resourceType;
        return this;
    }

    public CommandContext setDelivery(ResourceType resourceType, Vector2Int cellPos){
        this.cellPos = cellPos;
        this.resourceType = resourceType;
        return this;
    }

    // Getters
    public float      getX()      { return x; }
    public float      getY()      { return y; }
    public Camera     getCamera() { return camera; }
    public IEntity    getTarget() { return target; }
    public BuildingType getBuildingType() { return buildingType; }
    public ResourceType getResourceType() { return resourceType; }
    public Vector2Int getCellPos(){ return cellPos; }
}

