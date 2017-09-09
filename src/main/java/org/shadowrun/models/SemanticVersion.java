package org.shadowrun.models;

public class SemanticVersion {

    private int major;

    private int minor;

    private int patch;

    SemanticVersion(String version) {
        String[] versions = version.split("\\.");
        major = Integer.parseInt(versions[0]);
        minor = Integer.parseInt(versions[1]);
        patch = Integer.parseInt(versions[2]);
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public boolean isCompatible(SemanticVersion other) {
        return (major == other.major);
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}
