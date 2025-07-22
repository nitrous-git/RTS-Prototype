package Faction;

import Util.Camera;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FactionManager {
    private final List<Faction> factions = new ArrayList<>();

    public void addFaction(Faction faction) {
        factions.add(faction);
    }

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

    public List<Faction> getFactions() {
        return factions;
    }
}
