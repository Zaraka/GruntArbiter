package org.shadowrun.common.constants;

public enum ICE {

    ACID("Acid"),
    BINDER("Binder"),
    BLACKIC("Black IC"),
    BLASTER("Blaster(Grey IC)"),
    CRASH("Crash"),
    JAMMER("Jammer"),
    KILLER("Killer"),
    MARKER("Marker"),
    PATROL("Patrol"),
    PROBE("Probe"),
    SCRAMBLE("Scramble"),
    SPARKY("Sparky"),
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
