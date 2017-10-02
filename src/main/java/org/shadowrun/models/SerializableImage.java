package org.shadowrun.models;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class SerializableImage implements Observable {

    private static final Logger LOG = LoggerFactory.getLogger(SerializableImage.class);

    private StringProperty imageSource;

    private transient ObjectProperty<Image> image;

    public SerializableImage() {
        this.imageSource = new SimpleStringProperty();
        this.image = new SimpleObjectProperty<>(null);

        injectImage();
    }

    public SerializableImage(SerializableImage other) {
        this.imageSource = new SimpleStringProperty(other.getImageSource());
        this.image = new SimpleObjectProperty<>(null);
        if(!other.imageProperty().isNull().get())
            image.setValue(imageFromString(other.getImageSource()));

        injectImage();
    }

    private void injectImage() {
        image.addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(newValue, null);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                try {
                    ImageIO.write(bufferedImage, "png", bos);
                    byte[] imageBytes = bos.toByteArray();
                    this.imageSource.set(Base64.getEncoder().encodeToString(imageBytes));
                    bos.close();
                } catch (IOException ex) {
                    LOG.error("IOEception while updating image source: ", ex);
                }
            }
        });
    }

    public String getImageSource() {
        return imageSource.get();
    }

    public StringProperty imageSourceProperty() {
        return imageSource;
    }

    public ObjectProperty<Image> imageProperty() {
        if(image == null) {
            image = new SimpleObjectProperty<>();

            injectImage();
        } else if(!imageSource.isEmpty().get() && image.get() == null) {
            image.setValue(imageFromString(imageSource.get()));
            injectImage();
        }
        return image;
    }

    private Image imageFromString(String image) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(image));
        return new Image(byteArrayInputStream);
    }


    @Override
    public void addListener(InvalidationListener listener) {
        imageProperty().addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        imageProperty().removeListener(listener);
    }
}
