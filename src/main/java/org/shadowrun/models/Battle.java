package org.shadowrun.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Battle {

    private ObjectProperty<Host> host;

    private IntegerProperty backgroundCount;

    private ObservableList<Character> characters;


    public Battle() {
        backgroundCount = new SimpleIntegerProperty();
        characters = FXCollections.observableArrayList();
    }

    public Host getHost() {
        return host.get();
    }

    public ObjectProperty<Host> hostProperty() {
        return host;
    }

    public int getBackgroundCount() {
        return backgroundCount.get();
    }

    public IntegerProperty backgroundCountProperty() {
        return backgroundCount;
    }

    public ObservableList<Character> getCharacters() {
        return characters;
    }
}
