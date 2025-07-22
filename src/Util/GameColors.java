package Util;

import java.awt.Color;

public final class GameColors {

    private GameColors() {}

    // Walls & Grid
    public static final Color WALL                                = new Color(0x2C3E50); // Midnight Blue (neutral background)
    public static final Color GRID_LINES                          = new Color(0x34495E); // Wet Asphalt (subtle grid)

    // Buildings
    public static final Color BUILDING_BARRACKS                   = new Color(0x2980B9); // Steel Blue (primary)
    public static final Color BUILDING_SUPPLY_DEPOT               = new Color(0x1ABC9C); // Turquoise (accent)
    public static final Color BUILDING_COMMAND_CENTER             = new Color(0xE74C3C); // Alizarin Red (anchor)

    // Under-Construction Variants (darker)
    public static final Color BUILDING_BARRACKS_UNDER_CONSTRUCTION       = new Color(0x2471A3); // Belize Hole (under construction)
    public static final Color BUILDING_SUPPLY_DEPOT_UNDER_CONSTRUCTION   = new Color(0x16A085); // Green Sea (under construction)
    public static final Color BUILDING_COMMAND_CENTER_UNDER_CONSTRUCTION = new Color(0xC0392B); // Pomegranate (under construction)

    // Units
    public static final Color UNIT_PLAYER_WORKER              = new Color(0x97c1dd); // Sky Blue (worker) 0x3498DB
    public static final Color UNIT_PLAYER_COMBAT              = new Color(0x3498DB); // Light Steel Blue (combat) 0x97c1dd
    public static final Color UNIT_ENEMY_WORKER               = new Color(0xEC7063); // Terra Cotta (opponent)
    public static final Color UNIT_ENEMY_COMBAT               = new Color(0xC0392B); // Pomegranate Red (threat)

    // Resource Nodes
    public static final Color RESOURCE_NODE_MINERAL           = new Color(0x00BFFF); // Deep Sky Blue (minerals)
    public static final Color RESOURCE_NODE_GAS               = new Color(0x7F8C8D); // Concrete Gray (gas)

    // Highlights & Selection
    public static final Color SELECTION_BOX                   = new Color(0x1ABC9C); // Turquoise (selection)
    public static final Color UNIT_HIGHLIGHT                  = new Color(0x3498DB); // Light Blue (unit focus)
    public static final Color BUILDING_HIGHLIGHT              = new Color(0x8FE4F4); // Light Cyan (building focus)
    public static final Color RESOURCE_HIGHLIGHT              = new Color(0xBDC3C7); // Silver (resource focus)

    // Helper Tile Highlights
    public static final Color MOVEMENT_TILE_HELPER                   = new Color(0x34495E); // Wet Asphalt (movement tile)
    public static final Color BUILDING_PLACEMENT_TILE_HELPER         = new Color(0x2C3E50); // Midnight Blue (placement tile)

    /*
    // Walls & Grid
    public static final Color WALL                    = new Color(0x333333); // Dark Gray (neutral background)
    public static final Color GRID_LINES              = new Color(0x4F4F4F); // Mid-Gray (subtle grid)
oo
    // Buildings
    public static final Color BUILDING_BARRACKS       = new Color(0x8B6D2F); // Warm Dark Gold (primary)
    public static final Color BUILDING_SUPPLY_DEPOT   = new Color(0xC1A66B); // Matte Yellow (accent)
    public static final Color BUILDING_COMMAND_CENTER = new Color(0xA33E3E); // Muted Red (anchor)

    // Under-Construction Variants (darker)
    public static final Color BUILDING_BARRACKS_UNDER_CONSTRUCTION       = new Color(0x936B09); // Darker Goldenrod (under construction)
    public static final Color BUILDING_SUPPLY_DEPOT_UNDER_CONSTRUCTION   = new Color(0xAA8C2C); // Darker Gold (under construction)
    public static final Color BUILDING_COMMAND_CENTER_UNDER_CONSTRUCTION = new Color(0x992D22); // Darker Muted Red (under construction)

    // Units
    public static final Color UNIT_PLAYER_WORKER      = new Color(0xD4BA66); // Light Matte Yellow (worker)
    public static final Color UNIT_PLAYER_COMBAT      = new Color(0xBF2F2F); // Warm Red (combat)
    public static final Color UNIT_ENEMY_WORKER       = new Color(0x7D7D7D); // Stone Gray (opponent)
    public static final Color UNIT_ENEMY_COMBAT       = new Color(0x4F4F4F); // Mid-Gray (threat)

    // Resource Nodes
    public static final Color RESOURCE_NODE_MINERAL   = new Color(0xD4AF37); // Classic Gold (minerals)
    public static final Color RESOURCE_NODE_GAS       = new Color(0x9E9E9E); // Industrial Gray (gas)

    // Highlights & Selection
    public static final Color SELECTION_BOX           = new Color(0xFFE8A1); // Soft Gold (selection)
    public static final Color UNIT_HIGHLIGHT          = new Color(0xFFADA2); // Light Red (unit focus)
    public static final Color BUILDING_HIGHLIGHT      = new Color(0xFFF1B5); // Pale Yellow (building focus)
    public static final Color RESOURCE_HIGHLIGHT      = new Color(0xE0E0E0); // Light Gray (resource focus)

    // Helper Tile Highlights
    public static final Color MOVEMENT_TILE_HELPER             = new Color(0x7F8C8D); // Cool Gray (movement tile)
    public static final Color BUILDING_PLACEMENT_TILE_HELPER   = new Color(0x5A5A5A); // Medium Gray (placement tile)
     */
}
