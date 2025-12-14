package Manager;

import Building.AbstractBuilding;
import Faction.Faction;
import GameObjects.IEntity;
import GameObjects.Tile;
import Panel.GamePanel;
import Resource.*;
import Unit.AbstractUnit;
import Unit.EnemyUnit;
import Unit.IControllable;
import Util.Camera;
import Util.SelectionBox;
import Util.TileMap;
import Util.Vector2Int;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResourceManager implements IResourceManager {

    ///  DECLARATIONS  ///
    private int mineralCount = 1000;
    private int gasCount     = 0;
    private int usedSupply   = 0;
    private int maxSupply    = 10;    // default starting supply

    private final List<ResourceNode> worldNodes; // reference, not a copy
    public static List<ResourceNode> selectedResourceNodeList;
    public static ResourceNode quickSelection;

    TileMap map;
    Faction ownerFaction;

    // Constructor
    public ResourceManager(TileMap map, ResourceNodeRepository RNR) {
        this.map = map;
        this.worldNodes = RNR.getAllNodes();
        selectedResourceNodeList = new ArrayList<ResourceNode>();
    }

    /*
    public void draw(Graphics g, Camera c) {
        for (ResourceNode node : resourceNodeList) {
            node.draw(g, c);
        }
    }

    public void update() {
        for (int i = 0; i < resourceNodeList.size(); i++) {
            resourceNodeList.get(i).update();
            if (resourceNodeList.get(i).isDepleted()) {
                System.out.println("IsDepleted");
                resourceNodeList.get(i).destroyRemainingArtefact();
                resourceNodeList.remove(resourceNodeList.get(i));
            }
        }
    }
     */

    /*
    * Economy and trading methods
    * */
    @Override
    public boolean canAfford(Cost cost) {
        return mineralCount >= cost.getMinerals()
                && gasCount >= cost.getGas()
                && usedSupply + cost.getSupply() <= maxSupply;
    }

    @Override
    public void spend(Cost cost) {
        if (!canAfford(cost))
            throw new IllegalStateException("Cannot afford: " + cost);
        mineralCount -= cost.getMinerals();
        gasCount -= cost.getGas();
        usedSupply += cost.getSupply();
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


    /*
    * Helper methods
    *
    public void buildResourceList() {
        float posX = 0;
        float posY = 0;

        for (int i = 0; i < GamePanel.ROWS; i++) {
            for (int j = 0; j < GamePanel.COLS; j++) {

                if (map.intArr[i][j] == ResourceNode.TOKEN) {
                    if (map.tileArr[i][j] != null) return;
                    System.out.println(map.tileArr[i][j]);

                    ResourceNode node = new ResourceNode(map, ResourceType.MINERAL, posX, posY);
                    int index = GamePanel.COLS*i + j;
                    node.setID(index);
                    node.setTag("Mineral");
                    resourceNodeList.add(node);

                    System.out.println(map.tileArr[i][j]);
                }
                posX += GamePanel.TILE_SIZE;
            }
            posY += GamePanel.TILE_SIZE;
            posX = 0;
        }
    }
    */

    public void clearSelectedResources() {
        for (ResourceNode node : worldNodes) {
            node.setSelected(false);
        }
        if (!selectedResourceNodeList.isEmpty()) selectedResourceNodeList.clear();
    }

    // update the unit selected states
    public void checkSelection(SelectionBox SB) {
        for (ResourceNode node : worldNodes) {
            boolean unitSelected;
            unitSelected = SB.intersects(node.hitbox);
            node.setSelected(unitSelected);
        }
    }

    public void checkQuickBoxSelection(SelectionBox SB){
        for (ResourceNode node : worldNodes) {
            if (SB.intersects(node.hitbox)) {
                quickSelection = node;
            }
        }
    }

    public List<ResourceNode> getSelectedResourceNodeList() {
        List<ResourceNode> srn = new ArrayList<>();
        for (ResourceNode node : worldNodes) {
            if (node.isSelected()) {
                srn.add(node);
            }
        }
        selectedResourceNodeList = srn;
        return srn;
    }



    /*
     * Getter & Setter
     * */
    @Override
    public int get(ResourceType type) {
        return (type == ResourceType.MINERAL ? mineralCount : gasCount);
    }
    @Override
    public int getMaxSupply()   { return maxSupply; }
    @Override
    public int getUsedSupply()  { return usedSupply; }

    public int getGasCount() {
        return gasCount;
    }

    public void setGasCount(int gasCount) {
        this.gasCount = gasCount;
    }

    public int getMineralCount() {
        return mineralCount;
    }

    public void setMineralCount(int mineralCount) {
        this.mineralCount = mineralCount;
    }

    public List<ResourceNode> getWorldNodes() {
        return worldNodes;
    }

}
