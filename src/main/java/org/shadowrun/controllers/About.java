package org.shadowrun.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.shadowrun.models.SemanticVersion;


public class About implements Controller {

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
        stringBuilder.append("GitHub project https://github.com/Zaraka/GruntArbiter/\n\n");

        stringBuilder.append("By using Grunt Arbiter you agree that you legally own a copy of the Shadowrun rulebook" +
                "and any sourcebook whose information you select to use. You are solely responsible for determining" +
                "the appropiatness of using oir redistributing the content You create and assume any risks associated" +
                "with your exercise of permissions under this license.");


        textArea_about.setText(stringBuilder.toString());
    }

    @Override
    public Stage getStage() {
        return stage;
    }
}
