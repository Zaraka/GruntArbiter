package org.shadowrun.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.shadowrun.common.NumericLimitListener;
import org.shadowrun.models.Device;

import java.util.Optional;

public class ControllerAddDevice {

    private Device device;
    private Stage stage;

    @FXML
    private Button button_ok;
    @FXML
    private Button button_cancel;

    @FXML
    private TextField textField_name;
    @FXML
    private TextField textField_rating;
    @FXML
    private TextField textField_attack;
    @FXML
    private TextField textField_sleeze;
    @FXML
    private TextField textField_firewall;
    @FXML
    private TextField textField_dataProcessing;

    @FXML
    private void okOnAction() {
        device = new Device(textField_name.textProperty().get(),
                getRating(), getAttack(), getSleeze(), getFirewall(), getDataProcessing());

        stage.close();
    }

    @FXML
    private void cancelOnAction() {
        stage.close();
    }

    public void onOpen(Stage stage) {
        this.stage = stage;
        this.device = null;

        textField_rating.textProperty().addListener(new NumericLimitListener(textField_rating, 0, null));
        textField_attack.textProperty().addListener(new NumericLimitListener(textField_attack, 0, null));
        textField_sleeze.textProperty().addListener(new NumericLimitListener(textField_sleeze, 0, null));
        textField_firewall.textProperty().addListener(new NumericLimitListener(textField_firewall, 0, null));
        textField_dataProcessing.textProperty().addListener(new NumericLimitListener(textField_dataProcessing, 0, null));
    }

    public Optional<Device> getDevice() {
        return Optional.ofNullable(device);
    }

    private int getRating() {
        return (textField_rating.textProperty().isEmpty().get()) ? 0 :
                Integer.parseInt(textField_rating.textProperty().get());
    }

    private int getAttack() {
        return (textField_attack.textProperty().isEmpty().get()) ? 0 :
                Integer.parseInt(textField_attack.textProperty().get());
    }

    private int getSleeze() {
        return (textField_sleeze.textProperty().isEmpty().get()) ? 0 :
                Integer.parseInt(textField_sleeze.textProperty().get());
    }

    private int getFirewall() {
        return (textField_firewall.textProperty().isEmpty().get()) ? 0 :
                Integer.parseInt(textField_firewall.textProperty().get());
    }

    private int getDataProcessing() {
        return (textField_dataProcessing.textProperty().isEmpty().get()) ? 0 :
                Integer.parseInt(textField_dataProcessing.textProperty().get());
    }
}
