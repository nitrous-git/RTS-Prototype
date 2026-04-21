package Faction.ColorPalette;

import java.awt.Color;

public class FactionPalettes {

    private FactionPalettes() {}

    /**
     * Faction A
     * Matte steel / slate blue
     */
    public static final FactionColors FACTION_A = new FactionColors(
            new Color(0x97c1dd), // Sky Blue | workerUnit
            new Color(0x3498DB),   // Light Steel Blue | combatUnit

            new Color(0x2980B9),  // Steel Blue (primary) | buildingBarracks
            new Color(0x1ABC9C), // Turquoise (accent) | buildingSupplyDepot
            new Color(0xE74C3C),   // Alizarin Red (anchor) | buildingCommandCenter

            new Color(0x2471A3),    // Belize Hole | buildingUnderConstructionBarracks
            new Color(0x16A085),   // Green Sea | buildingUnderConstructionSupplyDepot
            new Color(0xC0392B),    // Pomegranate | buildingUnderConstructionCommandCenter

            new Color(0x3498DB), // Turquoise | unitHighlight
            new Color(0x8FE4F4), // Light Blue | buildingHighlight
            new Color(0xBDC3C7)  // Silver | resourceHighlight
    );

    /**
     * Faction B
     * Matte obsidian / bronze command
     */
    public static final FactionColors FACTION_B = new FactionColors(
            new Color(0xD8C3A5), // Sand Bronze | workerUnit
            new Color(0xC27D3A), // Matte Amber Bronze | combatUnit

            new Color(0x8E5B3A), // Burnt Bronze | buildingBarracks
            new Color(0x4E8B8B), // Muted Teal | buildingSupplyDepot
            new Color(0xB0424F), // Dusty Crimson | buildingCommandCenter

            new Color(0x6E462E), // Dark Bronze | buildingUnderConstructionBarracks
            new Color(0x3B6B6B), // Dark Teal | buildingUnderConstructionSupplyDepot
            new Color(0x7F2F3A), // Dark Crimson | buildingUnderConstructionCommandCenter

            new Color(0xE0A458), // Unit highlight
            new Color(0x9DD9D2), // Building highlight
            new Color(0xB8BEC5)  // Resource highlight
    );

    /**
     * Faction C
     * Matte imperial violet / arcane sci-fi
     */
    public static final FactionColors FACTION_C = new FactionColors(
            new Color(0xB8A9D9), // Soft Lavender Steel | workerUnit
            new Color(0x7D5BA6), // Matte Violet | combatUnit

            new Color(0x5B4B8A), // Deep Indigo | buildingBarracks
            new Color(0x3FA7A3), // Muted Aqua | buildingSupplyDepot
            new Color(0xD96C6C), // Dusty Coral Red | buildingCommandCenter

            new Color(0x463A6B), // Dark Indigo | buildingUnderConstructionBarracks
            new Color(0x2F7F7C), // Dark Aqua | buildingUnderConstructionSupplyDepot
            new Color(0xA25050), // Dark Coral | buildingUnderConstructionCommandCenter

            new Color(0xC9B7F2), // Unit highlight
            new Color(0x9FE3DE), // Building highlight
            new Color(0xC7CCD6)  // Resource highlight
    );

    /**
     * Faction D
     * Matte tactical jade / frontier command
     */
    public static final FactionColors FACTION_D = new FactionColors(
            new Color(0x9FC4B2), // Soft Sage | workerUnit
            new Color(0x3E8F76), // Matte Jade | combatUnit

            new Color(0x2F6F74), // Blue-Green Steel | buildingBarracks
            new Color(0x7AA95C), // Muted Olive | buildingSupplyDepot
            new Color(0xC65D47), // Rust Command Red | buildingCommandCenter

            new Color(0x24565A), // Dark Blue-Green | buildingUnderConstructionBarracks
            new Color(0x5E8446), // Dark Olive | buildingUnderConstructionSupplyDepot
            new Color(0x944535), // Dark Rust | buildingUnderConstructionCommandCenter

            new Color(0x7FDCC2), // Unit highlight
            new Color(0xB7E6D8), // Building highlight
            new Color(0xBFC7CD)  // Resource highlight
    );

}
