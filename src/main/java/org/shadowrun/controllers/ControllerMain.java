package org.shadowrun.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.shadowrun.logic.AppLogic;
import org.shadowrun.logic.BattleLogic;
import org.shadowrun.models.Character;
import org.shadowrun.models.PlayerCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class ControllerMain {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerMain.class);

    private AppLogic appLogic;
    private BattleLogic battleLogic;
    private Stage stage;

    //------------------------object injections
    @FXML
    private TableView<Character> tableView_masterTable;
    @FXML
    private TableColumn<Character, String> tableColumn_masterTable_character;
    @FXML
    private TableColumn<Character, Integer> tableColumn_masterTable_initiative;
    @FXML
    private TableColumn<Character, Integer> tableColumn_masterTable_turn1;
    @FXML
    private TableColumn<Character, Integer> tableColumn_masterTable_turn2;
    @FXML
    private TableColumn<Character, Integer> tableColumn_masterTable_turn3;
    @FXML
    private TableColumn<Character, Integer> tableColumn_masterTable_turn4;
    @FXML
    private TableColumn<Character, Integer> tableColumn_masterTable_turn5;
    @FXML
    private TableColumn<Character, Integer> tableColumn_masterTable_turn6;
    @FXML
    private TableColumn<Character, Integer> tableColumn_masterTable_turn7;

    @FXML
    private TableView<PlayerCharacter> tableView_playerCharacters;
    @FXML
    private TableColumn<PlayerCharacter, String> tableColumn_playerCharacters_character;

    @FXML
    private Menu menu_campaign;
    @FXML
    private MenuItem menuItem_addPlayer;
    @FXML
    private MenuItem menuItem_newBattle;
    @FXML
    private MenuItem menuItem_saveCampaign;

    @FXML
    private Tab tab_characters;
    @FXML
    private Tab tab_battle;


    //------------------------method hooks
    @FXML
    private void addCharacterOnAction() {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/addCharacter.fxml"));
            root = loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Create new character");
            dialog.setScene(new Scene(root));
            ControllerAddCharacter controllerAddCharacter = loader.getController();
            controllerAddCharacter.onOpen(dialog);
            dialog.showAndWait();
            controllerAddCharacter.getCharacter().ifPresent(playerCharacter -> battleLogic.getActiveBattle().getCharacters().add(playerCharacter));
            addBattleHooks();

        } catch (IOException ex) {
            LOG.error("Could not load addCharacter dialog: ", ex);
        }
    }

    @FXML
    private void addPlayerOnAction() {
        TextInputDialog dialog = new TextInputDialog("John Doe");
        dialog.setTitle("New player");
        dialog.setHeaderText("Create new player");
        dialog.setContentText("Please enter name for new player:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            appLogic.newCharacter(name);
            addCampaignHooks();
        });
    }

    @FXML
    private void newCampaignOnAction() {
        TextInputDialog dialog = new TextInputDialog("SampleCampaign");
        dialog.setTitle("New campaign");
        dialog.setHeaderText("Create new campaign");
        dialog.setContentText("Please enter name for new campaign:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            appLogic.newCampaign(name);
            addCampaignHooks();
        });
    }

    @FXML
    private void openCampaignOnAction() {
        FileChooser dialog = new FileChooser();
        dialog.setTitle("Open campaign");
        dialog.getExtensionFilters().add(new FileChooser.ExtensionFilter("Grunt campaign", "*.gra"));

        File file = dialog.showOpenDialog(stage);
        if (file != null) {
            appLogic.openCampaign(file);
            addCampaignHooks();
        }
    }

    @FXML
    private void newBattleOnAction() {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/newBattle.fxml"));
            root = loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Create new battle");
            dialog.setScene(new Scene(root));
            ControllerNewBattle controllerNewBattle = loader.getController();
            controllerNewBattle.onOpen(dialog, appLogic.getActiveCampaign().getPlayers());
            dialog.showAndWait();
            controllerNewBattle.getIncludedPlayers().ifPresent(playerCharacters -> battleLogic.createNewBattle(playerCharacters));
            addBattleHooks();

        } catch (IOException ex) {
            LOG.error("Could not load addCharacter dialog: ", ex);
        }
    }

    @FXML
    private void closeAppOnAction() {
        stage.close();
    }

    @FXML
    private void aboutOnAction() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Grunt Arbiter");
        alert.setHeaderText("Grunt Arbiter alpha version");
        alert.setContentText("Created by Zaraka.");
        alert.showAndWait();
    }

    private void addCampaignHooks() {
        tableView_playerCharacters.setItems(appLogic.getActiveCampaign().getPlayers());
    }

    private void addBattleHooks() {
        tableView_masterTable.setItems(battleLogic.getActiveBattle().getCharacters());
    }

    public void setStageAndListeners(Stage stage) {
        this.stage = stage;
        appLogic = new AppLogic();

        menu_campaign.disableProperty().bind(appLogic.hasCampaign());
        menuItem_newBattle.disableProperty().bind(appLogic.hasCampaign());
        menuItem_saveCampaign.disableProperty().bind(appLogic.hasCampaign());
        tab_characters.disableProperty().bind(appLogic.hasCampaign());
        tab_battle.disableProperty().bind(appLogic.hasCampaign());

        tableColumn_playerCharacters_character.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

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
        MenuItem deletePlayer = new MenuItem("Delete player");
        deletePlayer.setOnAction(event -> tableView_playerCharacters.getItems().remove(tableView_playerCharacters.getSelectionModel().getSelectedIndex()));
        tableView_playerCharacters.setContextMenu(new ContextMenu(renamePlayer, deletePlayer));

        tableColumn_masterTable_character.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        tableColumn_masterTable_initiative.setCellValueFactory(cellData -> cellData.getValue().initiativeProperty().asObject());
        tableColumn_masterTable_turn1.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().countTurn(1)));
        tableColumn_masterTable_turn2.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().countTurn(2)));
        tableColumn_masterTable_turn3.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().countTurn(3)));
        tableColumn_masterTable_turn4.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().countTurn(4)));
        tableColumn_masterTable_turn5.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().countTurn(5)));
        tableColumn_masterTable_turn6.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().countTurn(6)));
        tableColumn_masterTable_turn7.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().countTurn(7)));

        MenuItem moveToRealWorld = new MenuItem("Move to real world");
        MenuItem moveToAstralPlane = new MenuItem("Move to astral plane");
        MenuItem moveToMatrixSpace = new MenuItem("Move to matrix space");
        tableView_masterTable.setContextMenu(new ContextMenu(moveToRealWorld, moveToMatrixSpace, moveToAstralPlane));

    }
}
