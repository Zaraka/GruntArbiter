package org.shadowrun.common.factories;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.shadowrun.controllers.ControllerManageSquads;
import org.shadowrun.models.Campaign;

import java.io.IOException;

public class SquadDialogFactory {

    public ControllerManageSquads createDialog(Campaign campaign) throws IOException {
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/manageSquads.fxml"));
        root = loader.load();
        Stage dialog = new Stage();
        dialog.setTitle("Create new squad");
        dialog.setScene(new Scene(root));

        ControllerManageSquads controllerManageSquads = loader.getController();
        controllerManageSquads.onOpen(dialog, campaign);
        return  controllerManageSquads;
    }
}
