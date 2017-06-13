package org.shadowrun.models;

public enum ICE {

    ACID("Acid"),
    BINDER("Binder"),
    BLACKIC("Black IC"),
    BLASTER("Blaster"),
    CRASH("Crash"),
    JAMMER("Jammer"),
    KILLEr("Killer"),
    MARKER("Marker"),
    PATROL("Patrol"),
    PROBE("Probe"),
    TARBABY("Tar Baby"),
    TRACK("Track");

    ICE(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }

    public static ICE fromName(String name) {
        return ICE.valueOf(name.replaceAll("\\s+", "").toUpperCase());
    }
}
