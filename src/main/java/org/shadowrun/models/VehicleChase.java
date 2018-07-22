package org.shadowrun.models;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import org.shadowrun.common.constants.TerrainType;
import org.shadowrun.common.constants.VehicleChaseRole;
import org.shadowrun.common.events.VehiclePositionChange;

import java.util.Map;

public class VehicleChase implements Observable {

    private IntegerProperty terrainModifier;

    private ObjectProperty<TerrainType> terrainType;


    private ObservableMap<String, VehicleChaseRole> chaseRoles;

    private ObservableMap<String, Integer> positions;

    private EventHandler<VehiclePositionChange> positionChangeEventHandler;

    public VehicleChase(TerrainType terrainType,
                        int terrainModifier,
                        Map<String, Integer> positions,
                        Map<String, VehicleChaseRole> roles) {

        this.terrainModifier = new SimpleIntegerProperty(terrainModifier);
        this.terrainType = new SimpleObjectProperty<>(terrainType);
        this.chaseRoles = FXCollections.observableMap(roles);
        this.positions = FXCollections.observableMap(positions);

        this.positions.addListener((MapChangeListener<String, Integer>) change -> {
            if(positionChangeEventHandler != null) {
                positionChangeEventHandler.handle(new VehiclePositionChange(null, null));
            }
        });
    }

    public void setPositionChangeEventHandler(EventHandler<VehiclePositionChange> positionChangeEventHandler) {
        this.positionChangeEventHandler = positionChangeEventHandler;
    }

    public int getTerrainModifier() {
        return terrainModifier.get();
    }

    public void modifyPosition(String vehicle, Integer position) {
        positions.put(vehicle, position);
        positionChangeEventHandler.handle(new VehiclePositionChange(vehicle, position));
    }

    public IntegerProperty terrainModifierProperty() {
        return terrainModifier;
    }

    public TerrainType getTerrainType() {
        return terrainType.get();
    }

    public ObjectProperty<TerrainType> terrainTypeProperty() {
        return terrainType;
    }

    public ObservableMap<String, VehicleChaseRole> getChaseRoles() {
        return chaseRoles;
    }

    public ObservableMap<String, Integer> getPositions() {
        return positions;
    }

    @Override
    public void addListener(InvalidationListener listener) {
        terrainType.addListener(listener);
        terrainModifier.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        terrainType.addListener(listener);
        terrainModifier.addListener(listener);
    }
}
