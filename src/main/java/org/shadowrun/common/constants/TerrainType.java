package org.shadowrun.common.constants;

public enum TerrainType {
    GROUND("Ground"),
    SKY("Sky"),
    WATER("Water");

    private String name;

    public String getName() {
        return name;
    }

    TerrainType(String name) {
        this.name = name;
    }
}
