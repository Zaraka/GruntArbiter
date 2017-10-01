package org.shadowrun.common;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;


public class NumericLimitListener implements ChangeListener<String> {

    private TextField hooked;

    private Integer min;

    private Integer max;

    private boolean allowMinus;

    public NumericLimitListener(TextField hooked, Integer min, Integer max) {
        super();

        this.hooked = hooked;
        this.min = min;
        this.max = max;

        allowMinus = (min == null || min < 0);
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if(allowMinus) {
            if (!newValue.matches("(^\\d*|^-\\d*)")) {
                hooked.setText(oldValue);
            }
        } else {
            if (!newValue.matches("^\\d*")) {
                hooked.setText(oldValue);
            }
        }



        Integer value;
        try {
            value = Integer.parseInt(hooked.textProperty().get());
        } catch (NumberFormatException ex) {
            value = 0;
        }

        if(min != null && value < min) {
            hooked.setText(String.valueOf(min));
        } else if (max != null && value > max) {
            hooked.setText(String.valueOf(max));
        }
    }
}
