package org.shadowrun.models;

import javafx.beans.property.*;
import org.shadowrun.common.constants.World;

public class Character {

    private StringProperty name;

    private IntegerProperty initiative;

    private ObjectProperty<World> world;

    private BooleanProperty npc;

    private BooleanProperty ice;

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
        this.ice = new SimpleBooleanProperty(false);
        this.physicalMonitor = new SimpleIntegerProperty(0);
        this.stunMonitor = new SimpleIntegerProperty(0);
    }

    public Character(String name, int initiative, World world, boolean npc, boolean ice, int monitor) {
        this.name = new SimpleStringProperty(name);
        this.initiative = new SimpleIntegerProperty(initiative);
        this.world = new SimpleObjectProperty<>(world);
        this.npc = new SimpleBooleanProperty(npc);
        this.ice = new SimpleBooleanProperty(ice);
        this.physicalMonitor = new SimpleIntegerProperty(monitor);
        this.stunMonitor = new SimpleIntegerProperty((ice) ? 0 : monitor);
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

    public boolean isIce() {
        return ice.get();
    }

    public BooleanProperty iceProperty() {
        return ice;
    }


}
