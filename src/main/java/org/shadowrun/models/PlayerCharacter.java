package org.shadowrun.models;


import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class PlayerCharacter implements Identificable, Observable {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerCharacter.class);

    private StringProperty uuid;

    private StringProperty name;

    private IntegerProperty physicalMonitor;

    private IntegerProperty stunMonitor;

    private IntegerProperty spiritIndex;

    private SerializableImage portrait;

    public PlayerCharacter(String name, int physicalMonitor, int stunMonitor, int spiritIndex) {
        this.name = new SimpleStringProperty(name);
        this.physicalMonitor = new SimpleIntegerProperty(physicalMonitor);
        this.stunMonitor = new SimpleIntegerProperty(stunMonitor);
        this.spiritIndex = new SimpleIntegerProperty(spiritIndex);
        this.uuid = new SimpleStringProperty(UUID.randomUUID().toString());
        this.portrait = new SerializableImage();
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

    public SerializableImage getPortrait() {
        return portrait;
    }

    @Override
    public String getUuid() {
        return uuid.get();
    }

    @Override
    public void addListener(InvalidationListener listener) {
        name.addListener(listener);
        physicalMonitor.addListener(listener);
        stunMonitor.addListener(listener);
        spiritIndex.addListener(listener);
        portrait.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        name.removeListener(listener);
        physicalMonitor.removeListener(listener);
        stunMonitor.removeListener(listener);
        spiritIndex.removeListener(listener);
        portrait.removeListener(listener);
    }
}
