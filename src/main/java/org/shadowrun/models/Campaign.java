package org.shadowrun.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Campaign {

    private StringProperty name;

    private ObservableList<PlayerCharacter> players;

    private ObservableList<Character> characterPresets;

    public Campaign(String name) {
        this.name = new SimpleStringProperty(name);
        this.players = FXCollections.observableArrayList();
        this.characterPresets = FXCollections.observableArrayList();
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
}
