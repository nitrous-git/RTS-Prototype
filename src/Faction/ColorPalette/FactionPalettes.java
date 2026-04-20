package Faction.ColorPalette;

import java.awt.Color;

public class FactionPalettes {

    private FactionPalettes() {}

    /**
     * Faction A
     * Matte steel / slate blue
     */
    public static final FactionColors FACTION_A = new FactionColors(
            new Color(111, 133, 156), // workerUnit
            new Color(74, 98, 122),   // combatUnit

            new Color(92, 114, 136),  // buildingBarracks
            new Color(105, 126, 145), // buildingSupplyDepot
            new Color(63, 83, 104),   // buildingCommandCenter

            new Color(63, 79, 95),    // buildingUnderConstructionBarracks
            new Color(74, 89, 102),   // buildingUnderConstructionSupplyDepot
            new Color(45, 59, 74),    // buildingUnderConstructionCommandCenter

            new Color(154, 180, 205), // selectionBox
            new Color(184, 201, 217), // unitHighlight
            new Color(164, 183, 198), // buildingHighlight
            new Color(104, 157, 168)  // resourceHighlight
    );

    /**
     * Faction B
     * Matte ember / muted crimson
     */
    public static final FactionColors FACTION_B = new FactionColors(
            new Color(168, 109, 113), // workerUnit
            new Color(129, 76, 81),   // combatUnit

            new Color(145, 88, 93),   // buildingBarracks
            new Color(156, 100, 94),  // buildingSupplyDepot
            new Color(101, 58, 63),   // buildingCommandCenter

            new Color(98, 61, 65),    // buildingUnderConstructionBarracks
            new Color(108, 70, 66),   // buildingUnderConstructionSupplyDepot
            new Color(70, 41, 45),    // buildingUnderConstructionCommandCenter

            new Color(208, 164, 166), // selectionBox
            new Color(221, 190, 191), // unitHighlight
            new Color(205, 173, 170), // buildingHighlight
            new Color(182, 138, 104)  // resourceHighlight
    );

    /**
     * Faction C
     * Matte jade / desaturated teal
     */
    public static final FactionColors FACTION_C = new FactionColors(
            new Color(104, 148, 136), // workerUnit
            new Color(71, 110, 100),  // combatUnit

            new Color(87, 125, 114),  // buildingBarracks
            new Color(99, 137, 125),  // buildingSupplyDepot
            new Color(58, 89, 81),    // buildingCommandCenter

            new Color(60, 86, 79),    // buildingUnderConstructionBarracks
            new Color(69, 98, 90),    // buildingUnderConstructionSupplyDepot
            new Color(41, 64, 58),    // buildingUnderConstructionCommandCenter

            new Color(160, 198, 188), // selectionBox
            new Color(191, 219, 212), // unitHighlight
            new Color(173, 204, 195), // buildingHighlight
            new Color(111, 168, 153)  // resourceHighlight
    );

    /**
     * Faction D
     * Matte sand / ash gold
     */
    public static final FactionColors FACTION_D = new FactionColors(
            new Color(173, 152, 110), // workerUnit
            new Color(132, 113, 77),  // combatUnit

            new Color(148, 128, 91),  // buildingBarracks
            new Color(160, 141, 103), // buildingSupplyDepot
            new Color(104, 88, 58),   // buildingCommandCenter

            new Color(101, 86, 60),   // buildingUnderConstructionBarracks
            new Color(112, 96, 68),   // buildingUnderConstructionSupplyDepot
            new Color(74, 62, 41),    // buildingUnderConstructionCommandCenter

            new Color(210, 194, 155), // selectionBox
            new Color(224, 214, 188), // unitHighlight
            new Color(210, 198, 170), // buildingHighlight
            new Color(186, 169, 115)  // resourceHighlight
    );

}
