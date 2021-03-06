package org.shadowrun.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.shadowrun.common.NumericLimitListener;
import org.shadowrun.common.nodes.cells.BarrierPresetCell;
import org.shadowrun.common.constants.BarrierType;
import org.shadowrun.logic.AppLogic;
import org.shadowrun.models.Barrier;

import java.util.Arrays;
import java.util.Optional;

public class AddBarrier implements Controller {

    private Stage stage;
    private Barrier barrier;
    private AppLogic appLogic;

    @FXML
    private TextField textField_name;
    @FXML
    private TextField textField_structure;
    @FXML
    private TextField textField_armor;

    @FXML
    private ComboBox<BarrierType> comboBox_preset;

    @FXML
    private Label label_description;

    @FXML
    private Button button_ok;

    @FXML
    private void okOnAction() {
        barrier = new Barrier(textField_name.textProperty().get(),
                comboBox_preset.getSelectionModel().getSelectedItem(),
                Integer.parseInt(textField_structure.textProperty().get()),
                Integer.parseInt(textField_armor.textProperty().get()));
        stage.close();
    }

    @FXML
    private void cancelOnAction() {
        stage.close();
    }


    public void onOpen(Stage stage, AppLogic appLogic) {
        this.stage = stage;
        this.appLogic = appLogic;
        this.barrier = null;

        textField_name.textProperty().setValue(appLogic.getConfig().getLatestBarrierName());
        textField_armor.textProperty().addListener(new NumericLimitListener(textField_armor, 0, null));
        textField_structure.textProperty().addListener(new NumericLimitListener(textField_structure, 0, null));

        comboBox_preset.setItems(FXCollections.observableArrayList(Arrays.asList(BarrierType.values())));
        comboBox_preset.setCellFactory(param -> new BarrierPresetCell());
        comboBox_preset.setButtonCell(new BarrierPresetCell());
        comboBox_preset.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                textField_armor.textProperty().setValue(String.valueOf(newValue.getArmor()));
                textField_structure.textProperty().setValue(String.valueOf(newValue.getStructure()));
                label_description.textProperty().setValue(newValue.getDescription());
            }
        });

        button_ok.disableProperty().bind(
                textField_armor.textProperty().isEmpty().or(
                        textField_name.textProperty().isEmpty().or(
                                textField_structure.textProperty().isEmpty())));
    }

    public Optional<Barrier> getBarrier() {
        return Optional.ofNullable(barrier);
    }

    @Override
    public Stage getStage() {
        return stage;
    }
}
