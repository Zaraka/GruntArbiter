package org.shadowrun.models;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.shadowrun.common.constants.BarrierType;

public class Barrier implements Observable {

    private StringProperty name;

    private BarrierType type;

    private Monitor structureMonitor;

    private IntegerProperty armor;

    public Barrier(String name, BarrierType barrierType, Integer structure, Integer armor) {
        this.name = new SimpleStringProperty(name);
        this.type = barrierType;
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

    public BarrierType getType() {
        return type;
    }

    public void setType(BarrierType type) {
        this.type = type;
    }

    @Override
    public void addListener(InvalidationListener listener) {
        name.addListener(listener);
        structureMonitor.addListener(listener);
        armor.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        name.removeListener(listener);
        structureMonitor.removeListener(listener);
        armor.removeListener(listener);
    }
}
