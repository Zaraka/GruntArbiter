package org.shadowrun.common;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;


public class NumericLimitListener implements ChangeListener<String> {

    private TextField hooked;

    private int limit;

    public NumericLimitListener(TextField hooked, int limit) {
        super();

        this.hooked = hooked;
        this.limit = limit;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (!newValue.matches("\\d*")) {
            hooked.setText(newValue.replaceAll("[^\\d]", ""));
        }

        Integer value;
        try {
            value = Integer.parseInt(hooked.textProperty().get());
        } catch (NumberFormatException ex) {
            value = 0;
        }

        if(value > limit) {
            hooked.setText(String.valueOf(limit));
        }
    }
}
