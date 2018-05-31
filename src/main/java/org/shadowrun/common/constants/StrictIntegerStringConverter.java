package org.shadowrun.common.constants;

import javafx.util.StringConverter;

public class StrictIntegerStringConverter extends StringConverter<Integer> {

    @Override
    public Integer fromString(String value) {
        // If the specified value is null or zero-length, return null
        if (value == null) {
            return 0;
        }

        value = value.trim();

        if (value.length() < 1) {
            return 0;
        }

        return Integer.valueOf(value);
    }

    @Override
    public String toString(java.lang.Integer value) {
        // If the specified value is null, return a zero-length String
        if (value == null) {
            return "0";
        }

        return (Integer.toString(value));
    }
}
