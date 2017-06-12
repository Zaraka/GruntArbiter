package org.shadowrun.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import org.shadowrun.models.Character;
import org.shadowrun.models.World;

import java.util.Optional;

public class ControllerAddCharacter {

    private Optional<Character> character;
    private Stage stage;

    @FXML
    private ToggleGroup world;

    @FXML
    private TextField textField_name;
    @FXML
    private TextField textField_initiative;

    @FXML
    private RadioButton radioButton_realWorld;
    @FXML
    private RadioButton radioButton_matrix;
    @FXML
    private RadioButton radioButton_astralPlane;

    @FXML
    public void okOnAction() {
        character = Optional.of(new Character(textField_name.textProperty().get(), Integer.valueOf(textField_initiative.textProperty().get()),
                (World)world.getSelectedToggle().getUserData(), false));
        stage.close();
    }

    @FXML
    public void cancelOnAction() {
        stage.close();
    }


    public void onOpen(Stage stage) {
        this.stage = stage;
        this.character = Optional.empty();
        textField_initiative.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField_initiative.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        radioButton_realWorld.setUserData(World.REAL);
        radioButton_matrix.setUserData(World.MATRIX);
        radioButton_astralPlane.setUserData(World.ASTRAL);
    }

    public Optional<Character> getCharacter() {
        return character;
    }
}
