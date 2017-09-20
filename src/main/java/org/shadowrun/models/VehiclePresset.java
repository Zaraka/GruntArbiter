package org.shadowrun.models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class VehiclePresset {
    private ObjectProperty<Vehicle> vehicle;

    private ObservableList<VehiclePresset> children;

    public VehiclePresset(Vehicle vehicle) {
        this.vehicle = new SimpleObjectProperty<>(vehicle);

        children = FXCollections.observableArrayList();
    }

    public Vehicle getVehicle() {
        return vehicle.get();
    }

    public ObjectProperty<Vehicle> vehicleProperty() {
        return vehicle;
    }

    public ObservableList<VehiclePresset> getChildren() {
        return children;
    }
}
