package org.shadowrun.controllers;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import org.shadowrun.common.constants.*;
import org.shadowrun.common.converters.VehicleConverter;
import org.shadowrun.common.nodes.cells.TerrainModifierCell;
import org.shadowrun.common.nodes.cells.TerrainTypeCell;
import org.shadowrun.models.Battle;
import org.shadowrun.models.Character;
import org.shadowrun.models.Vehicle;
import org.shadowrun.models.VehicleChase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class VehicleCombat implements Controller {

    private Stage stage;
    private Battle battle;
    private VehicleChase vehicleChase = null;
    private Map<Character, String> characterVehicleMap = new HashMap<>();

    public class VehicleValues {
        private StringProperty name;
        private ObjectProperty<VehicleChaseRole> role;
        private IntegerProperty position;
        private StringProperty uuid;

        public VehicleValues(Vehicle vehicle) {
            this.uuid = new SimpleStringProperty(vehicle.getUuid());
            this.name = new SimpleStringProperty(vehicle.getName());
            this.role = new SimpleObjectProperty<>(VehicleChaseRole.PURSUER);
            this.position = new SimpleIntegerProperty(0);
        }

        public String getName() {
            return name.get();
        }

        public StringProperty nameProperty() {
            return name;
        }

        public VehicleChaseRole getRole() {
            return role.get();
        }

        public ObjectProperty<VehicleChaseRole> roleProperty() {
            return role;
        }

        public int getPosition() {
            return position.get();
        }

        public IntegerProperty positionProperty() {
            return position;
        }

        public String getUuid() {
            return uuid.get();
        }

        public StringProperty uuidProperty() {
            return uuid;
        }
    }


    @FXML
    private ComboBox<TerrainType> comboBox_terrainType;
    @FXML
    private ComboBox<TerrainModifier> comboBox_terrainModifier;

    @FXML
    private TableView<Character> tableView_passengers;
    @FXML
    private TableColumn<Character, String> tableColumn_passengers_name;
    @FXML
    private TableColumn<Character, String> tableColumn_passengers_vehicle;

    @FXML
    private TableView<VehicleValues> tableView_vehicles;
    @FXML
    private TableColumn<VehicleValues, String> tableColumn_vehicles_name;
    @FXML
    private TableColumn<VehicleValues, VehicleChaseRole> tableColumn_vehicles_role;
    @FXML
    private TableColumn<VehicleValues, Integer> tableColumn_vehicles_position;


    @FXML
    public void cancelOnAction() {
        stage.close();
    }

    @FXML
    public void okOnAction() {

        Map<String, Integer> positions = tableView_vehicles.getItems().stream().collect(Collectors
                .toMap(VehicleValues::getUuid, VehicleValues::getPosition));
        Map<String, VehicleChaseRole> roles = tableView_vehicles.getItems().stream().collect(Collectors
                .toMap(VehicleValues::getUuid, VehicleValues::getRole));

        vehicleChase = new VehicleChase(
                comboBox_terrainType.getSelectionModel().getSelectedItem(),
                comboBox_terrainModifier.getSelectionModel().getSelectedItem().getModifier(),
                positions,
                roles);

        stage.close();
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    public void onOpen(Stage stage, Battle battle) {
        this.stage = stage;
        this.battle = battle;

        comboBox_terrainType.setItems(FXCollections.observableArrayList(TerrainType.values()));
        comboBox_terrainType.setCellFactory(param -> new TerrainTypeCell());
        comboBox_terrainType.setButtonCell(new TerrainTypeCell());
        comboBox_terrainType.getSelectionModel().select(0);

        comboBox_terrainModifier.setItems(FXCollections.observableArrayList(TerrainModifier.values()));
        comboBox_terrainModifier.setCellFactory(param -> new TerrainModifierCell());
        comboBox_terrainModifier.setButtonCell(new TerrainModifierCell());
        comboBox_terrainModifier.getSelectionModel().select(0);

        tableView_passengers.setItems(battle.getCharacters());
        tableColumn_passengers_name.setCellValueFactory(param -> param.getValue().nameProperty());
        tableColumn_passengers_vehicle.setCellValueFactory(param -> param.getValue().vehicleProperty());
        List<String> vehicles = battle.getVehicles().stream()
                .filter(vehicle -> vehicle.getType() == VehicleType.VEHICLE)
                .map(Vehicle::getUuid).collect(Collectors.toList());
        Map<String, String> vehiclesUUIDS = battle.getVehicles().stream()
                .filter(vehicle -> vehicle.getType() == VehicleType.VEHICLE)
                .collect(Collectors.toMap(Vehicle::getUuid, Vehicle::getName));
        tableColumn_passengers_vehicle.setCellFactory(ComboBoxTableCell
                .forTableColumn(new VehicleConverter(vehiclesUUIDS), FXCollections.observableArrayList(vehicles)));

        tableColumn_passengers_vehicle.setOnEditCommit(event -> characterVehicleMap.put(event.getRowValue(),
                event.getNewValue()));

        tableView_vehicles.setItems(FXCollections.observableArrayList(battle.getVehicles().stream()
                .map(VehicleValues::new).collect(Collectors.toList())));

        tableColumn_vehicles_name.setCellValueFactory(param -> param.getValue().nameProperty());
        tableColumn_vehicles_role.setCellValueFactory(param -> param.getValue().roleProperty());
        tableColumn_vehicles_role.setCellFactory(ComboBoxTableCell.forTableColumn(
                FXCollections.observableArrayList(VehicleChaseRole.values())
        ));
        tableColumn_vehicles_position.setCellValueFactory(param -> param.getValue().positionProperty().asObject());
        tableColumn_vehicles_position.setCellFactory(TextFieldTableCell.forTableColumn(new StrictIntegerStringConverter()));
    }

    public Optional<VehicleChase> getValues() {
        return Optional.ofNullable(vehicleChase);
    }
}
