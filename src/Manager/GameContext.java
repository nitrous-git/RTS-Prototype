package Manager;

import Building.AbstractBuilding;
import GameObjects.IEntity;
import Unit.AbstractUnit;
import Util.SelectionBox;

import java.util.ArrayList;
import java.util.List;

public class GameContext {

    // Declarations
    private List<AbstractBuilding> allBuildings = new ArrayList<>();
    private List<AbstractUnit> allUnits = new ArrayList<>();
    private AbstractBuilding selectedBuilding;
    private AbstractUnit selectedUnit;

    // Constructor
    public GameContext() {}

    // Helpers && Utilities //
    public void registerUnit(AbstractUnit u) {
        allUnits.add(u);
    }

    public void unregisterUnit(AbstractUnit u) {
        allUnits.remove(u);
    }

    public void registerBuilding(AbstractBuilding b) {
        allBuildings.add(b);
    }

    public void unregisterBuilding(AbstractBuilding b) {
        allBuildings.remove(b);
    }

    public void checkBuildingSelection(SelectionBox SB) {
        // Clear all previous selections
        for (AbstractBuilding b : allBuildings) {
            b.setSelected(false);
        }

        // Gather all buildings whose hitbox overlaps the selection
        List<AbstractBuilding> tempList = new ArrayList<>();
        for (AbstractBuilding b : allBuildings) {
            if (SB.intersects(b.hitbox)) {
                tempList.add(b);
            }
        }

        // If we found any, pick the first one and mark it selected
        if (!tempList.isEmpty()) {
            tempList.get(0).setSelected(true);
            selectedBuilding = tempList.get(0);
        }
    }

    public void clearSelectedBuilding() {
        AbstractBuilding ab = (AbstractBuilding)selectedBuilding;
        if (selectedBuilding != null) {
            ab.setSelected(false);
            selectedBuilding = null;
        }
    }

    public AbstractBuilding getSelectedBuilding() {
        for (AbstractBuilding building : allBuildings) {
            if (building.isSelected()) {
                return building;
            }
        }
        return null;
    }


    // Getter && Setter //
    public List<AbstractBuilding> getAllBuildings() {
        return allBuildings;
    }

    public List<AbstractUnit> getAllUnits() {
        return allUnits;
    }

}
