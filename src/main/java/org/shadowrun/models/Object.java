package org.shadowrun.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Object {

    private StringProperty name;

    private IntegerProperty structure;

    private IntegerProperty armor;

    public Object(String name, Integer structure, Integer armor) {
        this.name = new SimpleStringProperty(name);
        this.structure = new SimpleIntegerProperty(structure);
        this.armor = new SimpleIntegerProperty(armor);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public int getStructure() {
        return structure.get();
    }

    public IntegerProperty structureProperty() {
        return structure;
    }

    public int getArmor() {
        return armor.get();
    }

    public IntegerProperty armorProperty() {
        return armor;
    }
}
