package org.shadowrun.common;

import javafx.util.StringConverter;

import java.util.Arrays;
import java.util.List;

public class IterationTimeConverter extends StringConverter<Number> {

    @Override
    public String toString(Number object) {
        StringBuilder stringBuilder = new StringBuilder();
        if(object == null) {
            stringBuilder.append("00:00");
        } else {
            stringBuilder.append(object.intValue() * 5 / 60);
            stringBuilder.append(":");
            stringBuilder.append(object.intValue() * 5 % 60);
        }
        return stringBuilder.toString();
    }

    @Override
    public Integer fromString(String string) {
        List<String> parts = Arrays.asList(string.trim().split(":"));
        if(parts.isEmpty() || parts.size() < 2) {
            return 0;
        } else {
            return Integer.parseInt(parts.get(0)) * 12 + Integer.parseInt(parts.get(1)) / 5;
        }
    }
}
