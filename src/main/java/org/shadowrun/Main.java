package org.shadowrun;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/window.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Grunt Arbiter");
        primaryStage.getIcons().add(new Image(getClass()
                .getClassLoader().getResource("icons/icon.png").toExternalForm()));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        org.shadowrun.controllers.Main controller = loader.getController();
        controller.setStageAndListeners(scene, primaryStage, this);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
