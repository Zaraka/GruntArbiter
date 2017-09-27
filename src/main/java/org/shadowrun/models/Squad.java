package org.shadowrun.models;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.lang3.StringUtils;

public class Squad implements Observable {

    private StringProperty name;

    private ObservableList<Character> characters;

    public Squad() {
        this.characters = FXCollections.observableArrayList();
        this.name = new SimpleStringProperty(StringUtils.EMPTY);
    }

    public ObservableList<Character> getCharacters() {
        return characters;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public void addListener(InvalidationListener listener) {
        characters = FXCollections.observableList(characters, param -> new Observable[]{param});

        characters.addListener(listener);
        name.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        characters.removeListener(listener);
        characters.removeListener(listener);
    }
}
