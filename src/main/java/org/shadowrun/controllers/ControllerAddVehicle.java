package org.shadowrun.controllers;

import com.google.gson.Gson;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import org.hildan.fxgson.FxGson;
import org.shadowrun.common.NumericLimitListener;
import org.shadowrun.common.nodes.cells.VehicleTreeCell;
import org.shadowrun.models.Vehicle;
import org.shadowrun.models.VehiclePresset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.util.Optional;

public class ControllerAddVehicle {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerAddVehicle.class);

    private static final Gson gson = FxGson.createWithExtras();

    private Stage stage;
    private Vehicle vehicle;

    @FXML
    private TreeView<Vehicle> treeView_pressets;

    @FXML
    private TextField textField_name;
    @FXML
    private TextField textField_handling_onRoad;
    @FXML
    private TextField textField_handling_offRoad;
    @FXML
    private TextField textField_speed_onRoad;
    @FXML
    private TextField textField_speed_offRoad;
    @FXML
    private TextField textField_acceleration_onRoad;
    @FXML
    private TextField textField_acceleration_offRoad;
    @FXML
    private TextField textField_body;
    @FXML
    private TextField textField_armor;
    @FXML
    private TextField textField_pilot;
    @FXML
    private TextField textField_sensor;

    @FXML
    private void okOnAction() {
        vehicle = new Vehicle(
                textField_name.getText(),
                Integer.parseInt(textField_handling_onRoad.getText()),
                Integer.parseInt(textField_handling_offRoad.getText()),
                Integer.parseInt(textField_speed_onRoad.getText()),
                Integer.parseInt(textField_speed_offRoad.getText()),
                Integer.parseInt(textField_acceleration_onRoad.getText()),
                Integer.parseInt(textField_acceleration_offRoad.getText()),
                Integer.parseInt(textField_body.getText()),
                Integer.parseInt(textField_armor.getText()),
                Integer.parseInt(textField_pilot.getText()),
                Integer.parseInt(textField_sensor.getText())
        );
        stage.close();
    }

    @FXML
    private void cancelOnAction() {
        stage.close();
    }

    public void onOpen(Stage stage) {
        this.stage = stage;
        this.vehicle = null;

        textField_handling_onRoad.textProperty()
                .addListener(new NumericLimitListener(textField_handling_onRoad, 0, null));
        textField_handling_offRoad.textProperty()
                .addListener(new NumericLimitListener(textField_handling_offRoad, 0, null));
        textField_speed_onRoad.textProperty()
                .addListener(new NumericLimitListener(textField_speed_onRoad, 0, null));
        textField_speed_offRoad.textProperty()
                .addListener(new NumericLimitListener(textField_speed_offRoad, 0, null));
        textField_acceleration_onRoad.textProperty()
                .addListener(new NumericLimitListener(textField_acceleration_onRoad, 0, null));
        textField_acceleration_offRoad.textProperty()
                .addListener(new NumericLimitListener(textField_acceleration_offRoad, 0, null));
        textField_body.textProperty()
                .addListener(new NumericLimitListener(textField_body, 0, null));
        textField_armor.textProperty()
                .addListener(new NumericLimitListener(textField_armor, 0, null));
        textField_pilot.textProperty()
                .addListener(new NumericLimitListener(textField_pilot, 0, null));
        textField_sensor.textProperty()
                .addListener(new NumericLimitListener(textField_sensor, 0, null));


        VehiclePresset vehiclePresset = gson.fromJson(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream("data/vehicles.json")),
                VehiclePresset.class);

        TreeItem<Vehicle> rootNode = new TreeItem<>(vehiclePresset.getVehicle());

        for(VehiclePresset rootVehiclePressets : vehiclePresset.getChildren()) {
            loadPreset(rootVehiclePressets, rootNode.getChildren());
        }

        rootNode.setExpanded(true);
        treeView_pressets.setRoot(rootNode);
        treeView_pressets.setShowRoot(false);

        treeView_pressets.setCellFactory(param -> new VehicleTreeCell());

        treeView_pressets.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                textField_handling_onRoad.textProperty()
                        .setValue(String.valueOf(newValue.getValue().getHandling().getOnRoad()));
                textField_handling_offRoad.textProperty()
                        .setValue(String.valueOf(newValue.getValue().getHandling().getOffRoad()));
                textField_speed_onRoad.textProperty()
                        .setValue(String.valueOf(newValue.getValue().getSpeed().getOnRoad()));
                textField_speed_offRoad.textProperty()
                        .setValue(String.valueOf(newValue.getValue().getSpeed().getOffRoad()));
                textField_acceleration_onRoad.textProperty()
                        .setValue(String.valueOf(newValue.getValue().getAcceleration().getOnRoad()));
                textField_acceleration_offRoad.textProperty()
                        .setValue(String.valueOf(newValue.getValue().getAcceleration().getOffRoad()));
                textField_body.textProperty()
                        .setValue(String.valueOf(newValue.getValue().getBody()));
                textField_armor.textProperty()
                        .setValue(String.valueOf(newValue.getValue().getArmor()));
                textField_pilot.textProperty()
                        .setValue(String.valueOf(newValue.getValue().getPilot()));
                textField_sensor.textProperty()
                        .setValue(String.valueOf(newValue.getValue().getSensor()));
                textField_name.textProperty()
                        .setValue(newValue.getValue().getName());
            }
        });

    }

    public Optional<Vehicle> getVehicle() {
        return Optional.ofNullable(vehicle);
    }

    private void loadPreset(VehiclePresset vehiclePresset, ObservableList<TreeItem<Vehicle>> node) {
        TreeItem<Vehicle> newNode = new TreeItem<>(vehiclePresset.getVehicle());
        node.add(newNode);

        for(int i = 0; i < vehiclePresset.getChildren().size(); i++) {
            loadPreset(vehiclePresset.getChildren().get(i), newNode.getChildren());
        }
    }
}
