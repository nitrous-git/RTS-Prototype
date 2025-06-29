package Manager;

import Resource.Cost;
import Resource.IResourceManager;
import Resource.ResourceType;

public class ResourceManager implements IResourceManager {
    private int mineralCount = 1000;
    private int gasCount     = 0;
    private int usedSupply   = 0;
    private int maxSupply    = 10;    // default starting supply

    @Override
    public int get(ResourceType type) {
        return (type == ResourceType.MINERAL ? mineralCount : gasCount);
    }
    @Override
    public int getMaxSupply()   { return maxSupply; }
    @Override
    public int getUsedSupply()  { return usedSupply; }

    @Override
    public boolean canAfford(Cost cost) {
        return mineralCount >= cost.getMinerals()
                && gasCount     >= cost.getGas()
                && usedSupply   + cost.getSupply() <= maxSupply;
    }

    @Override
    public void spend(Cost cost) {
        if (!canAfford(cost))
            throw new IllegalStateException("Cannot afford: " + cost);
        mineralCount -= cost.getMinerals();
        gasCount     -= cost.getGas();
        usedSupply   += cost.getSupply();
    }

    @Override
    public void add(ResourceType type, int amount) {
        if (type == ResourceType.MINERAL)
            mineralCount += amount;
        else
            gasCount += amount;
    }

    @Override
    public void increaseMaxSupply(int by) {
        maxSupply += by;
    }
}
