package Unit;

import Resource.Cost;

public enum UnitType {
    WORKER(new Cost(25, 0, 1)),
    COMBAT(new Cost(50, 0, 1));

    private final Cost cost;

    UnitType(Cost cost) {
        this.cost = cost;
    }

    public Cost getCost() {
        return cost;
    }
}