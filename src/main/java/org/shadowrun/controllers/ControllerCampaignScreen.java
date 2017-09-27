package org.shadowrun.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.shadowrun.common.factories.TextInputDialogFactory;
import org.shadowrun.models.Campaign;
import org.shadowrun.models.PlayerCharacter;

import javax.naming.Context;
import java.util.Optional;

public class ControllerCampaignScreen {

    private static final TextInputDialogFactory textInputDialogFactory = new TextInputDialogFactory();

    private Stage stage;
    private Campaign campaign;
    private ControllerMain controllerMain;

    @FXML
    private TableView<PlayerCharacter> tableView_playerCharacters;
    @FXML
    private TableColumn<PlayerCharacter, String> tableColumn_playerCharacters_character;
    @FXML
    private TableColumn<PlayerCharacter, Integer> tableColumn_playerCharacters_physicalMonitor;
    @FXML
    private TableColumn<PlayerCharacter, Integer> tableColumn_playerCharacters_stunMonitor;
    @FXML
    private TableColumn<PlayerCharacter, Integer> tableColumn_playerCharacters_spiritIndex;

    @FXML
    private TextField textField_name;
    @FXML
    private TextArea textArea_description;

    public void setStageAndListeners(Stage stage, ControllerMain controllerMain, Campaign campaign) {
        this.stage = stage;
        this.campaign = campaign;
        this.controllerMain = controllerMain;

        //Items
        tableView_playerCharacters.setItems(campaign.getPlayers());

        tableColumn_playerCharacters_character.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        tableColumn_playerCharacters_physicalMonitor
                .setCellValueFactory(param -> param.getValue().physicalMonitorProperty().asObject());
        tableColumn_playerCharacters_stunMonitor
                .setCellValueFactory(param -> param.getValue().stunMonitorProperty().asObject());
        tableColumn_playerCharacters_spiritIndex
                .setCellValueFactory(param -> param.getValue().spiritIndexProperty().asObject());

        MenuItem renamePlayer = new MenuItem("Rename player");
        renamePlayer.setOnAction(event -> {
            PlayerCharacter selected = tableView_playerCharacters.getSelectionModel().getSelectedItem();
            TextInputDialog dialog = textInputDialogFactory.createDialog(
                    "Rename player",
                    "Rename player " + selected.getName(),
                    "Please enter new name:",
                    selected.getName());
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(selected::setName);
        });
        MenuItem setPhysicalMonitor = new MenuItem("Set physical monitor");
        setPhysicalMonitor.setOnAction(event -> {
            PlayerCharacter selected = tableView_playerCharacters.getSelectionModel().getSelectedItem();
            TextInputDialog dialog = textInputDialogFactory.createDialog(
                    "Set physical monitor",
                    "Set " + selected.getName() + " monitor",
                    "Please enter new max physical monitor:",
                    String.valueOf(selected.getPhysicalMonitor()));
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(s -> selected.setPhysicalMonitor(Integer.parseInt(s)));
        });
        MenuItem setStunMonitor = new MenuItem("Set stun monitor");
        setStunMonitor.setOnAction(event -> {
            PlayerCharacter selected = tableView_playerCharacters.getSelectionModel().getSelectedItem();
            TextInputDialog dialog = textInputDialogFactory.createDialog(
                    "Set stun monitor",
                    "Set " + selected.getName() + " monitor",
                    "Please enter new max stun monitor:",
                    String.valueOf(selected.getStunMonitor()));
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(s -> selected.setStunMonitor(Integer.parseInt(s)));
        });
        MenuItem deletePlayer = new MenuItem("Delete player");
        deletePlayer.setOnAction(event -> tableView_playerCharacters.getItems()
                .remove(tableView_playerCharacters.getSelectionModel().getSelectedIndex()));
        MenuItem addPlayer = new MenuItem("Add player");
        addPlayer.setOnAction(event -> controllerMain.addPlayerOnAction());

        ContextMenu fullContextMenu = new ContextMenu(
                addPlayer,
                new SeparatorMenuItem(),
                renamePlayer,
                setPhysicalMonitor,
                setStunMonitor,
                deletePlayer);
        ContextMenu emptyContextMenu = new ContextMenu(addPlayer);
        tableView_playerCharacters.setContextMenu(emptyContextMenu);

        tableView_playerCharacters.setRowFactory(param -> {
            TableRow<PlayerCharacter> tableRow = new TableRow<>();

            tableRow.emptyProperty().addListener((observable, oldValue, newValue) -> {
                tableRow.setContextMenu((newValue) ? emptyContextMenu : fullContextMenu);
            });
            return tableRow;
        });

        textField_name.textProperty().bindBidirectional(campaign.nameProperty());
        textArea_description.textProperty().bindBidirectional(campaign.descriptionProperty());

    }

    public void remove() {
        textField_name.textProperty().unbindBidirectional(campaign.nameProperty());
        textArea_description.textProperty().unbindBidirectional(campaign.descriptionProperty());
    }
}
