package org.shadowrun.controllers;

import com.sun.javafx.geom.Vec4d;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.shadowrun.common.exceptions.IncompatibleVersionsException;
import org.shadowrun.common.factories.ExceptionDialogFactory;
import org.shadowrun.logic.AppLogic;
import org.shadowrun.logic.BattleLogic;
import org.shadowrun.models.Battle;
import org.shadowrun.models.Campaign;
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

    private static final ExceptionDialogFactory exceptionDialogFactory = new ExceptionDialogFactory();

    private AppLogic appLogic;
    private BattleLogic battleLogic;
    private Stage stage;

    //------------------------object injections
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


    //------------------------method hooks
    @FXML
    public void addPlayerOnAction() {
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
    public void newCampaignOnAction() {
        TextInputDialog dialog = new TextInputDialog("SampleCampaign");
        dialog.setTitle("New campaign");
        dialog.setHeaderText("Create new campaign");
        dialog.setContentText("Please enter name for new campaign:");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getClassLoader().getResource("css/dark.css").toExternalForm());
        Optional<String> result = dialog.showAndWait();
        
        result.ifPresent(name -> {
            appLogic.newCampaign(name);
            addCampaignHooks();
        });
    }

    @FXML
    public void openCampaignOnAction() {
        FileChooser dialog = new FileChooser();
        dialog.setTitle("Open campaign");
        dialog.getExtensionFilters().add(new FileChooser.ExtensionFilter("Grunt campaign (.gra)", "*.gra"));

        File file = dialog.showOpenDialog(stage);
        if (file != null) {
            openCampaign(file);
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
                Alert alert = exceptionDialogFactory.createExceptionDialog(
                        "Error!",
                        "Error occured while saving campaign.",
                        "I/O exception", e);
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add(
                        getClass().getClassLoader().getResource("css/dark.css").toExternalForm());
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
            if(!file.getName().endsWith(".gra")) {
                file = new File(file.getName() + ".gra");
            }
            try {
                appLogic.saveAsCampaign(file);
            } catch (IOException e) {
                LOG.error(e.getMessage());
                Alert alert = exceptionDialogFactory.createExceptionDialog(
                        "Error!",
                        "Error occured while saving campaign.",
                        "I/O exception", e);

                alert.showAndWait();
            }
        }
    }

    @FXML
    private void closeCampaignOnAction() {
        FilteredList<Tab> campaignTabs = tabPane.getTabs().filtered(tab -> tab.getUserData() != null
                && tab.getUserData().getClass() == ControllerCampaignScreen.class);
        if(!campaignTabs.isEmpty())
            campaignTabs.get(0).getOnClosed().handle(null);
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
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getClassLoader().getResource("css/dark.css").toExternalForm());
        alert.setTitle("About Grunt Arbiter");
        alert.setHeaderText("Grunt Arbiter version: " + appLogic.getConfig().getVersion().toString());
        alert.setContentText("Created by Zaraka.\nhttp://www.github.com/zaraka/gruntarbiter");
        alert.showAndWait();
    }


    @FXML
    private void openRecentCampaign1OnAction() {
        openCampaign(((Path) menuItem_recentCampaign1.getUserData()).toFile());
    }

    @FXML
    private void openRecentCampaign2OnAction() {
        openCampaign(((Path) menuItem_recentCampaign2.getUserData()).toFile());
    }

    @FXML
    private void openRecentCampaign3OnAction() {
        openCampaign(((Path) menuItem_recentCampaign3.getUserData()).toFile());
    }

    @FXML
    private void openWelcomeScreenOnAction() {
        FilteredList<Tab> welcomeScreens = tabPane.getTabs()
                .filtered(tab -> tab.getUserData() != null && tab.getUserData().getClass() == ControllerWelcomeScreen.class);
        if(welcomeScreens.isEmpty()) {
            openWelcomeScreen();
        } else {
            tabPane.getSelectionModel().select(welcomeScreens.get(0));
        }
    }

    private void addCampaignHooks() {
        if (appLogic.getActiveCampaign() != null) {
            Campaign campaign = appLogic.getActiveCampaign();

            openCampaignScreen(campaign);

            //open battles
            campaign.getBattles().forEach(battle -> openBattle(battle, true));
        }
    }

    public void setStageAndListeners(Stage stage) {
        this.stage = stage;
        appLogic = new AppLogic();
        battleLogic = new BattleLogic();

        Vec4d windowPos = appLogic.getConfig().loadWindowPos();

        stage.setX(windowPos.x);
        stage.setY(windowPos.y);
        stage.setWidth(windowPos.z);
        stage.setHeight(windowPos.w);

        stage.setMaximized(appLogic.getConfig().getMaximized());

        stage.setOnCloseRequest(event -> {
            appLogic.getConfig().saveWindowPos(
                    new Vec4d(  stage.getX(),
                            stage.getY(),
                            stage.getWidth(),
                            stage.getHeight()));
            appLogic.getConfig().setMaximized(stage.isMaximized());
        });

        menu_campaign.disableProperty().bind(appLogic.hasCampaign());
        menuItem_newBattle.disableProperty().bind(appLogic.hasCampaign());
        menuItem_saveCampaign.disableProperty().bind(appLogic.hasCampaign());
        menuItem_saveAsCampaign.disableProperty().bind(appLogic.hasCampaign());
        menuItem_closeCampaign.disableProperty().bind(appLogic.hasCampaign());


        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                Object userData = newTab.getUserData();
                if (userData != null && userData.getClass() == ControllerBattle.class) {
                    ControllerBattle controllerBattle = (ControllerBattle) userData;
                    battleLogic.activeBattleProperty().setValue(controllerBattle.getBattle());
                }
            }
        });

        appLogic.activeCampaignProperty().addListener((observable, oldValue, newValue) -> {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Grunt Arbiter");
            if(newValue != null) {
                stringBuilder.append(" - ");
                stringBuilder.append(newValue.getName());
                if(appLogic.getCampaignFile() != null) {
                    stringBuilder.append(" [");
                    stringBuilder.append(appLogic.getCampaignFile().toPath().toString());
                    stringBuilder.append("]");
                }
            }
            stage.setTitle(stringBuilder.toString());
        });

        loadRecentFiles();
        openWelcomeScreen();
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

    private void openWelcomeScreen() {
        try {
            FXMLLoader tabLoader = new FXMLLoader(getClass().getClassLoader().getResource("views/welcomeScreen.fxml"));
            Tab welcomeScreenTab = new Tab("Welcome screen", tabLoader.load());
            ControllerWelcomeScreen controllerWelcomeScreen = tabLoader.getController();
            welcomeScreenTab.setUserData(controllerWelcomeScreen);
            welcomeScreenTab.setOnClosed(event -> {
                controllerWelcomeScreen.remove();
                tabPane.getTabs().removeIf(tab -> tab.getUserData() == controllerWelcomeScreen);
            });
            tabPane.getTabs().add(welcomeScreenTab);
            controllerWelcomeScreen.setStageAndListeners(stage, this, appLogic);
            tabPane.getSelectionModel().select(welcomeScreenTab);
        } catch (IOException ex) {
            LOG.error("Error while loading welcome screen tab ", ex);
        }
    }

    private void openCampaignScreen(Campaign campaign) {
        try {
            FXMLLoader tabLoader = new FXMLLoader(getClass().getClassLoader().getResource("views/campaign.fxml"));
            Tab campaignScreenTab = new Tab(campaign.getName(), tabLoader.load());
            ControllerCampaignScreen controllerCampaignScreen = tabLoader.getController();
            campaignScreenTab.setUserData(controllerCampaignScreen);
            campaignScreenTab.setOnClosed(event -> {
                tabPane.getTabs().removeIf(tab -> tab.getUserData() != null &&
                        tab.getUserData().getClass() == ControllerBattle.class);
                battleLogic.clear();
                appLogic.closeCampaign();
                controllerCampaignScreen.remove();
                tabPane.getTabs().removeIf(tab -> tab.getUserData() == controllerCampaignScreen);
            });
            tabPane.getTabs().add(campaignScreenTab);
            controllerCampaignScreen.setStageAndListeners(stage, this, campaign);
            tabPane.getSelectionModel().select(campaignScreenTab);
        } catch (IOException ex) {
            LOG.error("Error while loading campaign screen tab ", ex);
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

    public void openCampaign(File file) {
        if (file != null) {
            try {
                appLogic.openCampaign(file);
                addCampaignHooks();
            } catch (IOException e) {
                LOG.error(e.getMessage());
                Alert alert = exceptionDialogFactory.createExceptionDialog(
                        "Error!",
                        "Error occured while opening campaign file.",
                        "You campaign file could not be read", e);
                alert.showAndWait();
            } catch (IncompatibleVersionsException ex) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Oh, drek!");
                alert.setHeaderText("Version of campaign you are trying to load is incompoatible with app version");
                alert.setContentText("This means, that loading this campaign could crash or corrupt campaign file.\ndo you still want to load?");
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add(
                        getClass().getClassLoader().getResource("css/dark.css").toExternalForm());
                Optional<ButtonType> result = alert.showAndWait();
                result.ifPresent(buttonType -> {
                    if (buttonType == ButtonType.OK) {
                        appLogic.loadCampaign(file, ex.getCampaign());
                        addCampaignHooks();
                    }
                });
            }
        }
    }
}
