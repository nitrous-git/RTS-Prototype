package Building;

import Resource.Cost;

public enum BuildingType {
    BARRACKS(new Cost(100, 0, 0), 0),
    COMMAND_CENTER(new Cost(200, 0, 0), 0),
    SUPPLY_DEPOT(new Cost(100, 0, 0), 4); // provides +4 supply

    private final Cost cost;
    private final int supplyProvided;

    BuildingType(Cost cost, int supplyProvided) {
        this.supplyProvided = supplyProvided;
        this.cost = cost;
    }

    public Cost getCost() {
        return cost;
    }

    public int getSupplyProvided() {
        return supplyProvided;
    }
}