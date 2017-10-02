package org.shadowrun.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.shadowrun.common.NumericLimitListener;
import org.shadowrun.common.constants.CharacterType;
import org.shadowrun.common.constants.CompanionType;
import org.shadowrun.common.constants.World;
import org.shadowrun.common.factories.CharacterDialogFactory;
import org.shadowrun.common.factories.DeviceDialogFactory;
import org.shadowrun.common.factories.VehicleDialogFactory;
import org.shadowrun.common.nodes.cells.CharacterPresetCell;
import org.shadowrun.common.nodes.cells.CompanionTypeCell;
import org.shadowrun.common.nodes.rows.CompanionTableRow;
import org.shadowrun.models.*;
import org.shadowrun.models.Character;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class ControllerAddCharacter implements Controller {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerAddCharacter.class);

    private static final CharacterDialogFactory characterDialogFactory = new CharacterDialogFactory();
    private static final VehicleDialogFactory vehicleDialogFactory = new VehicleDialogFactory();
    private static final DeviceDialogFactory deviceDialogFactory = new DeviceDialogFactory();

    private Character character;
    private Stage stage;
    private Campaign campaign;
    private CharacterType characterType;

    @FXML
    private ComboBox<Character> comboBox_preset;
    @FXML
    private ComboBox<CompanionType> comboBox_companions;

    @FXML
    private ToggleGroup world;

    @FXML
    private TextField textField_name;
    @FXML
    private TextField textField_initiative;
    @FXML
    private TextField textField_physicalConditionMonitor;
    @FXML
    private TextField textField_stunConditionMonitor;

    @FXML
    private RadioButton radioButton_realWorld;
    @FXML
    private RadioButton radioButton_matrix;
    @FXML
    private RadioButton radioButton_astralPlane;

    @FXML
    private CheckBox checkbox_npc;

    @FXML
    private Button button_ok;
    @FXML
    private Button button_deletePreset;
    @FXML
    private Button button_addToCompanions;

    @FXML
    private TableView<Companion> tableView_companions;
    @FXML
    private TableColumn<Companion, String> tableColumn_companions_name;

    @FXML
    private VBox vbox_companions;

    @FXML
    private void okOnAction() {
        character = createCharacter();
        stage.close();
    }

    @FXML
    private void cancelOnAction() {
        stage.close();
    }

    @FXML
    private void savePresetOnAction() {
        campaign.getCharacterPresets()
                .removeIf(character1 -> Objects.equals(character1.getName(), textField_name.getText()));
        Character character = createCharacter();
        campaign.getCharacterPresets().add(character);
        comboBox_preset.getSelectionModel().select(character);
    }

    @FXML
    private void deletePresetOnAction() {
        campaign.getCharacterPresets().remove(comboBox_preset.getSelectionModel().getSelectedItem());
        textField_name.setText(StringUtils.EMPTY);
        textField_stunConditionMonitor.setText("10");
        textField_physicalConditionMonitor.setText("10");
        textField_initiative.setText(StringUtils.EMPTY);
    }

    @FXML
    private void addToCompanionsOnAction() {
        switch (comboBox_companions.getSelectionModel().getSelectedItem()) {
            case CHARACTER:
                try {
                    ControllerAddCharacter controllerAddCharacter =
                            characterDialogFactory.createDialog(
                                    campaign,
                                    CharacterType.COMPANION,
                                    null);
                    controllerAddCharacter.getStage().showAndWait();
                    controllerAddCharacter.getCharacter().ifPresent(companionCharacter -> {
                        tableView_companions.getItems().add(new Companion(companionCharacter));
                    });

                } catch (IOException ex) {
                    LOG.error("Could not load addCharacter dialog: ", ex);
                }
                break;
            case VEHICLE:
                try {
                    ControllerAddVehicle controllerAddVehicle =
                            vehicleDialogFactory.createDialog(null);
                    controllerAddVehicle.getStage().showAndWait();
                    controllerAddVehicle.getVehicle().ifPresent(vehicle -> {
                        tableView_companions.getItems().add(new Companion(vehicle));
                    });
                } catch (IOException ex) {
                    LOG.error("Could not load addVehicle dialog: ", ex);
                }
                break;
            case DEVICE:
                try {
                    ControllerAddDevice controllerAddDevice =
                            deviceDialogFactory.createDialog(campaign, null);
                    controllerAddDevice.getStage().showAndWait();
                    controllerAddDevice.getDevice().ifPresent(device -> {
                        tableView_companions.getItems().add(new Companion(device));
                    });

                } catch (IOException ex) {
                    LOG.error("Could not load addDevice dialog: ", ex);
                }
                break;
        }
    }

    public void onOpen(Stage stage, Campaign campaign, CharacterType characterType, Character edit) {
        this.stage = stage;
        this.character = null;
        this.campaign = campaign;
        this.characterType = characterType;

        textField_initiative.textProperty()
                .addListener(new NumericLimitListener(textField_initiative, 0, null));
        textField_physicalConditionMonitor.textProperty()
                .addListener(new NumericLimitListener(textField_physicalConditionMonitor, 0, null));
        textField_stunConditionMonitor.textProperty()
                .addListener(new NumericLimitListener(textField_stunConditionMonitor, 0, null));
        radioButton_realWorld.setUserData(World.REAL);
        radioButton_matrix.setUserData(World.MATRIX);
        radioButton_astralPlane.setUserData(World.ASTRAL);

        comboBox_preset.setItems(campaign.getCharacterPresets());
        comboBox_preset.setCellFactory(param -> new CharacterPresetCell());
        comboBox_preset.setButtonCell(new CharacterPresetCell());
        comboBox_preset.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        textField_name.textProperty().setValue(newValue.getName());
                        textField_physicalConditionMonitor.textProperty()
                                .setValue(String.valueOf(newValue.getPhysicalMonitor().getMax()));
                        textField_stunConditionMonitor.textProperty()
                                .setValue(String.valueOf(newValue.getStunMonitor().getMax()));
                        switch (newValue.getWorld()) {
                            case REAL:
                                radioButton_realWorld.setSelected(true);
                                break;
                            case ASTRAL:
                                radioButton_astralPlane.setSelected(true);
                                break;
                            case MATRIX:
                                radioButton_matrix.setSelected(true);
                                break;
                        }
                        if(!characterType.equals(CharacterType.COMPANION)) {
                            tableView_companions.setItems(newValue.getCompanions());
                        }
                    }
                }
        );

        vbox_companions.managedProperty().bind(vbox_companions.visibleProperty());

        if (!characterType.equals(CharacterType.COMPANION)) {
            tableColumn_companions_name.setCellValueFactory(param -> param.getValue().nameProperty());

            comboBox_companions.setItems(FXCollections.observableArrayList(CompanionType.values()));
            comboBox_companions.setButtonCell(new CompanionTypeCell());
            comboBox_companions.setCellFactory(param -> new CompanionTypeCell());

            button_addToCompanions.disableProperty().bind(comboBox_companions.valueProperty().isNull());

            tableView_companions.setRowFactory(param -> {
                TableRow<Companion> tableRow = new CompanionTableRow();

                MenuItem deleteCompanion = new MenuItem("Delete companion");
                deleteCompanion.setOnAction(event -> {
                    tableView_companions.getItems()
                            .remove(tableView_companions.getSelectionModel().getSelectedItem());
                });

                MenuItem editCompanion = new MenuItem("Edit companion");
                editCompanion.setOnAction(event -> {
                    Companion companion = tableView_companions.getSelectionModel().getSelectedItem();
                    try {
                        switch (companion.getCompanionType()) {
                            case CHARACTER:
                                ControllerAddCharacter controllerAddCharacter =
                                        characterDialogFactory.createDialog(campaign,
                                                CharacterType.COMPANION,
                                                ((Character) companion.getCompanion()));
                                controllerAddCharacter.getStage().showAndWait();
                                controllerAddCharacter.getCharacter().ifPresent(character -> {
                                    ((Character) companion.getCompanion()).setFrom(character);
                                });
                                break;
                            case DEVICE:
                                ControllerAddDevice controllerAddDevice =
                                        deviceDialogFactory.createDialog(campaign,
                                                ((Device) companion.getCompanion()));
                                controllerAddDevice.getStage().showAndWait();
                                controllerAddDevice.getDevice().ifPresent(device -> {
                                    ((Device) companion.getCompanion()).setFrom(device);
                                });
                                break;
                            case VEHICLE:
                                ControllerAddVehicle controllerAddVehicle =
                                        vehicleDialogFactory.createDialog(((Vehicle) companion.getCompanion()));
                                controllerAddVehicle.getStage().showAndWait();
                                controllerAddVehicle.getVehicle().ifPresent(vehicle -> {
                                    ((Vehicle) companion.getCompanion()).setFrom(vehicle);
                                });
                        }
                    } catch (IOException ex) {
                        LOG.error(ex.toString());
                    }
                });

                tableRow.emptyProperty().addListener((observable, oldValue, newValue) -> {
                    tableRow.setContextMenu((newValue) ? null :
                            new ContextMenu(editCompanion, deleteCompanion));
                });
                return tableRow;
            });
        } else {
            vbox_companions.setVisible(false);
        }


        if (edit != null) {
            textField_initiative.setText(String.valueOf(edit.getInitiative()));
            textField_physicalConditionMonitor.setText(String.valueOf(edit.getPhysicalMonitor().getMax()));
            textField_stunConditionMonitor.setText(String.valueOf(edit.getStunMonitor().getMax()));
            textField_name.setText(edit.getName());
            if (!characterType.equals(CharacterType.COMPANION)) {
                tableView_companions.setItems(FXCollections.observableArrayList(edit.getCompanions()));
            }
        }

        button_deletePreset.disableProperty().bind(comboBox_preset.getSelectionModel().selectedItemProperty().isNull());

        button_ok.disableProperty().bind(textField_name.textProperty().isEmpty());
    }

    public Optional<Character> getCharacter() {
        return Optional.ofNullable(character);
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    private int getInitative() {
        return (textField_initiative.textProperty().isEmpty().get()) ? 0 :
                Integer.parseInt(textField_initiative.textProperty().get());
    }

    private int getPhysicalConditionMonitor() {
        return (textField_physicalConditionMonitor.textProperty().isEmpty().get()) ? 0 :
                Integer.parseInt(textField_physicalConditionMonitor.textProperty().get());
    }

    private int getStunConditionMonitor() {
        return (textField_stunConditionMonitor.textProperty().isEmpty().get()) ? 0 :
                Integer.parseInt(textField_stunConditionMonitor.textProperty().get());
    }

    private Character createCharacter() {
        return new Character(
                textField_name.textProperty().get(),
                getInitative(),
                (World) world.getSelectedToggle().getUserData(),
                checkbox_npc.isSelected(),
                false,
                characterType,
                getPhysicalConditionMonitor(),
                getStunConditionMonitor(),
                null, tableView_companions.getItems());
    }
}
