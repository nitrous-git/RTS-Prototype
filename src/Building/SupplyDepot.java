package Building;

import java.awt.Color;
import java.awt.Graphics;

import Faction.Faction;
import Panel.GamePanel;
import Util.*;
import GameObjects.Tile;

/**
 * A simple building that, when completed, increases the player's max supply
 */
public class SupplyDepot extends AbstractBuilding {

    // footprint in tiles
    public static final int WIDTH_TILES  = 2;
    public static final int HEIGHT_TILES = 2;
    public static final int TOKEN = 3;             // unique marker in intArr

    // construction duration
    public static final int CONSTRUCTION_TICKS = 480; // e.g. 8 seconds @60fps

    // how much supply this depot provides
    public static final int SUPPLY_AMOUNT = 8;

    private int constructionTimer = 0;

    private final Vector2 worldPos;
    private final Vector2Int cellPos;
    private final TileMap map;
    //private final Faction ownerFaction;

    public SupplyDepot(TileMap map, Faction ownerFaction, float x, float y) {
        super(x, y,
            (int)(GamePanel.TILE_SIZE * WIDTH_TILES),
            (int)(GamePanel.TILE_SIZE * HEIGHT_TILES),
            ownerFaction );

        // mark type so CommandPanel can pick it up
        TYPE = BuildingType.SUPPLY_DEPOT;

        this.worldPos = new Vector2(x, y);
        this.cellPos = GamePanel.convertWorldToCell(x, y);
        this.map = map;
        //this.ownerFaction = ownerFaction;

        setMaxHealth(250.0f);
        currentHealth = maxHealth;

        // begin under construction
        this.currentState = State.UNDER_CONSTRUCTION;
        paintTiles(GameColors.BUILDING_SUPPLY_DEPOT_UNDER_CONSTRUCTION);
    }

    public void update() {
        switch (currentState) {
            case UNDER_CONSTRUCTION:
                constructionTimer++;
                if (constructionTimer >= CONSTRUCTION_TICKS) {
                    currentState = State.IN_OPERATION;
                    paintTiles(GameColors.BUILDING_SUPPLY_DEPOT);
                    Logger.log("Supply Depot completed, +"
                            + SUPPLY_AMOUNT + " supply");
                    System.out.println("Supply Depot completed, +"
                            + SUPPLY_AMOUNT + " supply");
                    // increase the player's max supply
                    ownerFaction.getResourceManager().increaseMaxSupply(SUPPLY_AMOUNT);

                    if (!ownerFaction.isAI) {
                        // activate commandPanel
                        // enable its commands (e.g. none, but for UI consistency)
                        ownerFaction.getCommandPanel().setCommandsForBuilding(this);
                    }
                }
                return;
            case IN_OPERATION:
                // nothing to do each tick
                return;
        }
    }

    @Override
    public void draw(Graphics g, Camera camera) {
        if (!camera.captures(this)) return;
        if (selected) {
            g.setColor(GameColors.BUILDING_HIGHLIGHT);
            g.drawRect(
                    (int)((x - camera.getX()) * camera.scaleX),
                    (int)((y - camera.getY()) * camera.scaleY),
                    (int)(width * camera.scaleX),
                    (int)(height * camera.scaleY)
            );
        }
    }

    /**
     * Paints this depot onto the tileMap, marking intArr and tileArr.
     */
    private void paintTiles(Color color) {
        for (int ty = 0; ty < HEIGHT_TILES; ty++) {
            for (int tx = 0; tx < WIDTH_TILES; tx++) {
                float wx = worldPos.x + tx * GamePanel.TILE_SIZE;
                float wy = worldPos.y + ty * GamePanel.TILE_SIZE;
                map.tileArr[cellPos.y + ty][cellPos.x + tx] =
                        new Tile(wx, wy,
                                (int)GamePanel.TILE_SIZE,
                                (int)GamePanel.TILE_SIZE,
                                color);
                map.intArr[cellPos.y + ty][cellPos.x + tx] = TOKEN;
            }
        }
    }

    // getters for UI/status display
    public int getConstructionTimer() { return constructionTimer; }
    public static int getConstructionTicks() { return CONSTRUCTION_TICKS; }
}

