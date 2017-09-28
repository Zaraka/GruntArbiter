package org.shadowrun.models;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Vehicle implements Observable {

    private StringProperty name;

    private Monitor conditionMonitor;

    private VehicleAttribute handling;

    private VehicleAttribute speed;

    private VehicleAttribute acceleration;

    private IntegerProperty body;

    private IntegerProperty armor;

    private IntegerProperty pilot;

    private IntegerProperty sensor;

    public Vehicle(String name,
                   int handlingOnRoad, int handlingOffRoad,
                   int speedOnRoad, int speedOffRoad,
                   int accelerationOnRoad, int accelerationOffRoad,
                   int body, int armor, int pilot, int sensor) {
        this.name = new SimpleStringProperty(name);
        this.handling = new VehicleAttribute(handlingOnRoad, handlingOffRoad);
        this.speed = new VehicleAttribute(speedOnRoad, speedOffRoad);
        this.acceleration = new VehicleAttribute(accelerationOnRoad, accelerationOffRoad);
        this.body = new SimpleIntegerProperty(body);
        this.armor = new SimpleIntegerProperty(armor);
        this.pilot = new SimpleIntegerProperty(pilot);
        this.sensor = new SimpleIntegerProperty(sensor);

        conditionMonitor = null;
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

    public Monitor getConditionMonitor() {
        if(conditionMonitor == null) {
            conditionMonitor = new Monitor(12 + new Double(Math.ceil(body.get() / 2)).intValue());
        }
        return conditionMonitor;
    }

    public VehicleAttribute getHandling() {
        return handling;
    }

    public VehicleAttribute getSpeed() {
        return speed;
    }

    public VehicleAttribute getAcceleration() {
        return acceleration;
    }

    public int getBody() {
        return body.get();
    }

    public IntegerProperty bodyProperty() {
        return body;
    }

    public int getArmor() {
        return armor.get();
    }

    public IntegerProperty armorProperty() {
        return armor;
    }

    public int getPilot() {
        return pilot.get();
    }

    public IntegerProperty pilotProperty() {
        return pilot;
    }

    public int getSensor() {
        return sensor.get();
    }

    public IntegerProperty sensorProperty() {
        return sensor;
    }

    public void setFrom(Vehicle other) {

        name.setValue(other.getName());
        handling.setFrom(other.getHandling());
        speed.setFrom(other.getSpeed());
        acceleration.setFrom(other.getAcceleration());
        body.setValue(other.getBody());
        armor.setValue(other.getArmor());
        pilot.setValue(other.getPilot());
        sensor.setValue(other.getSensor());
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "name=" + name +
                ", conditionMonitor=" + conditionMonitor +
                ", handling=" + handling +
                ", speed=" + speed +
                ", acceleration=" + acceleration +
                ", body=" + body +
                ", armor=" + armor +
                ", pilot=" + pilot +
                ", sensor=" + sensor +
                '}';
    }

    @Override
    public void addListener(InvalidationListener listener) {
        name.addListener(listener);
        handling.addListener(listener);
        speed.addListener(listener);
        acceleration.addListener(listener);
        getConditionMonitor().addListener(listener);
        body.addListener(listener);
        armor.addListener(listener);
        pilot.addListener(listener);
        sensor.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        name.removeListener(listener);
        handling.removeListener(listener);
        speed.removeListener(listener);
        acceleration.removeListener(listener);
        getConditionMonitor().removeListener(listener);
        body.removeListener(listener);
        armor.removeListener(listener);
        pilot.removeListener(listener);
        sensor.removeListener(listener);
    }
}
