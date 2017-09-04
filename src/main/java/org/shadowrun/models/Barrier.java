package org.shadowrun.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Barrier {

    private StringProperty name;

    private Monitor structureMonitor;

    private IntegerProperty armor;

    public Barrier(String name, Integer structure, Integer armor) {
        this.name = new SimpleStringProperty(name);
        this.structureMonitor = new Monitor(structure);
        this.armor = new SimpleIntegerProperty(armor);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public Monitor getStructureMonitor() {
        return structureMonitor;
    }

    public int getArmor() {
        return armor.get();
    }

    public IntegerProperty armorProperty() {
        return armor;
    }

    public void setName(String name) {
        this.name.set(name);
    }
}
