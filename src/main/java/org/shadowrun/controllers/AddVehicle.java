package org.shadowrun.controllers;

import com.google.gson.Gson;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.hildan.fxgson.FxGson;
import org.shadowrun.common.NumericLimitListener;
import org.shadowrun.common.constants.VehicleType;
import org.shadowrun.common.nodes.cells.VehicleTreeCell;
import org.shadowrun.models.Campaign;
import org.shadowrun.models.Vehicle;
import org.shadowrun.models.VehiclePreset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.stream.Collectors;

public class AddVehicle implements Controller {

    private static final Logger LOG = LoggerFactory.getLogger(AddVehicle.class);

    private static final Gson gson = FxGson.createWithExtras();

    private Stage stage;
    private Vehicle vehicle;
    private Campaign campaign;
    private TreeItem<VehiclePreset> customPresets;

    @FXML
    private TreeView<VehiclePreset> treeView_presets;

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
    private TextField textField_image;

    @FXML
    private RadioButton radioButton_type_drone;
    @FXML
    private RadioButton radioButton_type_vehicle;

    @FXML
    private Button button_savePreset;
    @FXML
    private Button button_deletePreset;
    @FXML
    private Button okButton;

    @FXML
    private ToggleGroup type;

    @FXML
    private void okOnAction() {
        vehicle = createVehicle();
        stage.close();
    }

    @FXML
    private void cancelOnAction() {
        stage.close();
    }

    @FXML
    private void savePresetOnAction() {
        campaign.getVehicles().removeIf(vehicle1 -> vehicle1.getName().equals(textField_name.getText()));
        campaign.getVehicles().add(createVehicle());
        setupPresets();
    }

    @FXML
    private void deletePresetOnAction() {
        campaign.getVehicles().remove(treeView_presets.getSelectionModel().getSelectedItem().getValue().getVehicle());
        setupPresets();
    }

