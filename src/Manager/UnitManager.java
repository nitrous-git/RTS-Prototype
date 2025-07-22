package Manager;

import Faction.Faction;
import Unit.AbstractUnit;
import Util.Camera;
import Util.TileMap;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Abstract base class for managing a collection of units (player or enemy)
 */
public abstract class UnitManager {
    protected TileMap map;
    Faction ownerFaction;

    // Constructor
    public UnitManager(TileMap map) {
        this.map = map;
    }

    public abstract void buildUnitSquad();

    public abstract void draw(Graphics g, Camera c);

    public abstract void update();

    public void setOwnerFaction(Faction ownerFaction){ this.ownerFaction = ownerFaction; }
}
