package org.shadowrun.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.shadowrun.models.Campaign;
import org.shadowrun.models.PlayerCharacter;

import java.util.Optional;

public class ControllerCampaignScreen {

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

        tableView_playerCharacters.setRowFactory(param -> {
            TableRow<PlayerCharacter> tableRow = new TableRow<>();

            MenuItem renamePlayer = new MenuItem("Rename player");
            renamePlayer.setOnAction(event -> {
                PlayerCharacter selected = tableView_playerCharacters.getSelectionModel().getSelectedItem();
                TextInputDialog dialog = new TextInputDialog(selected.getName());
                dialog.setTitle("Rename player");
                dialog.setHeaderText("Rename player " + selected.getName());
                dialog.setContentText("Please enter new name:");
                Optional<String> result = dialog.showAndWait();

                result.ifPresent(selected::setName);
            });
            MenuItem setPhysicalMonitor = new MenuItem("Set physical monitor");
            setPhysicalMonitor.setOnAction(event -> {
                PlayerCharacter selected = tableView_playerCharacters.getSelectionModel().getSelectedItem();
                TextInputDialog dialog = new TextInputDialog(String.valueOf(selected.getPhysicalMonitor()));
                dialog.setTitle("Set physical monitor");
                dialog.setHeaderText("Set " + selected.getName() + " monitor.");
                dialog.setContentText("Please enter new max physical monitor:");
                Optional<String> result = dialog.showAndWait();

                result.ifPresent(s -> selected.setPhysicalMonitor(Integer.parseInt(s)));
            });
            MenuItem setStunMonitor = new MenuItem("Set stun monitor");
            setStunMonitor.setOnAction(event -> {
                PlayerCharacter selected = tableView_playerCharacters.getSelectionModel().getSelectedItem();
                TextInputDialog dialog = new TextInputDialog(String.valueOf(selected.stunMonitorProperty()));
                dialog.setTitle("Set stun monitor");
                dialog.setHeaderText("Set " + selected.getName() + " monitor.");
                dialog.setContentText("Please enter new max stun monitor:");
                Optional<String> result = dialog.showAndWait();

                result.ifPresent(s -> selected.setStunMonitor(Integer.parseInt(s)));
            });
            MenuItem deletePlayer = new MenuItem("Delete player");
            deletePlayer.setOnAction(event -> tableView_playerCharacters.getItems()
                    .remove(tableView_playerCharacters.getSelectionModel().getSelectedIndex()));
            MenuItem addPlayer = new MenuItem("Add player");
            addPlayer.setOnAction(event -> controllerMain.addPlayerOnAction());
            ContextMenu fullContextMenu = new ContextMenu(
                    renamePlayer,
                    setPhysicalMonitor,
                    setStunMonitor,
                    new SeparatorMenuItem(),
                    deletePlayer,
                    addPlayer);
            ContextMenu emptyContextMenu = new ContextMenu(addPlayer);

            tableRow.emptyProperty().addListener((observable, oldValue, newValue) ->
                    tableRow.setContextMenu(newValue ? emptyContextMenu : fullContextMenu));
            return tableRow;
        });
    }

    public void remove() {

    }
}
