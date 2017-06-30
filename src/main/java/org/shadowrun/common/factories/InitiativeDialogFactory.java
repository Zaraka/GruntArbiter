package org.shadowrun.common.factories;

import javafx.scene.control.TextInputDialog;
import org.shadowrun.models.Character;

public class InitiativeDialogFactory {
    public static TextInputDialog createDialog(Character character) {
        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle("Set initiative");
        dialog.setHeaderText("Set initiative for " + character.getName());
        dialog.setContentText("Please enter initative:");
        dialog.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                dialog.getEditor().setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        return dialog;
    }
}
