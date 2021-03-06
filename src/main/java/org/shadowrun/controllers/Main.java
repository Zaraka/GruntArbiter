package org.shadowrun.controllers;

import com.sun.javafx.geom.Vec4d;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Application;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.shadowrun.common.exceptions.IncompatibleVersionsException;
import org.shadowrun.common.factories.DialogFactory;
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

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private static final DialogFactory dialogFactory = new DialogFactory();

    private AppLogic appLogic;
    private BattleLogic battleLogic;
    private Stage stage;
    private Scene scene;
    private Application application;

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
        TextInputDialog dialog = dialogFactory.createTextInputDialog(
                "New player",
                "Create new player",
                "Please enter name for new player:",
                appLogic.getConfig().getLatestPlayerName());

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            appLogic.newCharacter(name);
            appLogic.getConfig().setLatestPlayerName(name);
        });
    }

    @FXML
    public void newCampaignOnAction() {
        closeCampaignOnAction();

        TextInputDialog dialog = dialogFactory.createTextInputDialog(
                "New campaign",
                "Create new campaign",
                "Please enter name for new campaign:",
                "SampleCampaign");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {
            appLogic.newCampaign(name);
            addCampaignHooks();
        });
    }

    @FXML
    public void openCampaignOnAction() {
        closeCampaignOnAction();

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
                Alert alert = dialogFactory.createExceptionDialog(
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
            if (!file.getName().endsWith(".gra")) {
                file = new File(file.getAbsolutePath() + ".gra");
            }
            try {
                appLogic.saveAsCampaign(file);
            } catch (IOException e) {
                LOG.error(e.getMessage());
                Alert alert = dialogFactory.createExceptionDialog(
                        "Error!",
                        "Error occured while saving campaign.",
                        "I/O exception", e);

                alert.showAndWait();
            }
        }
    }

    @FXML
    public void closeCampaignOnAction() {
        FilteredList<Tab> campaignTabs = tabPane.getTabs().filtered(tab -> tab.getUserData() != null
                && tab.getUserData().getClass() == CampaignScreen.class);
        if (!campaignTabs.isEmpty()) {
            campaignTabs.get(0).getOnCloseRequest().handle(null);
        }
    }

    @FXML
    public void newBattleOnAction() {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/newBattle.fxml"));
            root = loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Create new battle");
            dialog.setScene(new Scene(root));
            NewBattle newBattle = loader.getController();
            String battleSampleName = "Battle " + appLogic.getActiveCampaign().getBattles().size();
            newBattle.onOpen(dialog, battleSampleName, appLogic.getActiveCampaign().getPlayers());
            dialog.showAndWait();
            newBattle.getIncludedPlayers().ifPresent(playerCharacters -> {
                Battle battle = new Battle(
                        newBattle.getName(),
                        playerCharacters,
                        newBattle.getWeather(),
                        newBattle.getTime(),
                        newBattle.getNoise());
                appLogic.getActiveCampaign().getBattles().add(battle);
                openBattle(battle, false);
            });

        } catch (IOException ex) {
            LOG.error("Could not load newBattle dialog: ", ex);
        }
    }

    @FXML
    private void closeAppOnAction() {
        if (checkSave()) {
            stage.close();
        }
    }

    @FXML
    private void aboutOnAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/about.fxml"));
            Parent root = loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("About Grunt Arbiter");
            dialog.setScene(new Scene(root));
            About about = loader.getController();
            about.onOpen(dialog, appLogic.getConfig().getVersion());
            dialog.showAndWait();
        } catch (IOException ex) {
            LOG.error("Cant load about dialog: ", ex);
        }
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
                .filtered(tab -> tab.getUserData() != null && tab.getUserData().getClass() == WelcomeScreen.class);
        if (welcomeScreens.isEmpty()) {
            openWelcomeScreen();
        } else {
            tabPane.getSelectionModel().select(welcomeScreens.get(0));
        }
    }

    @FXML
    private void reportBugOnAction() {
        application.getHostServices().showDocument("https://github.com/Zaraka/GruntArbiter/issues/new");
    }

    private void addCampaignHooks() {
        if (appLogic.getActiveCampaign() != null) {
            Campaign campaign = appLogic.getActiveCampaign();

            openCampaignScreen(campaign);

            //open battles
            campaign.getBattles().forEach(battle -> openBattle(battle, true));
        }
    }

    public void setStageAndListeners(Scene scene, Stage stage, Application app) {
        this.stage = stage;
        this.appLogic = new AppLogic();
        this.battleLogic = new BattleLogic();
        this.application = app;
        this.scene = scene;

        KeyCombination combinationNextTurn = new KeyCodeCombination(KeyCode.RIGHT,KeyCombination.CONTROL_DOWN);
        KeyCombination combinationPrevTurn = new KeyCodeCombination(KeyCode.LEFT,KeyCombination.CONTROL_DOWN);

        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (combinationNextTurn.match(event) && battleLogic.hasBattle().get()) {
                battleLogic.nextPhase();
                event.consume();
            } else if (combinationPrevTurn.match(event) && battleLogic.hasBattle().get()) {
                battleLogic.prevPhase();
                event.consume();
            }
        });

        Vec4d windowPos = appLogic.getConfig().loadWindowPos();

        stage.setX(windowPos.x);
        stage.setY(windowPos.y);
        stage.setWidth(windowPos.z);
        stage.setHeight(windowPos.w);

        stage.setMaximized(appLogic.getConfig().getMaximized());

        stage.setOnCloseRequest(event -> {
            appLogic.getConfig().saveWindowPos(
                    new Vec4d(stage.getX(),
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
                    battleLogic.setActiveBattle(controllerBattle.getBattle(), controllerBattle);
                }
            }
        });

        stage.titleProperty().bind(javafx.beans.binding.Bindings.createStringBinding(() ->
                        appLogic.getAppTitle(),
                appLogic.activeCampaignProperty(),
                appLogic.campaignFileProperty(),
                appLogic.changesSavedProperty()));

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
                if(itemIndex > 2)
                    break;
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
            WelcomeScreen welcomeScreen = tabLoader.getController();
            welcomeScreenTab.setUserData(welcomeScreen);
            welcomeScreenTab.setOnClosed(event -> {
                welcomeScreen.remove();
                tabPane.getTabs().removeIf(tab -> tab.getUserData() == welcomeScreen);
            });
            tabPane.getTabs().add(welcomeScreenTab);
            welcomeScreen.setStageAndListeners(stage, this, appLogic);
            tabPane.getSelectionModel().select(welcomeScreenTab);
        } catch (IOException ex) {
            LOG.error("Error while loading welcome screen tab ", ex);
        }
    }

    private void openCampaignScreen(Campaign campaign) {
        try {
            FXMLLoader tabLoader = new FXMLLoader(getClass().getClassLoader().getResource("views/campaign.fxml"));
            Tab campaignScreenTab = new Tab(campaign.getName(), tabLoader.load());
            CampaignScreen campaignScreen = tabLoader.getController();
            campaignScreenTab.setUserData(campaignScreen);
            campaignScreenTab.setOnCloseRequest(event -> {
                if (checkSave()) {
                    tabPane.getTabs().removeIf(tab -> tab.getUserData() != null &&
                            tab.getUserData().getClass() == ControllerBattle.class);
                    battleLogic.clear();
                    appLogic.closeCampaign();
                    campaignScreen.remove();
                    tabPane.getTabs().removeIf(tab -> tab.getUserData() == campaignScreen);
                } else {
                    event.consume();
                }
            });
            tabPane.getTabs().add(campaignScreenTab);
            campaignScreen.setStageAndListeners(stage, this, campaign, appLogic);
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
            battleTab.setOnCloseRequest(event -> {
                if(!appLogic.getConfig().getBattleClosePrompt()) {
                    Dialog<Boolean> dialog =
                            dialogFactory.createClosePromptDialog(
                                    "Close battle",
                                    "Are you sure you want to close battle " + battle.getName(),
                                    "The battle will be deleted.");
                    Optional<Boolean> result = dialog.showAndWait();
                    if (!result.isPresent()) {
                        event.consume();
                    } else {
                        appLogic.getConfig().setBattleClosePrompt(result.get());
                    }
                }
            });
            battleTab.setOnClosed(event -> {
                controllerBattle.remove();
                tabPane.getTabs().removeIf(tab -> tab.getUserData() == controllerBattle);
                appLogic.getActiveCampaign().getBattles().remove(battle);
            });
            tabPane.getTabs().add(battleTab);
            controllerBattle.setStageAndListeners(battle, appLogic, battleLogic, loaded);
            battleLogic.setActiveBattle(battle, controllerBattle);
            tabPane.getSelectionModel().select(battleTab);
        } catch (IOException ex) {
            LOG.error("Error while loading battle tab ", ex);
        }
    }

    public void openCampaign(File file) {
        if (file != null && file.exists()) {
            if (checkSave()) {
                try {
                    appLogic.openCampaign(file);
                    addCampaignHooks();
                } catch (IOException e) {
                    LOG.error(e.getMessage());
                    Alert alert = dialogFactory.createExceptionDialog(
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

    private boolean checkSave() {
        if (!appLogic.isChangesSaved()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Unsaved campaign");
            alert.setHeaderText("There are unsaved changes in campaign");
            alert.setContentText("Do you wish to save campaign first?");

            ButtonType buttonYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.NO);
            ButtonType buttonCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonYes, buttonNo, buttonCancel);

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(
                    getClass().getClassLoader().getResource("css/dark.css").toExternalForm());
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if(result.get().getButtonData().equals(ButtonBar.ButtonData.CANCEL_CLOSE)) {
                    return false;
                }
                else if (result.get().getButtonData().equals(ButtonBar.ButtonData.YES)) {
                    saveCampaignOnAction();
                }
            }
        }
        return true;
    }

}
