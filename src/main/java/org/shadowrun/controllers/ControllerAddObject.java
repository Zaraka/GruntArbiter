package org.shadowrun.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.shadowrun.common.NumericLimitListener;
import org.shadowrun.models.Object;

import java.util.Optional;

public class ControllerAddObject {

    private Stage stage;
    private Object object;

    @FXML
    private TextField textField_name;
    @FXML
    private TextField textField_structure;
    @FXML
    private TextField textField_armor;

    @FXML
    public void okOnAction() {
        object = new Object(textField_name.textProperty().get(),
                Integer.parseInt(textField_structure.textProperty().get()),
                Integer.parseInt(textField_armor.textProperty().get()));
        stage.close();
    }

    @FXML
    public void cancelOnAction() {
        stage.close();
    }


    public void onOpen(Stage stage) {
        this.stage = stage;
        this.object = null;

        textField_armor.textProperty().addListener(new NumericLimitListener(textField_armor, 0, null));
        textField_structure.textProperty().addListener(new NumericLimitListener(textField_structure, 0, null));
    }

    public Optional<Object> getObject() {
        return Optional.of(object);
    }
}
