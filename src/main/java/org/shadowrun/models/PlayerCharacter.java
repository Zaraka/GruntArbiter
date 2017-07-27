package org.shadowrun.models;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PlayerCharacter {

    private StringProperty name;

    private IntegerProperty condition;

    public PlayerCharacter(String name, int condition) {
        this.name = new SimpleStringProperty(name);
        this.condition = new SimpleIntegerProperty(condition);
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

    public int getCondition() {
        return condition.get();
    }

    public IntegerProperty conditionProperty() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition.set(condition);
    }
}
