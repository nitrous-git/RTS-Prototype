package Faction;

import Building.AbstractBuilding;
import Util.Camera;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FactionManager {
    private final List<Faction> factions = new ArrayList<>();
    private final List<AbstractBuilding> allBuilding = new ArrayList<>();

    /// Life cycle flow ///
    public void updateAll() {
        for (Faction faction : factions) {
            faction.update();
        }
    }

    public void drawAll(Graphics g, Camera camera) {
        for (Faction faction : factions) {
            faction.draw(g, camera);
        }
    }

    ///  Getter && Setter ///
    public List<Faction> getFactions() {
        return factions;
    }
    //public List<AbstractBuilding> getAllBuildings() {
    //    return allBuilding;
    //}

    ///  Helper && Utility ///
    public List<AbstractBuilding> getAllBuildings() {
        for (Faction faction : factions) {
            List<AbstractBuilding> buildList = faction.getBuildingManager().buildingList;
            allBuilding.addAll(buildList); // iteration is replaced by addAll
        }
        return allBuilding;
    }

    public void addFaction(Faction faction) {
        factions.add(faction);
    }

}
