package org.shadowrun.models;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PlayerCharacter {

    private StringProperty name;

    private IntegerProperty physicalMonitor;

    private IntegerProperty stunMonitor;

    private IntegerProperty spiritIndex;

    public PlayerCharacter(String name, int physicalMonitor, int stunMonitor, int spiritIndex) {
        this.name = new SimpleStringProperty(name);
        this.physicalMonitor = new SimpleIntegerProperty(physicalMonitor);
        this.stunMonitor = new SimpleIntegerProperty(stunMonitor);
        this.spiritIndex = new SimpleIntegerProperty(spiritIndex);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getPhysicalMonitor() {
        return physicalMonitor.get();
    }

    public IntegerProperty physicalMonitorProperty() {
        return physicalMonitor;
    }

    public void setPhysicalMonitor(int physicalMonitor) {
        this.physicalMonitor.set(physicalMonitor);
    }

    public int getSpiritIndex() {
        return spiritIndex.get();
    }

    public IntegerProperty spiritIndexProperty() {
        return spiritIndex;
    }

    public void setSpiritIndex(int spiritIndex) {
        this.spiritIndex.set(spiritIndex);
    }

    public int getStunMonitor() {
        return stunMonitor.get();
    }

    public IntegerProperty stunMonitorProperty() {
        return stunMonitor;
    }

    public void setStunMonitor(int stunMonitor) {
        this.stunMonitor.set(stunMonitor);
    }
}
