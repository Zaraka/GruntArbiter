package org.shadowrun.common;

import javafx.util.StringConverter;

import java.util.Arrays;
import java.util.List;

public class IterationTimeConverter extends StringConverter<Number> {

    private Integer startingTime;

    public IterationTimeConverter(Integer startingTime) {
        super();

        this.startingTime = startingTime;
    }

    @Override
    public String toString(Number object) {
        StringBuilder stringBuilder = new StringBuilder();
        if (object == null) {
            stringBuilder.append("00:00:00");
        } else {
            int current = startingTime + object.intValue() * 3;
            stringBuilder.append(String.format("%02d", current / 3600));
            stringBuilder.append(":");
            current %= 3600;
            stringBuilder.append(String.format("%02d", current / 60));
            stringBuilder.append(":");
            current %= 60;
            stringBuilder.append(String.format("%02d", current));
        }
        return stringBuilder.toString();
    }

    @Override
    public Integer fromString(String string) {
        List<String> parts = Arrays.asList(string.trim().split(":"));
        if (parts.isEmpty() || parts.size() < 3) {
            return 0;
        } else {
            return ((
                    Integer.parseInt(parts.get(0)) * 3600 +
                            Integer.parseInt(parts.get(1)) * 60 +
                            Integer.parseInt(parts.get(2))) - startingTime) / 3;
        }
    }
}
