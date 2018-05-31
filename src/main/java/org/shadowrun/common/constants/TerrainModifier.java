package org.shadowrun.common.constants;

public enum TerrainModifier {
    OPEN("Open", 0, "Highways, flat plains, open sea, clear sky"),
    LIGHT("Light", 1, "Main street thoroughfares, rolling hills, dock areas, instracity air traffic"),
    RESTRICTED("Restricted", 2, "Side streets, light woods, rocky mountain slopes, light traffic, " +
            "shallow waters, heavy air traffic"),
    OBSTRUCTED("Obstructed", 3, "Low-altitude flying over heavy terrain, hihg traffic, riptude currents"),
    TIGHT("Tight", 4, "Back alleys, heavy woods, steep slopes, driving against traffic, swamp, heavy rapids, " +
            "flying through winding canyons/citiscape"),
    IMPOSSIBLE("Impossible", 6, "Flying at street level through a city, ten-meter waves and hurricane widns, " +
            "driving in an office building, situations where you just don't want someone to tell you the odds!");

    private String name;

    private String description;

    private int modifier;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getModifier() {
        return modifier;
    }

    TerrainModifier(String name, int modifier, String description) {
        this.name = name;
        this.modifier = modifier;
        this.description = description;
    }
}
