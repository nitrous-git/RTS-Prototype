package EnemyAI;

import Building.BuildingType;
import Manager.GameContext;
import Unit.UnitType;

public class AIStateContext {
    private float timestamp;
    private BuildingType buildingType;
    private UnitType unitType;
    private GameContext gameContext;
    private int unitAmount;

    public AIStateContext() { }

    public void setBuildContext(float timestamp, BuildingType buildingType){
        this.timestamp = timestamp;
        this.buildingType = buildingType;
    }

    public void setTrainUnitContext(float timestamp, UnitType unitType){
        this.timestamp = timestamp;
        this.unitType = unitType;
    }

    public void setWaveAttackContext(float timestamp, GameContext gameContext, int unitAmount){
        this.timestamp = timestamp;
        this.gameContext = gameContext;
        this.unitAmount = unitAmount;
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

    public GameContext getGameContext() {
        return gameContext;
    }

    public int getUnitAmount() {
        return unitAmount;
    }

    public void setTimestamp(float timestamp) {
        this.timestamp = timestamp;
    }
}
