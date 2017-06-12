package org.shadowrun.models;

import javafx.beans.property.*;

public class Character {

    private StringProperty name;

    private IntegerProperty initiative;

    private ObjectProperty<World> world;

    private BooleanProperty npc;

    private BooleanProperty selected;

    private IntegerProperty physicalMonitor;    //or Matrix or whatever

    private IntegerProperty stunMonitor;

    /**
     * For creating players
     * @param name
     * @param initiative
     * @param world
     */
    public Character(String name, int initiative, World world) {
        this.name = new SimpleStringProperty(name);
        this.initiative = new SimpleIntegerProperty(initiative);
        this.world = new SimpleObjectProperty<>(world);
        this.npc = new SimpleBooleanProperty(false);
        this.selected = new SimpleBooleanProperty(false);
        this.physicalMonitor = new SimpleIntegerProperty(0);
        this.stunMonitor = new SimpleIntegerProperty(0);
    }

    public Character(String name, int initiative, World world, boolean npc, int monitor) {
        this.name = new SimpleStringProperty(name);
        this.initiative = new SimpleIntegerProperty(initiative);
        this.world = new SimpleObjectProperty<>(world);
        this.npc = new SimpleBooleanProperty(npc);
        this.selected = new SimpleBooleanProperty(false);
        this.physicalMonitor = new SimpleIntegerProperty(monitor);
        this.stunMonitor = new SimpleIntegerProperty(monitor);
    }


    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    public int getInitiative() {
        return initiative.get();
    }

    public void setInitiative(int initiative) {
        this.initiative.setValue(initiative);
    }

    public World getWorld() {
        return world.get();
    }

    public void setWorld(World world) {
        this.world.setValue(world);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public IntegerProperty initiativeProperty() {
        return initiative;
    }

    public ObjectProperty<World> worldProperty() {
        return world;
    }

    public boolean isNpc() {
        return npc.get();
    }

    public BooleanProperty npcProperty() {
        return npc;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setNpc(boolean npc) {
        this.npc.set(npc);
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public int countTurn(int turn) {
        return initiative.get() - (turn - 1) * 10;
    }

    public int getPhysicalMonitor() {
        return physicalMonitor.get();
    }

    public IntegerProperty physicalMonitorProperty() {
        return physicalMonitor;
    }

    public int getStunMonitor() {
        return stunMonitor.get();
    }

    public IntegerProperty stunMonitorProperty() {
        return stunMonitor;
    }
}
