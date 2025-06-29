package Resource;

public class Cost {
    private final int minerals, gas, supply;

    public Cost(int minerals, int gas, int supply) {
        if (minerals < 0 || gas < 0 || supply < 0)
            throw new IllegalArgumentException("Cost values must be ≥ 0");
        this.minerals = minerals;
        this.gas      = gas;
        this.supply   = supply;
    }
    public int getMinerals() { return minerals; }
    public int getGas()      { return gas; }
    public int getSupply()   { return supply; }

    public Cost scale(double factor) {
        return new Cost((int)(minerals*factor),
                (int)(gas*factor),
                (int)(supply*factor));
    }

    public String toString() {
        return String.format("[%dM, %dG, %dS]", minerals, gas, supply);
    }
}
