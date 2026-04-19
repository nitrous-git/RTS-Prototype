package Util;

import javax.swing.UIManager;

import com.formdev.flatlaf.intellijthemes.FlatCarbonIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTMaterialDarkerIJTheme;

public final class AppTheme {

    public enum ThemeMode {
        FLAT_CARBON,
        FLAT_DARK_PURPLE,
        ARC_DARK_ORANGE,
        MATERIAL_DARKER
    }

    private AppTheme() {}

    public static void setupTheme(ThemeMode themeMode) {
        try {

            switch (themeMode) {
                case FLAT_CARBON -> FlatCarbonIJTheme.setup();
                case FLAT_DARK_PURPLE -> FlatDarkPurpleIJTheme.setup();
                case ARC_DARK_ORANGE -> FlatArcDarkOrangeIJTheme.setup();
                case MATERIAL_DARKER -> FlatMTMaterialDarkerIJTheme.setup();
            }

            applyGlobalTuning();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void applyGlobalTuning() {
        System.setProperty("flatlaf.useWindowDecorations", Boolean.toString(false));

        UIManager.put("Component.arc", 8);
        UIManager.put("Button.arc", 2);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("Component.innerFocusWidth", 11);
        UIManager.put("Button.innerFocusWidth", 1);
        UIManager.put("Component.arrowType", "chevron");
    }
}