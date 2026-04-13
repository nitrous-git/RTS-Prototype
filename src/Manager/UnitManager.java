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
    public List<AbstractUnit> unitList; // this list was static before ...
    public List<AbstractUnit> selectedUnitList;
    protected TileMap map;
    Faction ownerFaction;
    GameContext GC;

    // Constructor
    public UnitManager(TileMap map, GameContext GC) {
        this.map = map;
        this.GC = GC;
    }

    public abstract void buildUnitSquad();

    public abstract void draw(Graphics g, Camera c);

    public abstract void update();

    public void setOwnerFaction(Faction ownerFaction){ this.ownerFaction = ownerFaction; }

    public List<AbstractUnit> getUnitList(){ return unitList; };

    public List<AbstractUnit> getSelectedUnitList() { return selectedUnitList; }

    public void addUnit(AbstractUnit unit) {
        unitList.add(unit);
        GC.registerUnit(unit);
    }

    //public List<AbstractUnit> constructSelectedUnitList(){ return null; }

    public GameContext getGC() { return GC; }

}
