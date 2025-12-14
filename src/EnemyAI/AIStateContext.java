package EnemyAI;

import Building.BuildingType;
import Unit.UnitType;

public class AIStateContext {
    private float timestamp;
    private BuildingType buildingType;
    private UnitType unitType;

    public AIStateContext() { }

    public void setBuildContext(float timestamp, BuildingType buildingType){
        this.timestamp = timestamp;
        this.buildingType = buildingType;
    }

    public void setTrainUnitContext(float timestamp, UnitType unitType){
        this.timestamp = timestamp;
        this.unitType = unitType;
    }

    public BuildingType getBuildingType() {
        return buildingType;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public float getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(float timestamp) {
        this.timestamp = timestamp;
    }
}
