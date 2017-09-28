package org.shadowrun.common.factories;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.shadowrun.controllers.ControllerAddVehicle;
import org.shadowrun.models.Vehicle;

import java.io.IOException;

public class VehicleDialogFactory {
    public ControllerAddVehicle createDialog(Vehicle edit) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/addVehicle.fxml"));
        Parent root = loader.load();
        Stage dialog = new Stage();
        dialog.setTitle("Create new vehicle");
        dialog.setScene(new Scene(root));
        ControllerAddVehicle controllerAddVehicle = loader.getController();
        controllerAddVehicle.onOpen(dialog, edit);

        return controllerAddVehicle;
    }
}
