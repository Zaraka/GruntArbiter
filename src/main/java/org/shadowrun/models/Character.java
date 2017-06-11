package org.shadowrun.models;

import javafx.beans.property.*;

public class Character {

    private StringProperty name;

    private IntegerProperty initiative;

    private ObjectProperty<World> world;

    public Character(String name, int initiative, World world) {
        this.name = new SimpleStringProperty(name);
        this.initiative = new SimpleIntegerProperty(initiative);
        this.world = new SimpleObjectProperty<>(world);
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

    public int countTurn(int turn) {
        return initiative.get() - turn * 10;
    }
}
