package org.shadowrun;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.shadowrun.controllers.ControllerMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/window.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Grunt Arbiter");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        ControllerMain controller = loader.getController();
        controller.setStageAndListeners(scene, primaryStage, this);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
