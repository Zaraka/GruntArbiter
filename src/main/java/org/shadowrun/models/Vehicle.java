package org.shadowrun.models;

import javafx.beans.property.*;

public class Vehicle {

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
}
