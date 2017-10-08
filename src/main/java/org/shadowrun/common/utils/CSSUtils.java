package org.shadowrun.common.utils;

import org.shadowrun.models.Character;

import java.util.List;

public class CSSUtils {

    private CSSUtils() {}

    private static final String TABLE_ASTRAL = "table-astral";
    private static final String TABLE_MATRIX = "table-matrix";

    public static final String BUTTON_PRIMARY = "button-primary";

    public static void setCharacterBackground(Character character, List<String> cssClasses) {
        switch (character.getWorld()) {
            case REAL:
                cssClasses.remove(TABLE_ASTRAL);
                cssClasses.remove(TABLE_MATRIX);
                break;
            case ASTRAL:
                cssClasses.remove(TABLE_MATRIX);
                cssClasses.add(TABLE_ASTRAL);
                break;
            case MATRIX:
                cssClasses.remove(TABLE_ASTRAL);
                cssClasses.add(TABLE_MATRIX);
                break;
        }
    }
}
