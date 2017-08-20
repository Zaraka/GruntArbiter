package org.shadowrun.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.shadowrun.common.NumericLimitListener;
import org.shadowrun.models.Character;
import org.shadowrun.common.constants.World;

import java.util.Optional;

public class ControllerAddCharacter {

    private Character character;
    private Stage stage;

    @FXML
    private ToggleGroup world;

    @FXML
    private TextField textField_name;
    @FXML
    private TextField textField_initiative;
    @FXML
    private TextField textField_conditionMonitor;

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
    private void okOnAction() {
        character = new Character(
                textField_name.textProperty().get(),
                getInitative(),
                (World) world.getSelectedToggle().getUserData(),
                checkbox_npc.isSelected(),
                false,
                getConditionMonitor(),
                null);
        stage.close();
    }

    @FXML
    private void cancelOnAction() {
        stage.close();
    }


    public void onOpen(Stage stage) {
        this.stage = stage;
        this.character = null;
        textField_initiative.textProperty().addListener(new NumericLimitListener(textField_initiative, 0, null));
        textField_conditionMonitor.textProperty().addListener(new NumericLimitListener(textField_conditionMonitor, 0, null));
        radioButton_realWorld.setUserData(World.REAL);
        radioButton_matrix.setUserData(World.MATRIX);
        radioButton_astralPlane.setUserData(World.ASTRAL);

        button_ok.disableProperty().bind(textField_name.textProperty().isEmpty());
    }

    public Optional<Character> getCharacter() {
        return Optional.ofNullable(character);
    }

    private int getInitative() {
        return (textField_initiative.textProperty().isEmpty().get()) ? 0 :
                Integer.parseInt(textField_initiative.textProperty().get());
    }

    private int getConditionMonitor() {
        return (textField_conditionMonitor.textProperty().isEmpty().get()) ? 0 :
                Integer.parseInt(textField_conditionMonitor.textProperty().get());
    }
}
