package Command;

import Building.AbstractBuilding;
import GameObjects.IEntity;
import Resource.BuildingType;
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

    // Getters
    public float      getX()      { return x; }
    public float      getY()      { return y; }
    public Camera     getCamera() { return camera; }
    public IEntity    getTarget() { return target; }
    public BuildingType getBuildingType() { return buildingType; }
    public Vector2Int getCellPos(){ return cellPos; }
}

