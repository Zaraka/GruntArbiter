package org.shadowrun.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.shadowrun.common.NumericLimitListener;
import org.shadowrun.common.nodes.cells.DevicePressetCell;
import org.shadowrun.models.Campaign;
import org.shadowrun.models.Device;

import java.util.Objects;
import java.util.Optional;

public class ControllerAddDevice implements Controller {

    private Device device;
    private Stage stage;
    private Campaign campaign;

    @FXML
    private ComboBox<Device> comboBox_pressets;

    @FXML
    private Button button_ok;
    @FXML
    private Button button_cancel;
    @FXML
    private Button button_savePresset;
    @FXML
    private Button button_deletePresset;

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
        device = createDevice();

        stage.close();
    }

    @FXML
    private void cancelOnAction() {
        stage.close();
    }

    @FXML
    private void savePressetOnAction() {
        campaign.getDevicePressets()
                .removeIf(device1 -> Objects.equals(device1.getName(), textField_name.getText()));
        Device device = createDevice();
        campaign.getDevicePressets().add(device);
        comboBox_pressets.getSelectionModel().select(device);
    }

    @FXML
    private void deletePressetOnAction() {
        campaign.getDevicePressets().remove(comboBox_pressets.getSelectionModel().getSelectedItem());;
        textField_name.setText(StringUtils.EMPTY);
        textField_attack.setText(StringUtils.EMPTY);
        textField_dataProcessing.setText(StringUtils.EMPTY);
        textField_firewall.setText(StringUtils.EMPTY);
        textField_rating.setText(StringUtils.EMPTY);
        textField_sleeze.setText(StringUtils.EMPTY);

    }


    public void onOpen(Stage stage, Campaign campaign, Device edit) {
        this.stage = stage;
        this.campaign = campaign;
        this.device = null;

        comboBox_pressets.setCellFactory(param -> new DevicePressetCell());
        comboBox_pressets.setButtonCell(new DevicePressetCell());
        comboBox_pressets.setItems(campaign.getDevicePressets());

        comboBox_pressets.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                textField_name.setText(newValue.getName());
                textField_sleeze.setText(String.valueOf(newValue.getSleeze()));
                textField_rating.setText(String.valueOf(newValue.getRating()));
                textField_firewall.setText(String.valueOf(newValue.getFirewall()));
                textField_dataProcessing.setText(String.valueOf(newValue.getDataProcessing()));
                textField_attack.setText(String.valueOf(newValue.getAttack()));
            }
        });

        textField_rating.textProperty().addListener(new NumericLimitListener(textField_rating, 0, null));
        textField_attack.textProperty().addListener(new NumericLimitListener(textField_attack, 0, null));
        textField_sleeze.textProperty().addListener(new NumericLimitListener(textField_sleeze, 0, null));
        textField_firewall.textProperty().addListener(new NumericLimitListener(textField_firewall, 0, null));
        textField_dataProcessing.textProperty().addListener(new NumericLimitListener(textField_dataProcessing, 0, null));

        button_ok.disableProperty().bind(
                textField_rating.textProperty().isEmpty().or(
                        textField_attack.textProperty().isEmpty().or(
                                textField_sleeze.textProperty().isEmpty().or(
                                        textField_firewall.textProperty().isEmpty().or(
                                                textField_dataProcessing.textProperty().isEmpty().or(
                                                        textField_name.textProperty().isEmpty()
                                                ))))));

        button_deletePresset.disableProperty().bind(
                comboBox_pressets.getSelectionModel().selectedItemProperty().isNull());

        button_savePresset.disableProperty().bind(button_ok.disableProperty());
        
        if(edit != null) {
            textField_name.setText(edit.getName());
            textField_attack.setText(String.valueOf(edit.getAttack()));
            textField_dataProcessing.setText(String.valueOf(edit.getDataProcessing()));
            textField_firewall.setText(String.valueOf(edit.getFirewall()));
            textField_rating.setText(String.valueOf(edit.getRating()));
            textField_sleeze.setText(String.valueOf(edit.getSleeze()));
        }
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

    private Device createDevice() {
        return new Device(textField_name.textProperty().get(),
                getRating(), getAttack(), getSleeze(), getFirewall(), getDataProcessing());
    }

    @Override
    public Stage getStage() {
        return stage;
    }
}
