package org.shadowrun.models;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.StringProperty;
import org.shadowrun.common.constants.CompanionType;

public class Companion implements Observable {

    private Observable companion;

    private CompanionType companionType;

    public Companion(Character character) {
        companion = character;
        companionType = CompanionType.CHARACTER;
    }

    public Companion(Device device) {
        companion = device;
        companionType = CompanionType.DEVICE;
    }

    public Companion(Vehicle vehicle) {
        companion = vehicle;
        companionType = CompanionType.VEHICLE;
    }

    public StringProperty nameProperty() {
        switch (companionType) {
            case DEVICE:
                return ((Device) companion).nameProperty();
            case VEHICLE:
                return ((Vehicle) companion).nameProperty();
            default:
            case CHARACTER:
                return ((Character) companion).nameProperty();
        }
    }

    public Observable getCompanion() {
        return companion;
    }

    public CompanionType getCompanionType() {
        return companionType;
    }

    @Override
    public void addListener(InvalidationListener listener) {
        companion.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        companion.removeListener(listener);
    }
}
