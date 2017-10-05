package org.shadowrun.models;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.shadowrun.common.constants.CharacterType;
import org.shadowrun.common.constants.World;

import java.util.List;
import java.util.UUID;

public class Character implements Comparable<Character>, Identificable, Observable {

    private StringProperty uuid;

    private StringProperty name;

    private IntegerProperty initiative;

    private ObjectProperty<World> world;

    private BooleanProperty npc;

    private BooleanProperty ice;

    private ObjectProperty<CharacterType> type;

    private Monitor physicalMonitor;    //or Matrix or whatever

    private Monitor stunMonitor;

    private StringProperty playerUUID;

    private ObservableList<Companion> companions;

    private SerializableImage portrait;

    public Character(String name,
                     int initiative, World world,
                     boolean npc, boolean ice, CharacterType type,
                     int physicalMonitor, int stunMonitor,
                     PlayerCharacter player, List<Companion> companions) {
        this.name = new SimpleStringProperty(name);
        this.initiative = new SimpleIntegerProperty(initiative);
        this.world = new SimpleObjectProperty<>(world);
        this.npc = new SimpleBooleanProperty(npc);
        this.ice = new SimpleBooleanProperty(ice);
        this.physicalMonitor = new Monitor(physicalMonitor);
        this.stunMonitor = new Monitor((ice) ? 0 : stunMonitor);
        this.playerUUID = new SimpleStringProperty((player == null) ? null : player.getUuid());
        this.companions = (companions == null) ? FXCollections.observableArrayList() : FXCollections.observableArrayList(companions);
        this.type = new SimpleObjectProperty<>(type);
        this.uuid = new SimpleStringProperty(UUID.randomUUID().toString());
        this.portrait = new SerializableImage();
        if(player != null) {
            this.portrait.imageProperty().setValue(player.getPortrait().imageProperty().get());
        }
    }

    public Character(Character character) {
        this.name = new SimpleStringProperty(character.getName());
        this.initiative = new SimpleIntegerProperty(character.getInitiative());
        this.world = new SimpleObjectProperty<>(character.getWorld());
        this.npc = new SimpleBooleanProperty(character.isNpc());
        this.ice = new SimpleBooleanProperty(character.isIce());
        this.physicalMonitor = new Monitor(character.getPhysicalMonitor());
        this.stunMonitor = new Monitor(character.getStunMonitor());
        this.playerUUID = new SimpleStringProperty(null);
        this.companions = FXCollections.observableArrayList();
        this.type = new SimpleObjectProperty<CharacterType>(character.getType());
        this.uuid = new SimpleStringProperty(UUID.randomUUID().toString());
        this.portrait = new SerializableImage(character.getPortrait());
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public int getInitiative() {
        return initiative.get();
    }

    public void setInitiative(int initiative) {
        this.initiative.setValue(initiative);
    }

    public IntegerProperty initiativeProperty() {
        return initiative;
    }

    public World getWorld() {
        return world.get();
    }

    public void setWorld(World world) {
        this.world.setValue(world);
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

    public Monitor getPhysicalMonitor() {
        return physicalMonitor;
    }

    public Monitor getStunMonitor() {
        return stunMonitor;
    }

    public boolean isIce() {
        return ice.get();
    }

    public BooleanProperty iceProperty() {
        return ice;
    }

    public ObservableList<Companion> getCompanions() {
        return companions;
    }

    public CharacterType getType() {
        return type.get();
    }

    public ObjectProperty<CharacterType> typeProperty() {
        return type;
    }

    public String getPlayerUUID() {
        return playerUUID.get();
    }

    public StringProperty playerUUIDProperty() {
        return playerUUID;
    }

    public SerializableImage getPortrait() {
        return portrait;
    }

    public void setFrom(Character other) {
        name.setValue(other.getName());
        initiative.setValue(other.getInitiative());
        world.setValue(other.getWorld());
        npc.setValue(other.isNpc());
        ice.setValue(other.isIce());
        physicalMonitor.setFrom(other.getPhysicalMonitor());
        stunMonitor.setFrom(other.getStunMonitor());
        playerUUID.setValue(other.getPlayerUUID());
        companions = other.companions;
        type.setValue(other.getType());
    }

    @Override
    public int compareTo(Character o) {
        return (o == null) ? 0 : this.getInitiative() - o.getInitiative();
    }

    @Override
    public void addListener(InvalidationListener listener) {
        companions = FXCollections.observableList(companions, param -> new Observable[]{param});

        name.addListener(listener);
        initiative.addListener(listener);
        world.addListener(listener);
        ice.addListener(listener);
        npc.addListener(listener);
        playerUUID.addListener(listener);
        physicalMonitor.addListener(listener);
        stunMonitor.addListener(listener);
        companions.addListener(listener);
        type.addListener(listener);
        portrait.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        initiative.removeListener(listener);
        name.removeListener(listener);
        world.removeListener(listener);
        ice.removeListener(listener);
        npc.removeListener(listener);
        playerUUID.removeListener(listener);
        physicalMonitor.removeListener(listener);
        stunMonitor.removeListener(listener);
        companions.removeListener(listener);
        type.removeListener(listener);
        portrait.removeListener(listener);
    }

    @Override
    public String getUuid() {
        return uuid.get();
    }
}
