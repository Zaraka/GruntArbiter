package org.shadowrun.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.shadowrun.common.NumericLimitListener;
import org.shadowrun.models.Host;

import java.util.Optional;

public class ControllerAddHost {

    private Host host;
    private Stage stage;

    @FXML
    private Button button_ok;
    @FXML
    private Button button_cancel;

    @FXML
    private TextField textField_rating;
    @FXML
    private ComboBox<String> comboBox_attack;
    @FXML
    private ComboBox<String> comboBox_sleeze;
    @FXML
    private ComboBox<String> comboBox_firewall;
    @FXML
    private ComboBox<String> comboBox_dataProcessing;

    @FXML
    private RadioButton radioButton_manual;

    @FXML
    private RadioButton radioButton_automatic;

    @FXML
    private void okOnAction() {
        if (radioButton_automatic.isSelected()) {
            host = new Host();
            host.randomize(getRating());
            stage.close();
        } else {
            int attack = getRating() + comboBox_attack.getSelectionModel().getSelectedIndex();
            int sleeze = getRating() + comboBox_sleeze.getSelectionModel().getSelectedIndex();
            int firewall = getRating() + comboBox_firewall.getSelectionModel().getSelectedIndex();
            int dataProcessing = getRating() + comboBox_dataProcessing.getSelectionModel().getSelectedIndex();

            if (attack == sleeze || attack == firewall ||
                    attack == dataProcessing || sleeze == firewall ||
                    firewall == dataProcessing || sleeze == dataProcessing) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Host attributes conflict");
                alert.setContentText("Every attribute needs to have unique value.");
                alert.showAndWait();
            } else {
                host = new Host();
                host.setRating(getRating());
                host.setAttack(attack);
                host.setSleeze(sleeze);
                host.setFirewall(firewall);
                host.setDataProcessing(dataProcessing);
                stage.close();
            }
        }
    }

    @FXML
    private void cancelOnAction() {
        stage.close();
    }

    public void onOpen(Stage stage) {
        this.stage = stage;
        this.host = null;

        radioButton_manual.setUserData(true);
        radioButton_automatic.setUserData(false);

        textField_rating.textProperty().addListener(new NumericLimitListener(textField_rating, 0, 12));

        ObservableList<String> attributes = FXCollections.observableArrayList();
        attributes.add("0");
        attributes.add("1");
        attributes.add("2");
        attributes.add("3");

        comboBox_attack.setItems(attributes);
        comboBox_attack.getSelectionModel().select(0);
        comboBox_sleeze.setItems(attributes);
        comboBox_sleeze.getSelectionModel().select(1);
        comboBox_firewall.setItems(attributes);
        comboBox_firewall.getSelectionModel().select(2);
        comboBox_dataProcessing.setItems(attributes);
        comboBox_dataProcessing.getSelectionModel().select(3);

        comboBox_attack.disableProperty().bind(radioButton_manual.selectedProperty().not());
        comboBox_sleeze.disableProperty().bind(radioButton_manual.selectedProperty().not());
        comboBox_firewall.disableProperty().bind(radioButton_manual.selectedProperty().not());
        comboBox_dataProcessing.disableProperty().bind(radioButton_manual.selectedProperty().not());

        button_ok.disableProperty().bind(textField_rating.textProperty().isEmpty());
    }

    public Optional<Host> getHost() {
        return Optional.ofNullable(host);
    }

    private int getRating() {
        return (textField_rating.textProperty().isEmpty().get()) ? 0 :
                Integer.parseInt(textField_rating.textProperty().get());
    }
}
