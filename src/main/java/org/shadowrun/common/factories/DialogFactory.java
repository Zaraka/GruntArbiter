package org.shadowrun.common.factories;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.shadowrun.common.constants.CharacterType;
import org.shadowrun.controllers.ControllerAddCharacter;
import org.shadowrun.controllers.ControllerAddDevice;
import org.shadowrun.controllers.ControllerAddVehicle;
import org.shadowrun.controllers.ControllerManageSquads;
import org.shadowrun.models.Campaign;
import org.shadowrun.models.Character;
import org.shadowrun.models.Device;
import org.shadowrun.models.Vehicle;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class DialogFactory {

    private static String style;

    private static Image icon;

    public DialogFactory() {
        style = getClass().getClassLoader().getResource("css/dark.css").toExternalForm();
        icon = new Image(getClass()
                .getClassLoader().getResource("icons/icon.png").toExternalForm());
    }

    public ControllerAddDevice createDeviceDialog(Campaign campaign, Device edit) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/addDevice.fxml"));
        Parent root = loader.load();
        Stage dialog = new Stage();
        dialog.getIcons().add(icon);
        dialog.setTitle("Create new device");
        dialog.setScene(new Scene(root));

        ControllerAddDevice controllerAddDevice = loader.getController();
        controllerAddDevice.onOpen(dialog, campaign, edit);

        return controllerAddDevice;
    }

    public ControllerManageSquads createSquadDialog(Campaign campaign) throws IOException {
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/manageSquads.fxml"));
        root = loader.load();
        Stage dialog = new Stage();
        dialog.getIcons().add(icon);
        dialog.setTitle("Create new squad");
        dialog.setScene(new Scene(root));

        ControllerManageSquads controllerManageSquads = loader.getController();
        controllerManageSquads.onOpen(dialog, campaign);
        return  controllerManageSquads;
    }

    public ControllerAddCharacter createCharacterDialog(
            Campaign campaign, CharacterType characterType, Character edit) throws IOException {

        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/addCharacter.fxml"));
        root = loader.load();
        Stage dialog = new Stage();
        dialog.getIcons().add(icon);
        dialog.setTitle("Create new character");
        dialog.setScene(new Scene(root));

        ControllerAddCharacter controllerAddCharacter = loader.getController();
        controllerAddCharacter.onOpen(dialog, campaign, characterType, edit);

        return controllerAddCharacter;
    }

    public ControllerAddVehicle createVehicleDialog(Vehicle edit) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/addVehicle.fxml"));
        Parent root = loader.load();
        Stage dialog = new Stage();
        dialog.getIcons().add(icon);
        dialog.setTitle("Create new vehicle");
        dialog.setScene(new Scene(root));

        ControllerAddVehicle controllerAddVehicle = loader.getController();
        controllerAddVehicle.onOpen(dialog, edit);

        return controllerAddVehicle;
    }

    public Alert createExceptionDialog(String title, String header, String content, Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        Label label = new Label("The exception stacktrace was:");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String exceptionText = sw.toString();

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setExpandableContent(expContent);
        dialogPane.getStylesheets().add(style);
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(icon);
        return alert;
    }

    public Alert createConfirmationDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(style);
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(icon);
        return alert;
    }

    public TextInputDialog createTextInputDialog(String title, String header, String content, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(style);
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(icon);
        return dialog;
    }
}
