package org.shadowrun.common.constants;

public enum VehicleType {
    DRONE(6),
    VEHICLE(12);

    private int conditionBase;

    VehicleType(int base) {
        conditionBase = base;
    }

    public int getConditionBase() {
        return conditionBase;
    }
}
