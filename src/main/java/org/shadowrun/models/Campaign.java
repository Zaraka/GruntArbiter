package org.shadowrun.models;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.lang3.StringUtils;

/**
 * WARNING:
 * This Campaign class is also used as save file.
 * Everything in this class is serialized when saving Grunt Arbiter campaign.
 * That means, changing this class attributes will create issue with backward compability.
 * So all changes HAVE to raise Major version.
 */
public class Campaign implements Observable {

    private StringProperty name;

    private StringProperty description;

    private ObjectProperty<SemanticVersion> version;

    private ObservableList<PlayerCharacter> players;

    private ObservableList<Character> characterPresets;

    private ObservableList<Device> devicePressets;

    private ObservableList<Squad> squads;

    private ObservableList<Battle> battles;

    public Campaign(String name, SemanticVersion version) {
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(StringUtils.EMPTY);
        this.version = new SimpleObjectProperty<>(version);
        this.players = FXCollections.observableArrayList();
        this.characterPresets = FXCollections.observableArrayList();
        this.devicePressets = FXCollections.observableArrayList();
        this.battles = FXCollections.observableArrayList();
        this.squads = FXCollections.observableArrayList();
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

    public ObservableList<PlayerCharacter> getPlayers() {
        return players;
    }

    public void setPlayers(ObservableList<PlayerCharacter> players) {
        this.players = players;
    }

    public ObservableList<Character> getCharacterPresets() {
        return characterPresets;
    }

    public void setCharacterPresets(ObservableList<Character> characterPresets) {
        this.characterPresets = characterPresets;
    }

    public ObservableList<Battle> getBattles() {
        return battles;
    }

    public SemanticVersion getVersion() {
        return version.get();
    }

    public ObjectProperty<SemanticVersion> versionProperty() {
        return version;
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public ObservableList<Squad> getSquads() {
        return squads;
    }

    public ObservableList<Device> getDevicePressets() {
        return devicePressets;
    }

    @Override
    public void addListener(InvalidationListener listener) {
        players = FXCollections.observableList(players, param -> new Observable[]{param});
        characterPresets = FXCollections.observableList(characterPresets, param -> new Observable[]{param});
        squads = FXCollections.observableList(squads, param -> new Observable[]{param});
        devicePressets = FXCollections.observableList(devicePressets, param -> new Observable[]{param});
        battles = FXCollections.observableList(battles, param -> new Observable[]{param});

        name.addListener(listener);
        description.addListener(listener);
        version.addListener(listener);
        players.addListener(listener);
        characterPresets.addListener(listener);
        squads.addListener(listener);
        devicePressets.addListener(listener);
        battles.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        name.removeListener(listener);
        description.removeListener(listener);
        version.removeListener(listener);
        players.removeListener(listener);
        characterPresets.removeListener(listener);
        squads.removeListener(listener);
        devicePressets.removeListener(listener);
        battles.removeListener(listener);
    }
}
