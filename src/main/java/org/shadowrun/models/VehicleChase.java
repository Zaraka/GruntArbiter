package org.shadowrun.models;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.shadowrun.common.constants.TerrainType;
import org.shadowrun.common.constants.VehicleChaseRole;

import java.util.Map;

public class VehicleChase implements Observable {

    private IntegerProperty terrainModifier;

    private ObjectProperty<TerrainType> terrainType;

    private ObservableMap<String, VehicleChaseRole> chaseRoles;

    private ObservableMap<String, Integer> positions;


    public VehicleChase(TerrainType terrainType,
                        int terrainModifier,
                        Map<String, Integer> positions,
                        Map<String, VehicleChaseRole> roles) {

        this.terrainModifier = new SimpleIntegerProperty(terrainModifier);
        this.terrainType = new SimpleObjectProperty<>(terrainType);
        this.chaseRoles = FXCollections.observableMap(roles);
        this.positions = FXCollections.observableMap(positions);

    }

    public int getTerrainModifier() {
        return terrainModifier.get();
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
