package org.shadowrun.common.constants;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;

public enum CompanionType {

    CHARACTER("Character", FontAwesomeIcon.MALE),
    VEHICLE("Vehicle", FontAwesomeIcon.CAR),
    DEVICE("Device", FontAwesomeIcon.MOBILE);

    private String name;
    private FontAwesomeIcon icon;

    CompanionType(String name, FontAwesomeIcon icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public FontAwesomeIcon getIcon() {
        return icon;
    }
}
