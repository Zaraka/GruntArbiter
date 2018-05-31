package org.shadowrun.common.converters;

import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class VehicleConverter extends StringConverter<String> {

    private Map<String, String> vehicles;

    public VehicleConverter(Map<String, String>vehicles) {
        this.vehicles = vehicles;
    }

    @Override
    public String toString(String object) {
        if(object == null) {
            return "Double click to select vehicle";
        } else {
            return (vehicles.get(object) == null) ? StringUtils.EMPTY : vehicles.get(object);
        }
    }

    @Override
    public String fromString(String string) {
        return null;
    }
}
