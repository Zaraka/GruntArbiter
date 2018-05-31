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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.shadowrun.common.NumericLimitListener;
import org.shadowrun.common.constants.CharacterType;
import org.shadowrun.common.constants.ICE;
import org.shadowrun.common.nodes.cells.ICeCell;
import org.shadowrun.controllers.*;
import org.shadowrun.logic.AppLogic;
import org.shadowrun.models.*;
import org.shadowrun.models.Character;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.ThreadLocalRandom;

public class DialogFactory {

    private static String style;

    private static Image icon;

    public DialogFactory() {
        style = getClass().getClassLoader().getResource("css/dark.css").toExternalForm();
        icon = new Image(getClass()
                .getClassLoader().getResource("icons/icon.png").toExternalForm());
    }

    public VehicleCombat createVehicleChaseDialog(Battle battle) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/addVehicleCombat.fxml"));
        Parent root = loader.load();
        Stage dialog = new Stage();
        dialog.getIcons().add(icon);
        dialog.setTitle("Engage vehicle combat");
        dialog.setScene(new Scene(root));

        VehicleCombat vehicleCombat = loader.getController();
        vehicleCombat.onOpen(dialog, battle);

        return vehicleCombat;
    }

    public AddDevice createDeviceDialog(Campaign campaign, Device edit) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/addDevice.fxml"));
        Parent root = loader.load();
        Stage dialog = new Stage();
        dialog.getIcons().add(icon);
        dialog.setTitle("Create new device");
        dialog.setScene(new Scene(root));

        AddDevice addDevice = loader.getController();
        addDevice.onOpen(dialog, campaign, edit);

        return addDevice;
    }

    public AddBarrier createBarrierDialog(AppLogic appLogic) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/addBarrier.fxml"));
        Parent root = loader.load();
        Stage dialog = new Stage();
        dialog.getIcons().add(icon);
        dialog.setTitle("Create new barrier");
        dialog.setScene(new Scene(root));

        AddBarrier addBarrier = loader.getController();
        addBarrier.onOpen(dialog, appLogic);

        return addBarrier;
    }

    public ManageSquads createSquadDialog(Campaign campaign, AppLogic appLogic) throws IOException {
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/manageSquads.fxml"));
        root = loader.load();
        Stage dialog = new Stage();
        dialog.getIcons().add(icon);
        dialog.setTitle("Create new squad");
        dialog.setScene(new Scene(root));

        ManageSquads manageSquads = loader.getController();
        manageSquads.onOpen(dialog, campaign, appLogic);
        return manageSquads;
    }

    public AddCharacter createCharacterDialog(
            Campaign campaign, CharacterType characterType, Character edit, AppLogic appLogic) throws IOException {

        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/addCharacter.fxml"));
        root = loader.load();
        Stage dialog = new Stage();
        dialog.getIcons().add(icon);
        dialog.setTitle("Create new character");
        dialog.setScene(new Scene(root));

        AddCharacter addCharacter = loader.getController();
        addCharacter.onOpen(dialog, campaign, characterType, edit, appLogic);

        return addCharacter;
    }

    public AddVehicle createVehicleDialog(Campaign campaign, Vehicle edit) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/addVehicle.fxml"));
        Parent root = loader.load();
        Stage dialog = new Stage();
        dialog.getIcons().add(icon);
        dialog.setTitle("Create new vehicle");
        dialog.setScene(new Scene(root));

        AddVehicle addVehicle = loader.getController();
        addVehicle.onOpen(dialog, campaign, edit);

        return addVehicle;
    }

    public MonitorSettings createMonitorDialog(Monitor monitor, String monitorName) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/monitorSettings.fxml"));
        Parent root = loader.load();
        Stage dialog = new Stage();
        dialog.getIcons().add(icon);
        dialog.setTitle(monitorName + " settings");
        dialog.setScene(new Scene(root));

        MonitorSettings monitorSettings = loader.getController();
        monitorSettings.onOpen(dialog, monitor, monitorName);

        return monitorSettings;
    }

    public AddHost createHostDialog() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/addHost.fxml"));
        Parent root = loader.load();
        Stage dialog = new Stage();
        dialog.setTitle("Generate new host");
        dialog.setScene(new Scene(root));
        dialog.getIcons().add(icon);

        AddHost addHost = loader.getController();
        addHost.onOpen(dialog);

        return addHost;
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

    public Dialog<Boolean> createClosePromptDialog(String title, String header, String content) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        CheckBox checkBox_dontShow = new CheckBox("Don't show this dialog again");
        checkBox_dontShow.setSelected(false);

        grid.add(new Label(content), 0, 0);
        grid.add(checkBox_dontShow, 0, 1);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(style);
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(icon);

        dialogPane.setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return checkBox_dontShow.isSelected();
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

        Button rollDiceButton = new Button("Roll dice");
        rollDiceButton.setOnAction(event -> {
            int initiativeRoll = host.getRating();
            for(int i = 0; i < 4; i++) {
                initiativeRoll += ThreadLocalRandom.current().nextInt(1, 6 + 1);
            }
            initiative.setText(String.valueOf(initiativeRoll));
        });

        HBox comboButton = new HBox();
        comboButton.getChildren().add(initiative);
        comboButton.getChildren().add(rollDiceButton);

        grid.add(new Label("ICE:"), 0, 0);
        grid.add(iceChoice, 1, 0);
        grid.add(new Label("Initiative " + host.getDataProcessing() + " + 4d6 ="), 0, 1);
        grid.add(comboButton, 1, 1);

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
