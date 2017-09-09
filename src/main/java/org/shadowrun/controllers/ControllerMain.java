package org.shadowrun.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.shadowrun.common.factories.ExceptionDialogFactory;
import org.shadowrun.logic.AppLogic;
import org.shadowrun.logic.BattleLogic;
import org.shadowrun.models.Battle;
import org.shadowrun.models.Campaign;
import org.shadowrun.models.PlayerCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ControllerMain {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerMain.class);

    private AppLogic appLogic;
    private BattleLogic battleLogic;
    private Stage stage;

    //------------------------object injections
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
    private Menu menu_campaign;
    @FXML
    private MenuItem menuItem_newBattle;
    @FXML
    private MenuItem menuItem_saveCampaign;
    @FXML
    private MenuItem menuItem_saveAsCampaign;
    @FXML
    private MenuItem menuItem_closeCampaign;
    @FXML
    private Menu menu_recentCampaigns;
    @FXML
    private MenuItem menuItem_recentCampaign1;
    @FXML
    private MenuItem menuItem_recentCampaign2;
    @FXML
    private MenuItem menuItem_recentCampaign3;

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab tab_characters;


    //------------------------method hooks
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
        dialog.getExtensionFilters().add(new FileChooser.ExtensionFilter("Grunt campaign (.gra)", "*.gra"));

        File file = dialog.showOpenDialog(stage);
        if (file != null) {
            try {
                appLogic.openCampaign(file);
            } catch (IOException e) {
                LOG.error(e.getMessage());
                Alert alert = ExceptionDialogFactory.createExceptionDialog(
                        "Error!",
                        "Error occured while opening campaign file.",
                        "I/O exception", e);
                alert.showAndWait();
            }
            addCampaignHooks();
        }
    }

    @FXML
    private void saveCampaignOnAction() {
        if (appLogic.getCampaignFile() == null) {
            saveAsCampaignOnAction();
        } else {
            try {
                appLogic.saveCampaign();
            } catch (IOException e) {
                LOG.error(e.getMessage());
                Alert alert = ExceptionDialogFactory.createExceptionDialog(
                        "Error!",
                        "Error occured while saving campaign.",
                        "I/O exception", e);
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void saveAsCampaignOnAction() {
        FileChooser dialog = new FileChooser();
        dialog.setTitle("Save campaign");
        dialog.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Grunt campaign (.gra)", "*.gra"));

        File file = dialog.showSaveDialog(stage);
        if (file != null) {
            try {
                appLogic.saveAsCampaign(file);
            } catch (IOException e) {
                LOG.error(e.getMessage());
                Alert alert = ExceptionDialogFactory.createExceptionDialog(
                        "Error!",
                        "Error occured while saving campaign.",
                        "I/O exception", e);
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void closeCampaignOnAction() {
        List<Tab> toDelete = new ArrayList<>();
        for (Tab tab : tabPane.getTabs()) {
            Object userData = tab.getUserData();
            if (userData != null && userData.getClass() == ControllerBattle.class) {
                ControllerBattle controllerBattle = (ControllerBattle) userData;
                controllerBattle.remove();
                toDelete.add(tab);
            }
        }
        for (Tab tab : toDelete) {
            tabPane.getTabs().remove(tab);
        }

        appLogic.closeCampaign();
        addCampaignHooks();
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
            String battleSampleName = "Battle " + appLogic.getActiveCampaign().getBattles().size();
            controllerNewBattle.onOpen(dialog, battleSampleName, appLogic.getActiveCampaign().getPlayers());
            dialog.showAndWait();
            controllerNewBattle.getIncludedPlayers().ifPresent(playerCharacters -> {
                Battle battle = new Battle(
                        controllerNewBattle.getName(),
                        playerCharacters,
                        controllerNewBattle.getWeather(),
                        controllerNewBattle.getTime());

                battleLogic.activeBattleProperty().setValue(battle);
                appLogic.getActiveCampaign().getBattles().add(battle);

                openBattle(battle, false);

            });

        } catch (IOException ex) {
            LOG.error("Could not load newBattle dialog: ", ex);
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
        alert.setContentText("Created by Zaraka.\nhttp://www.github.com/zaraka/gruntarbiter");
        alert.showAndWait();
    }


    @FXML
    private void openRecentCampaign1OnAction() {
        try {
            appLogic.openCampaign(((Path) menuItem_recentCampaign1.getUserData()).toFile());
            addCampaignHooks();
        } catch (IOException e) {
            LOG.error("File doesn't exists");
        }
    }

    @FXML
    private void openRecentCampaign2OnAction() {
        try {
            appLogic.openCampaign(((Path) menuItem_recentCampaign2.getUserData()).toFile());
            addCampaignHooks();
        } catch (IOException e) {
            LOG.error("File doesn't exists");
        }
    }

    @FXML
    private void openRecentCampaign3OnAction() {
        try {
            appLogic.openCampaign(((Path) menuItem_recentCampaign3.getUserData()).toFile());
            addCampaignHooks();
        } catch (IOException e) {
            LOG.error("File doesn't exists");
        }
    }

    private void addCampaignHooks() {
        if (appLogic.getActiveCampaign() != null) {
            Campaign campaign = appLogic.getActiveCampaign();

            //Items
            tableView_playerCharacters.setItems(campaign.getPlayers());
            //tab selection
            tabPane.getSelectionModel().select(tab_characters);

            //open battles
            campaign.getBattles().forEach(battle -> openBattle(battle, true));
        }
    }

    public void setStageAndListeners(Stage stage) {
        this.stage = stage;
        appLogic = new AppLogic();
        battleLogic = new BattleLogic();

        menu_campaign.disableProperty().bind(appLogic.hasCampaign());
        menuItem_newBattle.disableProperty().bind(appLogic.hasCampaign());
        menuItem_saveCampaign.disableProperty().bind(appLogic.hasCampaign());
        menuItem_saveAsCampaign.disableProperty().bind(appLogic.hasCampaign());
        menuItem_closeCampaign.disableProperty().bind(appLogic.hasCampaign());
        tab_characters.disableProperty().bind(appLogic.hasCampaign());

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
            addPlayer.setOnAction(event -> addPlayerOnAction());
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
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                Object userData = newTab.getUserData();
                if (userData != null && userData.getClass() == ControllerBattle.class) {
                    ControllerBattle controllerBattle = (ControllerBattle) userData;
                    battleLogic.activeBattleProperty().setValue(controllerBattle.getBattle());
                }
            }
        });

        loadRecentFiles();
    }

    /**
     * These will not utilize observable list, because it's I/O operations
     */
    private void loadRecentFiles() {
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(menuItem_recentCampaign1);
        menuItems.add(menuItem_recentCampaign2);
        menuItems.add(menuItem_recentCampaign3);

        appLogic.getConfig().validateRecentFiles();
        List<Path> recentCampaigns = appLogic.getConfig().getRecentFiles();

        if (recentCampaigns.isEmpty()) {
            menu_recentCampaigns.disableProperty().setValue(true);
        } else {
            int itemIndex = 0;
            for (Path recentCampaign : recentCampaigns) {
                MenuItem recentCampaignMenuItem = menuItems.get(itemIndex);
                recentCampaignMenuItem.setUserData(recentCampaign);
                recentCampaignMenuItem.textProperty().setValue(recentCampaign.getFileName().toString());
                recentCampaignMenuItem.disableProperty().setValue(false);
                recentCampaignMenuItem.visibleProperty().setValue(true);
                itemIndex++;
            }

            for (; itemIndex < 3; itemIndex++) {
                MenuItem recentCampaignMenuItem = menuItems.get(itemIndex);
                recentCampaignMenuItem.disableProperty().setValue(true);
                recentCampaignMenuItem.visibleProperty().setValue(false);
                recentCampaignMenuItem.textProperty().setValue(StringUtils.EMPTY);
            }
        }
    }

    private void openBattle(Battle battle, boolean loaded) {
        try {
            FXMLLoader tabLoader = new FXMLLoader(getClass().getClassLoader().getResource("views/battle.fxml"));
            Tab battleTab = new Tab(battle.getName(), tabLoader.load());
            ControllerBattle controllerBattle = tabLoader.getController();
            battleTab.setUserData(controllerBattle);
            battleTab.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FIRE));
            battleTab.setOnClosed(event -> {
                controllerBattle.remove();
                tabPane.getTabs().removeIf(tab -> tab.getUserData() == controllerBattle);
                appLogic.getActiveCampaign().getBattles().remove(battle);
            });
            tabPane.getTabs().add(battleTab);
            controllerBattle.setStageAndListeners(battle, appLogic, battleLogic, loaded);
            battleLogic.activeBattleProperty().setValue(battle);
            tabPane.getSelectionModel().select(battleTab);
        } catch (IOException ex) {
            LOG.error("Error while loading battle tab ", ex);
        }
    }
}
