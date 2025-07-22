package Faction;

import Manager.*;
import Panel.CommandPanel;
import Util.Camera;
import Util.TileMap;

import java.awt.*;

public class Faction {
    private final String name;
    private final ResourceManager RM;
    private final BuildingManager BM;
    private final UnitManager UM;
    private final FactionController controller;

    public Faction(String name, FactionController controller, UnitManager UM, ResourceManager RM, TileMap map) {
        this.name = name;
        this.RM = RM;
        this.BM = new BuildingManager(map, this);
        this.UM = UM;
        this.controller = controller;
        controller.init(this);

        //RM.buildResourceList();
    }

    public void update() {
        //RM.update();
        BM.update();
        UM.update();
    }

    public void draw(Graphics g, Camera camera) {
        //RM.draw(g, camera);
        BM.draw(g, camera);
        UM.draw(g, camera);
    }

    public ResourceManager getResourceManager() { return RM; }
    public BuildingManager getBuildingManager() { return BM; }
    public UnitManager getUnitManager() { return UM; }
    public FactionController getController() { return controller; }
    public String getName() { return name; }
    public CommandPanel getCommandPanel() { return getController().getCP(); }
}
