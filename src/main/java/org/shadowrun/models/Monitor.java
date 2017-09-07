package org.shadowrun.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Monitor {

    private IntegerProperty max;

    private IntegerProperty current;

    public Monitor(int max) {
        this.max = new SimpleIntegerProperty(max);
        this.current = new SimpleIntegerProperty(max);
    }

    public Monitor(int max, int current) {
        this.max = new SimpleIntegerProperty(max);
        this.current = new SimpleIntegerProperty(current);
    }

    public int getMax() {
        return max.get();
    }

    public IntegerProperty maxProperty() {
        return max;
    }

    public int getCurrent() {
        return current.get();
    }

    public IntegerProperty currentProperty() {
        return current;
    }

    public int countWoundModifier() {
        return (max.get() - current.get()) / 3;
    }

    public void increase(int inc) {
        current.setValue(current.get() + inc);
        int maxVal = max.get();
        if(current.get() > maxVal) {
            current.setValue(maxVal);
        }
    }

    public void decrease(int dec) {
        current.setValue(current.get() - dec);
        if(current.get() < 0) {
            current.setValue(0);
        }
    }
}