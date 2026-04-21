package Faction;

import Faction.ColorPalette.FactionColors;
import GameObjects.Tile;
import Manager.*;
import Panel.CommandPanel;
import Util.Camera;
import Util.TileMap;
import Util.Vector2Int;

import java.awt.*;

public class Faction {

    private final String name;
    private final TileMap map;
    private final ResourceManager RM;
    private final BuildingManager BM;
    private final UnitManager UM;
    private final FactionController controller;
    private Vector2Int spawnSeed;
    private FactionColors factionColors;

    public boolean isAI;

    public Faction(String name, FactionController controller,
                   UnitManager UM, BuildingManager BM, ResourceManager RM,
                   TileMap map, FactionColors factionColors) {
        this.name = name;
        this.map = map;
        this.RM = RM;
        this.BM = BM;
        this.UM = UM;
        this.controller = controller;
        controller.init(this);
        isAI = controller instanceof AIController;
        this.factionColors = factionColors;
    }

    public void update() {
        BM.update();
        UM.update();

        if (isAI) controller.update();
    }

    public void draw(Graphics g, Camera camera) {
        BM.draw(g, camera);
        UM.draw(g, camera);
    }

    public ResourceManager getResourceManager() { return RM; }
    public BuildingManager getBuildingManager() { return BM; }
    public UnitManager getUnitManager() { return UM; }
    public FactionController getController() { return controller; }
    public String getName() { return name; }
    public CommandPanel getCommandPanel() { return getController().getCP(); }
    public TileMap getMap() { return map; }
    public Vector2Int getSpawnSeed() { return spawnSeed; }

    public void setSpawnSeed(SpawnSeedRepository SSR, int preferredIndex) {
        this.spawnSeed = SSR.requestSeed(preferredIndex);
    }

    public boolean isAI() {
        return isAI;
    }

    public FactionColors getFactionColors() {
        return factionColors;
    }

}
