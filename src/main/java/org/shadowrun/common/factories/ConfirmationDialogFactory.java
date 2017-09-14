package org.shadowrun.common.factories;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;

public class ConfirmationDialogFactory {
    public Alert createDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getClassLoader().getResource("css/dark.css").toExternalForm());
        return alert;
    }
}
