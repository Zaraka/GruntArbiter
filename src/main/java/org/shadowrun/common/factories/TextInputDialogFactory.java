package org.shadowrun.common.factories;

import javafx.scene.control.DialogPane;
import javafx.scene.control.TextInputDialog;

public class TextInputDialogFactory {
    public TextInputDialog createDialog(String title, String header, String content, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getClassLoader().getResource("css/dark.css").toExternalForm());
        return dialog;
    }
}
