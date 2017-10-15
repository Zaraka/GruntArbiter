package org.shadowrun.common.constants;

public enum NoisePreset {

    NONE("None", 0),
    SPAM("Spam zone"),
    CITY("City downtown", 1),
    SPRAWL("Sprawl downtown", 2),
    EVENT("Major event or advertising blitz", 3),
    CITY_COMMERCIAL("Commercial area in a city", 4),
    SPRAWL_COMMERCIAL("Commercial area in a sprawl", 5),
    EMERGENCY("Massive gathering or during widespread emergency", 6),
    STATIC("Static zone"),
    BUILDING("Abandoned building", 1),
    NEIGHBOURHOOD("Abandoned neighbourhood, barrens", 2),
    RURAL("Rural area, abandoned ungerground area, heavy rain or snow", 3),
    WILDERNESS("Wilderness, severe storm", 4),
    REMOTE_SATELLITE("Remote place with satellite access only", 5),
    CAVE("Remote, enclosed place (cave, desert ruin)", 6);

    private String name;
    private int noise;
    private boolean category;

    NoisePreset(String name) {
        this.name = name;
        this.noise = 0;
        this.category = true;
    }

    NoisePreset(String name, int noise) {
        this.name = name;
        this.noise = noise;
        this.category = false;
    }

    public String getName() {
        return name;
    }

    public int getNoise() {
        return noise;
    }

    public boolean isCategory() {
        return category;
    }
}
