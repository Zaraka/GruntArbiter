package org.shadowrun.models;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class VehiclePreset {
    private ObjectProperty<Vehicle> vehicle;

    private ObservableList<VehiclePreset> children;

    private BooleanProperty category;

    private ObjectProperty<FontAwesomeIcon> icon;

    public VehiclePreset(Vehicle vehicle) {
        this.vehicle = new SimpleObjectProperty<>(vehicle);
        category = new SimpleBooleanProperty(false);
        children = FXCollections.observableArrayList();
        icon = new SimpleObjectProperty<>(null);
    }

    public Vehicle getVehicle() {
        return vehicle.get();
    }

    public ObjectProperty<Vehicle> vehicleProperty() {
        return vehicle;
    }

    public ObservableList<VehiclePreset> getChildren() {
        return children;
    }

    public boolean isCategory() {
        return category.get();
    }

    public BooleanProperty categoryProperty() {
        return category;
    }

    public FontAwesomeIcon getIcon() {
        return icon.get();
    }

    public ObjectProperty<FontAwesomeIcon> iconProperty() {
        return icon;
    }
}
