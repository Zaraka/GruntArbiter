package org.shadowrun.models;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.ArrayList;
import java.util.Collections;

public class Host implements Observable {
    private IntegerProperty rating;

    private IntegerProperty attack;

    private IntegerProperty sleeze;

    private IntegerProperty firewall;

    private IntegerProperty dataProcessing;

    private ObservableMap<Character, IntegerProperty> connectedCharacters;

    public Host() {
        rating = new SimpleIntegerProperty(0);
        attack = new SimpleIntegerProperty(0);
        sleeze = new SimpleIntegerProperty(0);
        firewall = new SimpleIntegerProperty(0);
        dataProcessing = new SimpleIntegerProperty(0);
        connectedCharacters = FXCollections.observableHashMap();
    }

    public int getRating() {
        return rating.get();
    }

    public void setRating(int rating) {
        this.rating.setValue(rating);
    }

    public int getAttack() {
        return attack.get();
    }

    public void setAttack(int attack) {
        this.attack.setValue(attack);
    }

    public int getSleeze() {
        return sleeze.get();
    }

    public void setSleeze(int sleeze) {
        this.sleeze.setValue(sleeze);
    }

    public int getFirewall() {
        return firewall.get();
    }

    public void setFirewall(int firewall) {
        this.firewall.setValue(firewall);
    }

    public int getDataProcessing() {
        return dataProcessing.get();
    }

    public void setDataProcessing(int dataProcessing) {
        this.dataProcessing.setValue(dataProcessing);
    }

    public ObservableMap<Character, IntegerProperty> getConnectedCharacters() {
        return connectedCharacters;
    }

    public IntegerProperty ratingProperty() {
        return rating;
    }

    public IntegerProperty attackProperty() {
        return attack;
    }

    public IntegerProperty sleezeProperty() {
        return sleeze;
    }

    public IntegerProperty firewallProperty() {
        return firewall;
    }

    public IntegerProperty dataProcessingProperty() {
        return dataProcessing;
    }

    public void randomize(int rating) {
        this.rating.set(rating);
        ArrayList<Integer> ratings = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            ratings.add(rating + i);
        }
        Collections.shuffle(ratings);
        attack.set(ratings.get(0));
        sleeze.set(ratings.get(1));
        firewall.set(ratings.get(2));
        dataProcessing.set(ratings.get(3));
    }

    public BooleanBinding isInitalized() {
        return rating.isEqualTo(0).not();
    }

    @Override
    public void addListener(InvalidationListener listener) {
        rating.addListener(listener);
        attack.addListener(listener);
        sleeze.addListener(listener);
        firewall.addListener(listener);
        dataProcessing.addListener(listener);
    }
    
    @Override
    public void removeListener(InvalidationListener listener) {
        rating.removeListener(listener);
        attack.removeListener(listener);
        sleeze.removeListener(listener);
        firewall.removeListener(listener);
        dataProcessing.removeListener(listener);
    }
        
}