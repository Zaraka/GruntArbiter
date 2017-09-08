package org.shadowrun.models;

import java.net.URI;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class AppConfig {
    private Preferences preferences;

    public AppConfig() {
        preferences = Preferences.userNodeForPackage(AppConfig.class);
    }

    public List<Path> getRecentFiles() {
        List<Path> result = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            try {
                Path recentFile = Paths.get(URI.create(preferences.get("recentFile" + i, "")));
                result.add(recentFile);
            } catch(SecurityException | FileSystemNotFoundException | IllegalArgumentException ex) {
                //do nothing
            }
        }
        return result;
    }

    public void setRecentFiles(List<Path> paths) {
        for(int i = 0; i < paths.size(); i++) {
            preferences.put("recentFile" + i, paths.get(i).toUri().toString());
        }
    }

    public void validateRecentFiles() {
        setRecentFiles(getRecentFiles().stream().filter(path -> path.toFile().exists()).collect(Collectors.toList()));
    }
}
