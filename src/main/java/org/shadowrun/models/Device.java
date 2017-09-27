package org.shadowrun.models;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Device implements Observable {

    private StringProperty name;

    private IntegerProperty rating;

    private IntegerProperty attack;

    private IntegerProperty sleeze;

    private IntegerProperty firewall;

    private IntegerProperty dataProcessing;

    private Monitor conditionMonitor;

    public Device(String name, int rating, int attack, int sleeze, int firewall, int dataProcessing) {
        this.name = new SimpleStringProperty(name);
        this.rating = new SimpleIntegerProperty(rating);
        this.attack = new SimpleIntegerProperty(attack);
        this.sleeze = new SimpleIntegerProperty(sleeze);
        this.firewall = new SimpleIntegerProperty(firewall);
        this.dataProcessing = new SimpleIntegerProperty(dataProcessing);
        this.conditionMonitor = new Monitor(8 + (rating / 2));
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

    public int getRating() {
        return rating.get();
    }

    public IntegerProperty ratingProperty() {
        return rating;
    }

    public int getAttack() {
        return attack.get();
    }

    public IntegerProperty attackProperty() {
        return attack;
    }

    public int getSleeze() {
        return sleeze.get();
    }

    public IntegerProperty sleezeProperty() {
        return sleeze;
    }

    public int getFirewall() {
        return firewall.get();
    }

    public IntegerProperty firewallProperty() {
        return firewall;
    }

    public int getDataProcessing() {
        return dataProcessing.get();
    }

    public IntegerProperty dataProcessingProperty() {
        return dataProcessing;
    }

    public Monitor getConditionMonitor() {
        return conditionMonitor;
    }

    @Override
    public void addListener(InvalidationListener listener) {
        name.addListener(listener);
        rating.addListener(listener);
        attack.addListener(listener);
        sleeze.addListener(listener);
        firewall.addListener(listener);
        dataProcessing.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        name.removeListener(listener);
        rating.removeListener(listener);
        attack.removeListener(listener);
        sleeze.removeListener(listener);
        firewall.removeListener(listener);
        dataProcessing.removeListener(listener);
    }
}
