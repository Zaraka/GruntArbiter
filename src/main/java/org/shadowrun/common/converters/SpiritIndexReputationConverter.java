package org.shadowrun.common.converters;

import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;

public class SpiritIndexReputationConverter extends StringConverter<Number> {
    @Override
    public String toString(Number object) {
        if(object == null) {
            return StringUtils.EMPTY;
        } else {
            return String.valueOf(object.intValue() / 5);
        }
    }

    @Override
    public Number fromString(String string) {
        if(string == null) {
            return 0;
        } else {
            return Integer.parseInt(string);
        }
    }
}
