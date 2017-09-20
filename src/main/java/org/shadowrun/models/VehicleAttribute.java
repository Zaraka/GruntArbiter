package org.shadowrun.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class VehicleAttribute {

    private IntegerProperty onRoad;

    private IntegerProperty offRoad;

    public VehicleAttribute(int onRoad) {
        this.onRoad = new SimpleIntegerProperty(onRoad);
        this.offRoad = new SimpleIntegerProperty(onRoad);
    }

    public VehicleAttribute(int onRoad, int offRoad) {
        this.onRoad = new SimpleIntegerProperty(onRoad);
        this.offRoad = new SimpleIntegerProperty(offRoad);
    }

    public int getOnRoad() {
        return onRoad.get();
    }

    public IntegerProperty onRoadProperty() {
        return onRoad;
    }

    public int getOffRoad() {
        return offRoad.get();
    }

    public IntegerProperty offRoadProperty() {
        return offRoad;
    }
}
