package org.shadowrun.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.shadowrun.common.NumericLimitListener;
import org.shadowrun.common.cells.CharacterPresetCell;
import org.shadowrun.models.Campaign;
import org.shadowrun.models.Character;
import org.shadowrun.common.constants.World;

import java.util.Objects;
import java.util.Optional;

public class ControllerAddCharacter {

    private Character character;
    private Stage stage;
    private Campaign campaign;

    @FXML
    private ComboBox<Character> comboBox_preset;

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

    public void onOpen(Stage stage, Campaign campaign) {
        this.stage = stage;
        this.character = null;
        this.campaign = campaign;

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
                    if(newValue != null) {
                        textField_name.textProperty().setValue(newValue.getName());
                        textField_physicalConditionMonitor.textProperty()
                                .setValue(String.valueOf(newValue.getPhysicalMonitor()));
                        textField_stunConditionMonitor.textProperty()
                                .setValue(String.valueOf(newValue.getStunMonitor()));
                        switch (newValue.getWorld()){
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
                    }
                }
        );

        button_deletePreset.disableProperty().bind(comboBox_preset.getSelectionModel().selectedItemProperty().isNull());

        button_ok.disableProperty().bind(textField_name.textProperty().isEmpty());
    }

    public Optional<Character> getCharacter() {
        return Optional.ofNullable(character);
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
                getPhysicalConditionMonitor(),
                getStunConditionMonitor(),
                null);
    }
}
