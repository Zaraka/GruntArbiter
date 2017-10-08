package org.shadowrun.common.utils;

import javafx.scene.image.Image;

import java.net.URL;

public class ImageUtils {

    private ImageUtils() {}

    public static Image loadAndCrop(URL url) {
        return new Image(url.toExternalForm(), 212, 278, true, true);
    }
}
