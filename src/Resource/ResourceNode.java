package Resource;

import Util.*;
import Panel.GamePanel;
import GameObjects.Entity;
import GameObjects.Tile;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static Util.TileMap.EMPTY_TOKEN;

public class ResourceNode extends Entity {

    ///  DECLARATIONS ///
    public static final int TOKEN = 2;

    private final ResourceType type;
    public int initialAmount = 100;
    private int remainingAmount;
    private final int gatherRate = 10;
    protected boolean selected;

    private final Vector2 worldPos;
    private final Vector2Int cellPos;
    private final TileMap map;

    // Constructor
    public ResourceNode(TileMap map, ResourceType type, float x, float y) {
        super(x, y, (int)(GamePanel.TILE_SIZE), (int)(GamePanel.TILE_SIZE));

        worldPos = new Vector2(x, y);
        cellPos = GamePanel.convertWorldToCell(x, y);

        this.type = type;
        this.remainingAmount = initialAmount;
        this.map = map;

        initHitbox(x, y, this.width, this.height);
        addNodeToMap();
    }

    @Override
    public void draw(Graphics g, Camera camera) {
        if (camera.captures(this) && !isDepleted()) {
            if (selected) {
                g.setColor(GameColors.RESOURCE_HIGHLIGHT);
                g.drawRect(
                        (int) ((x - camera.getX()) * camera.scaleX),
                        (int) ((y - camera.getY()) * camera.scaleY),
                        (int) (width * camera.scaleX),
                        (int) (height * camera.scaleY)
                );
            }
            /*
            g.setColor(GameColors.RESOURCE_NODE_MINERAL);
            g.fillRect(
                    (int) ((x - camera.getX()) * camera.scaleX),
                    (int) ((y - camera.getY()) * camera.scaleY),
                    (int) (width * camera.scaleX),
                    (int) (height * camera.scaleY)
            );
             */
        }
    }

    public void update(){ }


    private void addNodeToMap() {
        map.tileArr[cellPos.y][cellPos.x] = new Tile(worldPos.x,
                                                    worldPos.y,
                                                    (int) GamePanel.TILE_SIZE,
                                                    (int) GamePanel.TILE_SIZE,
                                                    GameColors.RESOURCE_NODE_MINERAL);
        map.intArr[cellPos.y ][cellPos.x ] = TOKEN;

        //map.printer();
    }

    public int gather(IResourceManager resourceManager) {
        if (isDepleted()) return 0;
        int amountHarvested = Math.min(gatherRate, remainingAmount);
        remainingAmount -= amountHarvested;
        resourceManager.add(type, amountHarvested);
        //System.out.println("Remaining : " + remainingAmount +"/" + initialAmount +" : of Type :"+getType()+" from"+ toString());
        return amountHarvested;
    }

    public void extract(int quantity) {
        if (isDepleted()) return;
        int amountHarvested = Math.min(quantity, remainingAmount);
        remainingAmount -= amountHarvested;
    }

    public void destroyRemainingArtefact(){
        map.tileArr[cellPos.y][cellPos.x] = null;
        map.intArr[cellPos.y ][cellPos.x ] = EMPTY_TOKEN;
    }

    private void initHitbox(float x, float y, int width, int height) {
        // Body hitbox
        hitbox = new Rectangle2D.Float(x,  y, 0.9f*width, 0.9f*height);
    }


    /* --- Getter & Setter --- */

    public ResourceType getType() {
        return type;
    }

    public int getRemainingAmount() {
        return remainingAmount;
    }

    public boolean isDepleted() {
        return remainingAmount <= 0;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Rectangle.Float getHitbox() {
        return hitbox;
    }

    // -----------------------------------
    // pretty printing
    @Override
    public String toString() {
        return "tag : " + tag +" "+ ID + " IsSelected : " + selected;
    }
}
