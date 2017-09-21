package org.shadowrun.models;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.ArrayList;
import java.util.Collections;

public class Host {
    private IntegerProperty rating;

    private IntegerProperty attack;

    private IntegerProperty sleeze;

    private IntegerProperty firewall;

    private IntegerProperty dataProcessing;

    private IntegerProperty overwatchScore;

    private ObservableMap<Character, IntegerProperty> connectedCharacters;

    public Host() {
        rating = new SimpleIntegerProperty(0);
        attack = new SimpleIntegerProperty(0);
        sleeze = new SimpleIntegerProperty(0);
        firewall = new SimpleIntegerProperty(0);
        dataProcessing = new SimpleIntegerProperty(0);
        overwatchScore = new SimpleIntegerProperty(0);
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

    public int getOverwatchScore() {
        return overwatchScore.get();
    }

    public void setOverwatchScore(int overwatchScore) {
        this.overwatchScore.setValue(overwatchScore);
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

    public IntegerProperty overwatchScoreProperty() {
        return overwatchScore;
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
}