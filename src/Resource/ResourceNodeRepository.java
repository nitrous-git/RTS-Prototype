package Resource;

import Panel.GamePanel;
import Util.Camera;
import Util.TileMap;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static Resource.ResourceNode.TOKEN;

public class ResourceNodeRepository {
    private TileMap map;
    private final List<ResourceNode> resourceNodeList = new ArrayList<>();

    public ResourceNodeRepository(TileMap map) {
        this.map = map;
        buildResourceList();
    }

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

    private void buildResourceList() {
        float posX = 0;
        float posY = 0;

        for (int i = 0; i < GamePanel.ROWS; i++) {
            for (int j = 0; j < GamePanel.COLS; j++) {
                if (map.intArr[i][j] == TOKEN) {
                    ResourceNode node = new ResourceNode(map, ResourceType.MINERAL, posX, posY);
                    int index = GamePanel.COLS*i + j;
                    node.setID(index);
                    node.setTag("Mineral");
                    resourceNodeList.add(node);
                }
                posX += GamePanel.TILE_SIZE;
            }
            posY += GamePanel.TILE_SIZE;
            posX = 0;
        }
    }

    public List<ResourceNode> getAllNodes() {
        return Collections.unmodifiableList(resourceNodeList);
    }
}