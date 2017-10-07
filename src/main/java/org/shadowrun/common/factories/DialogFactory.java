package org.shadowrun.common.factories;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.shadowrun.common.NumericLimitListener;
import org.shadowrun.common.constants.CharacterType;
import org.shadowrun.common.constants.ICE;
import org.shadowrun.common.nodes.cells.ICeCell;
import org.shadowrun.controllers.*;
import org.shadowrun.models.*;
import org.shadowrun.models.Character;

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

    public ControllerAddBarrier createBarrierDialog() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/addBarrier.fxml"));
        Parent root = loader.load();
        Stage dialog = new Stage();
        dialog.getIcons().add(icon);
        dialog.setTitle("Create new barrier");
        dialog.setScene(new Scene(root));

        ControllerAddBarrier controllerAddBarrier = loader.getController();
        controllerAddBarrier.onOpen(dialog);

        return controllerAddBarrier;
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
        return controllerManageSquads;
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

    public ControllerMonitorSettings createMonitorDialog(Monitor monitor, String monitorName) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/monitorSettings.fxml"));
        Parent root = loader.load();
        Stage dialog = new Stage();
        dialog.getIcons().add(icon);
        dialog.setTitle(monitorName + " settings");
        dialog.setScene(new Scene(root));

        ControllerMonitorSettings controllerMonitorSettings = loader.getController();
        controllerMonitorSettings.onOpen(dialog, monitor, monitorName);

        return controllerMonitorSettings;
    }

    public ControllerAddHost createHostDialog() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/addHost.fxml"));
        Parent root = loader.load();
        Stage dialog = new Stage();
        dialog.setTitle("Generate new host");
        dialog.setScene(new Scene(root));
        dialog.getIcons().add(icon);

        ControllerAddHost controllerAddHost = loader.getController();
        controllerAddHost.onOpen(dialog);

        return controllerAddHost;
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

    public class InitiativeDialogResult {
        private Integer initiative;
        private Boolean applyWound;

        InitiativeDialogResult(int initiative, boolean applyWound) {
            this.applyWound = applyWound;
            this.initiative = initiative;
        }

        public Integer getInitiative() {
            return initiative;
        }

        public Boolean getApplyWound() {
            return applyWound;
        }
    }

    public Dialog<InitiativeDialogResult> createInitiativeDialog(Character character, boolean applyWound) {
        Dialog<InitiativeDialogResult> dialog = new Dialog<>();
        dialog.setTitle("Set initiative");
        dialog.setHeaderText("Set initiative for " + character.getName());

        ButtonType okButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField textField_initiative = new TextField("0");
        textField_initiative.textProperty().addListener(new NumericLimitListener(textField_initiative, 0, null));

        CheckBox checkBox_applyWound = new CheckBox("Apply wound modifier");
        checkBox_applyWound.setSelected(applyWound);

        grid.add(new Label("Please enter initative:"), 0, 0);
        grid.add(textField_initiative, 1, 0);
        grid.add(checkBox_applyWound, 0, 1);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(style);
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(icon);

        dialogPane.setContent(grid);
        Platform.runLater(textField_initiative::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                int initiative = Integer.parseInt(textField_initiative.getText());
                int wounded = initiative - (character.getPhysicalMonitor().countWoundModifier() +
                        character.getStunMonitor().countWoundModifier());
                return new InitiativeDialogResult(
                        (checkBox_applyWound.isSelected()) ? wounded : initiative,
                        checkBox_applyWound.isSelected());
            }
            return null;
        });

        return dialog;
    }

    public Dialog<Pair<String, String>> createICeDialog(Host host) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("ICe");
        dialog.setHeaderText("Create new ICe");

        ButtonType okButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<ICE> iceChoice = new ComboBox<>(FXCollections.observableArrayList(ICE.values()));
        iceChoice.setCellFactory(param -> new ICeCell());
        iceChoice.setButtonCell(new ICeCell());

        TextField initiative = new TextField();
        initiative.setPromptText("6");

        grid.add(new Label("ICE:"), 0, 0);
        grid.add(iceChoice, 1, 0);
        grid.add(new Label("Initiative " + host.getDataProcessing() + " + 4d6 ="), 0, 1);
        grid.add(initiative, 1, 1);

        Node addButton = dialog.getDialogPane().lookupButton(okButtonType);
        addButton.setDisable(true);

        initiative.textProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(newValue.trim().isEmpty());
            if (!newValue.matches("\\d*")) {
                initiative.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(style);
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(icon);

        dialogPane.setContent(grid);
        Platform.runLater(iceChoice::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return new Pair<>(iceChoice.getSelectionModel().getSelectedItem().name(), initiative.getText());
            }
            return null;
        });

        return dialog;
    }
}