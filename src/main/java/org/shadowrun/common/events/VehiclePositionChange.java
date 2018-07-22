package org.shadowrun.common.events;

import javafx.event.ActionEvent;

public class VehiclePositionChange extends ActionEvent {

    private String vehicle;
    private Integer position;

    public VehiclePositionChange(String vehicle, Integer position) {
        this.vehicle = vehicle;
        this.position = position;
    }

    public String getVehicle() {
        return vehicle;
    }

    public Integer getPosition() {
        return position;
    }
}
