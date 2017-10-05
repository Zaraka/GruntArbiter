package org.shadowrun.models;

import com.sun.javafx.geom.Vec4d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class AppConfig {

    private static final Logger LOG = LoggerFactory.getLogger(AppConfig.class);

    private static final String WINDOW_X = "WINDOW_X";
    private static final String WINDOW_Y = "WINDOW_Y";
    private static final String WINDOW_WIDTH = "WINDOW_WIDTH";
    private static final String WINDOW_HEIGHT = "WINDOW_HEIGHT";
    private static final String WINDOW_MAXIMIZED = "WINDOW_MAXIMIZED";
    private static final String APPLY_WOUND = "ApplyWound";

    private Preferences preferences;

    private Properties properties;

    private SemanticVersion version;

    public AppConfig() {
        preferences = Preferences.userNodeForPackage(AppConfig.class);
        properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream(".properties"));
            version = new SemanticVersion(properties.getProperty("app.version"));
        } catch (IOException ex) {
            LOG.error("Cant load maven properties: ", ex);
        }
    }

    public List<Path> getRecentFiles() {
        List<Path> result = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            try {
                Path recentFile = Paths.get(URI.create(preferences.get("recentFile" + i, "")));
                result.add(recentFile);
            } catch (SecurityException | FileSystemNotFoundException | IllegalArgumentException ex) {
                //do nothing
            }
        }
        return result;
    }

    public void setRecentFiles(List<Path> paths) {
        for (int i = 0; i < 3; i++) {
            preferences.remove("recentFile" + i);
        }

        for (int i = 0; i < paths.size(); i++) {
            preferences.put("recentFile" + i, paths.get(i).toUri().toString());
        }
    }

    public void validateRecentFiles() {
        setRecentFiles(getRecentFiles().stream().filter(path -> path.toFile().exists()).collect(Collectors.toList()));
    }

    public void insertOrRefreshRecentCampaign(Path campaign) {
        List<Path> recentCampaigns = getRecentFiles();

        if (recentCampaigns.contains(campaign)) {
            recentCampaigns.remove(campaign);
        }

        recentCampaigns.add(0, campaign);

        setRecentFiles(recentCampaigns);
    }

    public SemanticVersion getVersion() {
        return version;
    }

    public void saveWindowPos(Vec4d window) {
        preferences.putDouble(WINDOW_X, window.x);
        preferences.putDouble(WINDOW_Y, window.y);
        preferences.putDouble(WINDOW_WIDTH, window.z);
        preferences.putDouble(WINDOW_HEIGHT, window.w);
    }

    public Vec4d loadWindowPos() {
        return new Vec4d(preferences.getFloat(WINDOW_X, 0),
                preferences.getFloat(WINDOW_Y, 0),
                preferences.getFloat(WINDOW_WIDTH, 1280),
                preferences.getFloat(WINDOW_HEIGHT, 720));
    }

    public boolean getApplyWound() {
        return preferences.getBoolean(APPLY_WOUND, true);
    }

    public void setApplyWound(boolean value) {
        preferences.putBoolean(APPLY_WOUND, value);
    }

    public boolean getMaximized() {
        return preferences.getBoolean(WINDOW_MAXIMIZED, false);
    }

    public void setMaximized(boolean maximized) {
        preferences.putBoolean(WINDOW_MAXIMIZED, maximized);
    }
}
