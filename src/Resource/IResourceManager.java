package Resource;

public interface IResourceManager {
    int   get(ResourceType type);
    int   getMaxSupply();
    int   getUsedSupply();
    boolean canAfford(Cost cost);
    void  spend(Cost cost);
    void  add(ResourceType type, int amount);
    void  increaseMaxSupply(int by);
}