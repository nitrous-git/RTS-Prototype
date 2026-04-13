package Manager;

import Building.AbstractBuilding;
import Unit.AbstractUnit;
import Unit.IControllable;
import Util.SelectionBox;

import java.util.ArrayList;
import java.util.List;

public class GameContext {

    // Declarations
    private List<AbstractBuilding> allBuildings = new ArrayList<>();
    private List<AbstractUnit> allUnits = new ArrayList<>();
    private AbstractBuilding selectedBuilding;
    private List<AbstractUnit> selectedUnits;

    // Constructor
    public GameContext() {}

    // Helpers && Utilities //
    public void registerUnit(AbstractUnit u) {
        allUnits.add(u);
        //System.out.println("AllUnits Size : " + allUnits.size() + " added : " + u.getOwnerFaction().getName());
    }

    public void unregisterUnit(AbstractUnit u) {
        allUnits.remove(u);
        System.out.println("AllUnits Size : " + allUnits.size() + " removed : " + u.getOwnerFaction().getName());
    }

    public void registerBuilding(AbstractBuilding b) {
        allBuildings.add(b);
    }

    public void unregisterBuilding(AbstractBuilding b) {
        allBuildings.remove(b);
    }

    // Building Selection logic //
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
        AbstractBuilding ab = (AbstractBuilding) selectedBuilding;
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

    // Unit Selection Logic //
    // update the unit selected states
    public void checkSelection(SelectionBox SB) {
        for (AbstractUnit unit : allUnits) {
            if (unit instanceof IControllable) {
                IControllable cu = (IControllable)unit;
                boolean unitSelected;
                // Check if the unit is inside the selection box
                unitSelected = SB.intersects(unit.hitbox);
                cu.setSelected(unitSelected);

                //System.out.println("Unit : " + ((AbstractUnit)cu).getID() + " is selected");
            }
        }
    }

    public List<AbstractUnit> constructSelectedUnitList() {
        List<AbstractUnit> su = new ArrayList<>();
        for (AbstractUnit unit : allUnits) {
            if (unit instanceof IControllable) {
                IControllable cu = (IControllable)unit;
                if (cu.isSelected()) {
                    su.add(unit);
                }
            }
        }
        selectedUnits = su;
        return su;
    }






    // Getter && Setter //
    public List<AbstractBuilding> getAllBuildings() {
        return allBuildings;
    }

    public List<AbstractUnit> getAllUnits() {
        return allUnits;
    }

}
