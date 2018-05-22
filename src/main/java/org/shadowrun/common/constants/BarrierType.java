package org.shadowrun.common.constants;

public enum BarrierType {

    FRAGILE("Fragile", "Example: standard glass", 1, 2),
    CHEAP_MATERIAL("Cheap Material", "Example: Drywall, plaster, door, regural tire", 2, 4),
    AVERAGE_MATERIAL("Average Material", "Example: furniture, plastiboard, ballistic glass", 4, 6),
    HEAVY_MATERIAL("Heavy Material", "Example: tree, hardwood, dataterm, light post, chain link", 6, 8),
    REINFORCED_MATERIAL("Reinforced Material", "Example: densiplast, security door, armored glass, Kevlar wallboard", 8, 12),
    STRUCTURAL_MATERIAL("Structural Material", "Example: brick, plascrete", 10, 16),
    HEAVY_STRUCTURAL_MATERIAL("Heavy Structural Material", "Example: concrete, metal beam", 12, 20),
    ARMORED_REINFORCED_MATERIAL("Armored/Reinforced Material", "Example: reinforced concrete", 14, 24),
    HARDENED_MATERIAL("Hardened Material", "Example: blast bunkers", 16, 32);

    private String name;

    private String description;

    private int structure;

    private int armor;

    BarrierType(String name, String description, int structure, int armor) {
        this.name = name;
        this.description = description;
        this.structure = structure;
        this.armor = armor;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getStructure() {
        return structure;
    }

    public int getArmor() {
        return armor;
    }
}