    public void onOpen(Stage stage, Campaign campaign, Vehicle edit) {
        this.stage = stage;
        this.vehicle = null;
        this.campaign = campaign;

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

        radioButton_type_drone.setUserData(VehicleType.DRONE);
        radioButton_type_vehicle.setUserData(VehicleType.VEHICLE);

        setupPresets();

        treeView_presets.setCellFactory(param -> new VehicleTreeCell());

        treeView_presets.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.getValue().isCategory()) {
                textField_handling_onRoad.textProperty()
                        .setValue(String.valueOf(newValue.getValue().getVehicle().getHandling().getOnRoad()));
                textField_handling_offRoad.textProperty()
                        .setValue(String.valueOf(newValue.getValue().getVehicle().getHandling().getOffRoad()));
                textField_speed_onRoad.textProperty()
                        .setValue(String.valueOf(newValue.getValue().getVehicle().getSpeed().getOnRoad()));
                textField_speed_offRoad.textProperty()
                        .setValue(String.valueOf(newValue.getValue().getVehicle().getSpeed().getOffRoad()));
                textField_acceleration_onRoad.textProperty()
                        .setValue(String.valueOf(newValue.getValue().getVehicle().getAcceleration().getOnRoad()));
                textField_acceleration_offRoad.textProperty()
                        .setValue(String.valueOf(newValue.getValue().getVehicle().getAcceleration().getOffRoad()));
                textField_body.textProperty()
                        .setValue(String.valueOf(newValue.getValue().getVehicle().getBody()));
                textField_armor.textProperty()
                        .setValue(String.valueOf(newValue.getValue().getVehicle().getArmor()));
                textField_pilot.textProperty()
                        .setValue(String.valueOf(newValue.getValue().getVehicle().getPilot()));
                textField_sensor.textProperty()
                        .setValue(String.valueOf(newValue.getValue().getVehicle().getSensor()));
                textField_name.textProperty()
                        .setValue(newValue.getValue().getVehicle().getName());
                textField_image.textProperty()
                        .setValue(newValue.getValue().getVehicle().getImage());
                selectType(newValue.getValue().getVehicle().getType());
            }
        });

        if (edit != null) {
            textField_name.setText(edit.getName());
            textField_sensor.setText(String.valueOf(edit.getSensor()));
            textField_pilot.setText(String.valueOf(edit.getPilot()));
            textField_armor.setText(String.valueOf(edit.getArmor()));
            textField_body.setText(String.valueOf(edit.getBody()));
            textField_acceleration_offRoad.setText(String.valueOf(edit.getAcceleration().getOffRoad()));
            textField_acceleration_onRoad.setText(String.valueOf(edit.getAcceleration().getOnRoad()));
            textField_speed_offRoad.setText(String.valueOf(edit.getSpeed().getOffRoad()));
            textField_speed_onRoad.setText(String.valueOf(edit.getSpeed().getOnRoad()));
            textField_handling_offRoad.setText(String.valueOf(edit.getHandling().getOffRoad()));
            textField_handling_onRoad.setText(String.valueOf(edit.getHandling().getOnRoad()));
            textField_image.setText(edit.getImage());
            selectType(edit.getType());
        }

        button_savePreset.disableProperty().bind(textField_name.textProperty().isEmpty());
        button_deletePreset.disableProperty().setValue(true);
        treeView_presets.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                TreeItem<VehiclePreset> node = newValue;
                boolean custom = true;
                while (node.getParent() != null) {
                    if (node.getParent().equals(customPresets)) {
                        custom = false;
                    }
                    node = node.getParent();
                }
                button_deletePreset.disableProperty().setValue(custom);
            }
        });

        treeView_presets.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2) {
                okOnAction();
            }
        });


        okButton.disableProperty().bind(textField_acceleration_offRoad.textProperty().isEmpty().or(textField_acceleration_onRoad.textProperty().isEmpty()).or(
                textField_handling_offRoad.textProperty().isEmpty().or(textField_acceleration_onRoad.textProperty().isEmpty()).or(
                        textField_speed_offRoad.textProperty().isEmpty().or(textField_speed_onRoad.textProperty().isEmpty()).or(
                                textField_name.textProperty().isEmpty().or(textField_sensor.textProperty().isEmpty()).or(
                                        textField_pilot.textProperty().isEmpty().or(textField_armor.textProperty().isEmpty()).or(
                                            textField_body.textProperty().isEmpty()
                                        )
                                )
                        )
                )
        ));
    }

    private void setupPresets() {
        VehiclePreset vehiclePreset = gson.fromJson(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream("data/vehicles.json")),
                VehiclePreset.class);

        TreeItem<VehiclePreset> rootNode = new TreeItem<>(vehiclePreset);

        for (VehiclePreset rootVehiclePresets : vehiclePreset.getChildren()) {
            loadPreset(rootVehiclePresets, rootNode.getChildren());
        }

        TreeItem<VehiclePreset> customPresets = new TreeItem<>(new VehiclePreset(new Vehicle()));
        customPresets.getValue().categoryProperty().setValue(true);
        customPresets.getValue().getVehicle().setName("Custom presets");
        customPresets.getChildren().addAll(
                campaign.getVehicles()
                        .stream()
                        .map(VehiclePreset::new)
                        .map(TreeItem::new)
                        .collect(Collectors.toList()));
        rootNode.getChildren().add(customPresets);

        rootNode.setExpanded(true);
        treeView_presets.setRoot(rootNode);
        treeView_presets.setShowRoot(false);
        this.customPresets = customPresets;
    }

    private void selectType(VehicleType edit) {
        switch (edit) {
            case DRONE:
                type.selectToggle(radioButton_type_drone);
                break;
            case VEHICLE:
                type.selectToggle(radioButton_type_vehicle);
                break;
        }
    }

    public Optional<Vehicle> getVehicle() {
        return Optional.ofNullable(vehicle);
    }

    private void loadPreset(VehiclePreset vehiclePreset, ObservableList<TreeItem<VehiclePreset>> node) {
        TreeItem<VehiclePreset> newNode = new TreeItem<>(vehiclePreset);
        node.add(newNode);

        for (int i = 0; i < vehiclePreset.getChildren().size(); i++) {
            loadPreset(vehiclePreset.getChildren().get(i), newNode.getChildren());
        }
    }

    private Vehicle createVehicle() {
        return new Vehicle(
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
                Integer.parseInt(textField_sensor.getText()),
                (VehicleType) type.getSelectedToggle().getUserData(),
                textField_image.getText()
        );
    }

    @Override
    public Stage getStage() {
        return stage;
    }
}
