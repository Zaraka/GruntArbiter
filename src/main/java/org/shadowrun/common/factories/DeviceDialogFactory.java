package org.shadowrun.common.factories;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.shadowrun.controllers.ControllerAddDevice;
import org.shadowrun.models.Campaign;
import org.shadowrun.models.Device;

import java.io.IOException;

public class DeviceDialogFactory {
    public ControllerAddDevice createDialog(Campaign campaign, Device edit) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/addDevice.fxml"));
        Parent root = loader.load();
        Stage dialog = new Stage();
        dialog.setTitle("Create new device");
        dialog.setScene(new Scene(root));
        ControllerAddDevice controllerAddDevice = loader.getController();
        controllerAddDevice.onOpen(dialog, campaign, edit);

        return controllerAddDevice;
    }
}
