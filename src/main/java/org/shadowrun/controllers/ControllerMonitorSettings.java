package org.shadowrun.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.shadowrun.common.NumericLimitListener;
import org.shadowrun.models.Monitor;

import java.util.Optional;

public class ControllerMonitorSettings {

    private Stage stage;
    private Monitor monitor;

    @FXML
    private Label label_header;

    @FXML
    private TextField textField_currentValue;
    @FXML
    private TextField textField_maxValue;

    @FXML
    private Button button_ok;

    @FXML
    private void currentValuePlusOnAction() {
        int current = getCurrentValue() + 1;
        if (current <= getMaxValue()) {
            textField_currentValue.setText(String.valueOf(current));
        }
    }

    @FXML
    private void currentValueMinusOnAction() {
        int current = getCurrentValue() - 1;
        if (current > 0) {
            textField_currentValue.setText(String.valueOf(current));
        }
    }

    @FXML
    private void maxValuePlusOnAction() {
        textField_maxValue.setText(String.valueOf(getMaxValue() + 1));
    }

    @FXML
    private void maxValueMinusOnAction() {
        int max = getMaxValue() - 1;
        if (max > 0) {
            textField_maxValue.setText(String.valueOf(max));
        }
    }

    @FXML
    private void okOnAction() {
        monitor = new Monitor(getMaxValue(), getCurrentValue());
        stage.close();
    }

    @FXML
    private void cancelOnAction() {
        stage.close();
    }


    public void onOpen(Stage stage, Monitor monitor, String monitorName) {
        this.stage = stage;
        this.monitor = null;

        textField_currentValue.setText(String.valueOf(monitor.getCurrent()));
        textField_currentValue.textProperty().addListener(
                new NumericLimitListener(textField_currentValue, 0, null));
        textField_maxValue.setText(String.valueOf(monitor.getMax()));
        textField_maxValue.textProperty().addListener(
                new NumericLimitListener(textField_maxValue, 0, null));

        textField_maxValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                if(Integer.parseInt(newValue) < getCurrentValue())
                    textField_currentValue.setText(newValue);
            }
        });

        button_ok.disableProperty()
                .bind(textField_currentValue.textProperty().isEmpty()
                        .or(textField_maxValue.textProperty().isEmpty()));

        label_header.textProperty().setValue(monitorName);
    }

    public Optional<Monitor> getMonitor() {
        return Optional.ofNullable(monitor);
    }

    private int getMaxValue() {
        return Integer.parseInt(textField_maxValue.getText());
    }

    private int getCurrentValue() {
        return Integer.parseInt(textField_currentValue.getText());
    }
}
