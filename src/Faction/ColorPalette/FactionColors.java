package Faction.ColorPalette;

import java.awt.*;

public class FactionColors {

    // Units
    private final Color workerUnit;
    private final Color combatUnit;

    // Buildings
    private final Color buildingBarracks;
    private final Color buildingSupplyDepot;
    private final Color buildingCommandCenter;

    // Under-Construction Variants (darker)
    private final Color buildingUnderConstructionBarracks;
    private final Color buildingUnderConstructionSupplyDepot;
    private final Color buildingUnderConstructionCommandCenter;

    // Highlights & Selection
    private final Color selectionBox;
    private final Color unitHighlight;
    private final Color buildingHighlight;
    private final Color resourceHighlight;

    public FactionColors(Color workerUnit,
                         Color combatUnit,
                         Color buildingBarracks,
                         Color buildingSupplyDepot,
                         Color buildingCommandCenter,
                         Color buildingUnderConstructionBarracks,
                         Color buildingUnderConstructionSupplyDepot,
                         Color buildingUnderConstructionCommandCenter,
                         Color selectionBox,
                         Color unitHighlight,
                         Color buildingHighlight,
                         Color resourceHighlight) {
        this.workerUnit = workerUnit;
        this.combatUnit = combatUnit;
        this.buildingBarracks = buildingBarracks;
        this.buildingSupplyDepot = buildingSupplyDepot;
        this.buildingCommandCenter = buildingCommandCenter;
        this.buildingUnderConstructionBarracks = buildingUnderConstructionBarracks;
        this.buildingUnderConstructionSupplyDepot = buildingUnderConstructionSupplyDepot;
        this.buildingUnderConstructionCommandCenter = buildingUnderConstructionCommandCenter;
        this.selectionBox = selectionBox;
        this.unitHighlight = unitHighlight;
        this.buildingHighlight = buildingHighlight;
        this.resourceHighlight = resourceHighlight;
    }

    // Getters

    public Color getWorkerUnit() {
        return workerUnit;
    }

    public Color getResourceHighlight() {
        return resourceHighlight;
    }

    public Color getBuildingHighlight() {
        return buildingHighlight;
    }

    public Color getUnitHighlight() {
        return unitHighlight;
    }

    public Color getSelectionBox() {
        return selectionBox;
    }

    public Color getBuildingUnderConstructionCommandCenter() {
        return buildingUnderConstructionCommandCenter;
    }

    public Color getBuildingUnderConstructionSupplyDepot() {
        return buildingUnderConstructionSupplyDepot;
    }

    public Color getBuildingUnderConstructionBarracks() {
        return buildingUnderConstructionBarracks;
    }

    public Color getBuildingCommandCenter() {
        return buildingCommandCenter;
    }

    public Color getBuildingSupplyDepot() {
        return buildingSupplyDepot;
    }

    public Color getBuildingBarracks() {
        return buildingBarracks;
    }

    public Color getCombatUnit() {
        return combatUnit;
    }

}
