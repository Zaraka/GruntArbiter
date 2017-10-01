package org.shadowrun.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.shadowrun.models.SemanticVersion;


public class ControllerAbout implements Controller {

    private Stage stage;

    @FXML
    private TextArea textArea_about;

    @FXML
    private void okOnAction() {
        stage.close();
    }

    public void onOpen(Stage stage, SemanticVersion version) {
        this.stage = stage;

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Version: ");
        stringBuilder.append(version.toString());
        stringBuilder.append("\n");
        stringBuilder.append("Grunt arbiter is licensed under GPLv3 for full license please access https://www.gnu.org/licenses/gpl-3.0.en.html\n\n");

        stringBuilder.append("Created by Zaraka\n");
        stringBuilder.append("GitHub project https://github.com/Zaraka/GruntArbiter/\n");


        textArea_about.setText(stringBuilder.toString());
    }

    @Override
    public Stage getStage() {
        return stage;
    }
}
