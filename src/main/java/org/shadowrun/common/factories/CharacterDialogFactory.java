package org.shadowrun.common.factories;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.shadowrun.common.constants.CharacterType;
import org.shadowrun.controllers.ControllerAddCharacter;
import org.shadowrun.models.Campaign;
import org.shadowrun.models.Character;

import java.io.IOException;

public class CharacterDialogFactory {
    public ControllerAddCharacter createDialog(Campaign campaign, CharacterType characterType, Character edit) throws IOException {

        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/addCharacter.fxml"));
        root = loader.load();
        Stage dialog = new Stage();
        dialog.setTitle("Create new character");
        dialog.setScene(new Scene(root));
        ControllerAddCharacter controllerAddCharacter = loader.getController();
        controllerAddCharacter.onOpen(dialog, campaign, characterType, edit);

        return controllerAddCharacter;
    }
}
