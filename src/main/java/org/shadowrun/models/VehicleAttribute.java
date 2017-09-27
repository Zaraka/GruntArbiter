package org.shadowrun.models;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class VehicleAttribute implements Observable {

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


    @Override
    public void addListener(InvalidationListener listener) {
        onRoad.addListener(listener);
        offRoad.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        onRoad.removeListener(listener);
        offRoad.removeListener(listener);
    }


}
