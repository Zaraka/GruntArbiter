package org.shadowrun.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.converter.NumberStringConverter;
import org.apache.commons.lang3.StringUtils;
import org.shadowrun.common.IterationTimeConverter;
import org.shadowrun.common.NumericLimitListener;
import org.shadowrun.common.cells.CharacterCell;
import org.shadowrun.common.cells.ObjectCell;
import org.shadowrun.common.cells.TurnTableCell;
import org.shadowrun.common.cells.WeatherCell;
import org.shadowrun.common.constants.ICE;
import org.shadowrun.common.constants.Weather;
import org.shadowrun.common.constants.World;
import org.shadowrun.common.exceptions.NextTurnException;
import org.shadowrun.common.factories.CharacterIconFactory;
import org.shadowrun.common.factories.ExceptionDialogFactory;
import org.shadowrun.common.factories.InitiativeDialogFactory;
import org.shadowrun.logic.AppLogic;
import org.shadowrun.logic.BattleLogic;
import org.shadowrun.models.*;
import org.shadowrun.models.Character;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ControllerMain {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerMain.class);

    private AppLogic appLogic;
    private BattleLogic battleLogic;
    private Stage stage;

    //------------------------object injections
    @FXML
    private TableView<Character> tableView_masterTable;
    @FXML
    private TableColumn<Character, Character> tableColumn_masterTable_character;
    @FXML
    private TableColumn<Character, String> tableColumn_masterTable_condition;
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
    private TableView<Barrier> tableView_barrier;
    @FXML
    private TableColumn<Barrier, Barrier> tableColumn_barrier_object;
    @FXML
    private TableColumn<Barrier, Integer> tableColumn_barrier_structure;
    @FXML
    private TableColumn<Barrier, Integer> tableColumn_barrier_armor;

    @FXML
    private TableView<Device> tableView_devices;
    @FXML
    private TableColumn<Device, String> tableColumn_device_device;
    @FXML
    private TableColumn<Device, Integer> tableColumn_device_condition;
    @FXML
    private TableColumn<Device, Integer> tableColumn_device_rating;
    @FXML
    private TableColumn<Device, Integer> tableColumn_device_attack;
    @FXML
    private TableColumn<Device, Integer> tableColumn_device_sleeze;
    @FXML
    private TableColumn<Device, Integer> tableColumn_device_firewall;
    @FXML
    private TableColumn<Device, Integer> tableColumn_device_dataProcessing;

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
    private Menu menu_battle;


    @FXML
    private TabPane tabPane;
    @FXML
    private Tab tab_characters;
    @FXML
    private Tab tab_battle;


    @FXML
    private VBox vbox_realWorld;
    @FXML
    private VBox vbox_matrix;
    @FXML
    private VBox vbox_astralPlane;
    @FXML
    private VBox vbox_matrixProperties;


    @FXML
    private HBox hbox_selected_character;
    @FXML
    private HBox hbox_selected_barrier;
    @FXML
    private HBox hbox_selected_glyph;
    @FXML
    private HBox hbox_selected_device;


    @FXML
    private Label label_combatTurnCounter;
    @FXML
    private Label label_intiativePassCounter;
    @FXML
    private Label label_actionPhaseCounter;
    @FXML
    private Label label_currentCharacter;

    @FXML
    private Label label_host_rating;
    @FXML
    private Label label_host_attack;
    @FXML
    private Label label_host_sleeze;
    @FXML
    private Label label_host_firewall;
    @FXML
    private Label label_host_dataProcessing;

    @FXML
    private Label label_overwatchScore;
    @FXML
    private Label label_time;
    @FXML
    private Label label_selectedCharacter;

    @FXML
    private Button button_nextTurn;
    @FXML
    private Button button_prevTurn;

    @FXML
    private TextField textField_selected_physical;
    @FXML
    private TextField textField_selected_stun;
    @FXML
    private TextField textField_selected_structure;
    @FXML
    private TextField textField_selected_armor;
    @FXML
    private TextField textField_selected_condition;
    @FXML
    private TextField textField_backgroundCount;

    @FXML
    private ComboBox<Weather> comboBox_weather;

    @FXML
    private AnchorPane anchorPane_selected;

    @FXML
    private FontAwesomeIconView fontAwesomeIcon_selected;


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
            controllerAddCharacter.getCharacter().ifPresent(playerCharacter -> {
                battleLogic.getActiveBattle().getCharacters().add(playerCharacter);
                addBattleHooks();
            });

        } catch (IOException ex) {
            LOG.error("Could not load addCharacter dialog: ", ex);
        }
    }

    @FXML
    private void addICeOnAction() {
        if (battleLogic.getActiveBattle().getICe().size() >= battleLogic.getActiveBattle().getHost().getRating()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Too much ICe");
            alert.setHeaderText("ICe number exceeds host rating");
            alert.setContentText("Spawn another anyway?");
            Optional<ButtonType> result = alert.showAndWait();
            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return;
            }
        }

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("ICe");
        dialog.setHeaderText("Create new ICe");

        ButtonType okButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<String> iceChoice = new ComboBox<>(
                FXCollections.observableArrayList(
                        Arrays.stream(ICE.values()).map(ICE::getName).collect(Collectors.toList())));
        TextField initiative = new TextField();
        initiative.setPromptText("6");

        grid.add(new Label("ICE:"), 0, 0);
        grid.add(iceChoice, 1, 0);
        grid.add(new Label("Initiative:"), 0, 1);
        grid.add(initiative, 1, 1);

        Node addButton = dialog.getDialogPane().lookupButton(okButtonType);
        addButton.setDisable(true);

        initiative.textProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(newValue.trim().isEmpty());
            if (!newValue.matches("\\d*")) {
                initiative.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(iceChoice::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return new Pair<>(iceChoice.getSelectionModel().getSelectedItem(), initiative.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(res -> {
            ICE ice = ICE.fromName(res.getKey().replaceAll("\\s+", "").toUpperCase());
            Integer initiativeInt = Integer.parseInt(res.getValue());
            battleLogic.spawnICe(ice, initiativeInt);
            tableView_masterTable.refresh();
        });
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
        dialog.getExtensionFilters().add(new FileChooser.ExtensionFilter("Grunt campaign (.gra)", "*.gra"));

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
            controllerNewBattle.onOpen(dialog, appLogic.getActiveCampaign().getPlayers());
            dialog.showAndWait();
            controllerNewBattle.getIncludedPlayers().ifPresent(playerCharacters -> {
                battleLogic.createNewBattle(playerCharacters, controllerNewBattle.getWeather(), controllerNewBattle.getTime());
                setNewInitiative();
                addBattleHooks();
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
    private void nextTurnOnAction() {
        try {
            battleLogic.nextPhase();
        } catch (NextTurnException e) {
            setNewInitiative();
            battleLogic.refreshPhase();
        }
        tableView_masterTable.refresh();
    }

    @FXML
    private void prevTurnOnAction() {
        try {
            battleLogic.prevPhase();
        } catch (NextTurnException e) {
            setNewInitiative();
            battleLogic.refreshPhase();
        }
        tableView_masterTable.refresh();
    }

    @FXML
    private void generateHostOnAction() {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/addHost.fxml"));
            root = loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Generate new host");
            dialog.setScene(new Scene(root));
            ControllerAddHost controllerAddHost = loader.getController();
            controllerAddHost.onOpen(dialog);
            dialog.showAndWait();
            controllerAddHost.getHost().ifPresent(host -> {
                battleLogic.setHost(host);
            });

        } catch (IOException ex) {
            LOG.error("Could not load addCharacter dialog: ", ex);
        }
    }

    @FXML
    private void physicalPlusOnAction() {
        Character character = tableView_masterTable.getSelectionModel().getSelectedItem();
        character.physicalMonitorProperty().setValue(character.getPhysicalMonitor() + 1);
    }

    @FXML
    private void physicalMinusOnAction() {
        Character character = tableView_masterTable.getSelectionModel().getSelectedItem();
        character.physicalMonitorProperty().setValue(character.getPhysicalMonitor() - 1);
    }

    @FXML
    private void stunPlusOnAction() {
        Character character = tableView_masterTable.getSelectionModel().getSelectedItem();
        character.stunMonitorProperty().setValue(character.getStunMonitor() + 1);
    }

    @FXML
    private void stunMinusOnAction() {
        Character character = tableView_masterTable.getSelectionModel().getSelectedItem();
        character.stunMonitorProperty().setValue(character.getStunMonitor() - 1);
    }

    @FXML
    private void structurePlusOnAction() {
        Barrier barrier = tableView_barrier.getSelectionModel().getSelectedItem();
        barrier.structureProperty().setValue(barrier.getStructure() + 1);
    }

    @FXML
    private void structureMinusOnAction() {
        Barrier barrier = tableView_barrier.getSelectionModel().getSelectedItem();
        barrier.structureProperty().setValue(barrier.getStructure() - 1);
    }

    @FXML
    private void armorPlusOnAction() {
        Barrier barrier = tableView_barrier.getSelectionModel().getSelectedItem();
        barrier.armorProperty().setValue(barrier.getArmor() + 1);
    }

    @FXML
    private void armorMinusOnAction() {
        Barrier barrier = tableView_barrier.getSelectionModel().getSelectedItem();
        barrier.armorProperty().setValue(barrier.getArmor() - 1);
    }

    @FXML
    private void conditionPlusOnAction() {
        Device device = tableView_devices.getSelectionModel().getSelectedItem();
        device.conditionProperty().setValue(device.getCondition() + 1);
    }

    @FXML
    private void conditionMinusOnAction() {
        Device device = tableView_devices.getSelectionModel().getSelectedItem();
        device.conditionProperty().setValue(device.getCondition() - 1);
    }

    @FXML
    private void backgroundCountPlusOnAction() {
        battleLogic.raiseBackgroundCount();
    }

    @FXML
    private void backgroundCountMinusOnAction() {
        battleLogic.decreaseBackgroundCount();
    }

    @FXML
    private void overwatchScorePlusOnAction() {
        battleLogic.raiseOverwatchScore();
    }

    @FXML
    private void overwatchScoreMinusOnAction() {
        battleLogic.decreaseOverwatchScore();
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

    @FXML
    private void overwatchScoreResetOnAction() {
        battleLogic.resetOverwatchScoore();
    }

    @FXML
    private void addBarrierOnAction() {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/addBarrier.fxml"));
            root = loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Create new barrier");
            dialog.setScene(new Scene(root));
            ControllerAddBarrier controllerAddBarrier = loader.getController();
            controllerAddBarrier.onOpen(dialog);
            dialog.showAndWait();
            controllerAddBarrier.getBarrier()
                    .ifPresent(barrier -> battleLogic.getActiveBattle().getBarriers().add(barrier));

        } catch (IOException ex) {
            LOG.error("Could not load addBarier dialog: ", ex);
        }
    }

    @FXML
    private void addDeviceOnAction() {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/addDevice.fxml"));
            root = loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Create new device");
            dialog.setScene(new Scene(root));
            ControllerAddDevice controllerAddDevice = loader.getController();
            controllerAddDevice.onOpen(dialog);
            dialog.showAndWait();
            controllerAddDevice.getDevice().ifPresent(device -> battleLogic.getActiveBattle().getDevices().add(device));

        } catch (IOException ex) {
            LOG.error("Could not load addDevice dialog: ", ex);
        }
    }

    private void addCampaignHooks() {
        if (appLogic.getActiveCampaign() != null) {
            //Items
            tableView_playerCharacters.setItems(appLogic.getActiveCampaign().getPlayers());
            //tab selection
            tabPane.getSelectionModel().select(tab_characters);
        }
    }

    private void addBattleHooks() {
        Battle battle = battleLogic.getActiveBattle();
        
        //Items
        tableView_masterTable.setItems(battle.getCharacters());
        tableView_barrier.setItems(battle.getBarriers());
        tableView_devices.setItems(battle.getDevices());

        //unbinds
        label_combatTurnCounter.textProperty().unbind();
        label_intiativePassCounter.textProperty().unbind();
        label_actionPhaseCounter.textProperty().unbind();
        label_currentCharacter.textProperty().unbind();
        label_host_attack.textProperty().unbind();
        label_host_sleeze.textProperty().unbind();
        label_host_firewall.textProperty().unbind();
        label_host_dataProcessing.textProperty().unbind();
        label_host_rating.textProperty().unbind();
        textField_backgroundCount.textProperty().unbind();
        label_overwatchScore.textProperty().unbind();
        vbox_matrixProperties.visibleProperty().unbind();
        label_time.textProperty().unbind();
        button_prevTurn.disableProperty().unbind();
        comboBox_weather.valueProperty().unbind();

        if (battle != null) {
            //binds
            label_combatTurnCounter.textProperty().bind(battle.combatTurnProperty().asString());
            label_intiativePassCounter.textProperty().bind(battle.initiativePassProperty().asString());
            label_actionPhaseCounter.textProperty().bind(battle.actionPhaseProperty().asString());
            label_currentCharacter.textProperty().bind(battleLogic.currentCharacterNameProperty());
            label_host_attack.textProperty().bind(battle.getHost().attackProperty().asString());
            label_host_sleeze.textProperty().bind(battle.getHost().sleezeProperty().asString());
            label_host_firewall.textProperty().bind(battle.getHost().firewallProperty().asString());
            label_host_dataProcessing.textProperty().bind(battle.getHost().dataProcessingProperty().asString());
            label_host_rating.textProperty().bind(battle.getHost().ratingProperty().asString());
            textField_backgroundCount.textProperty().bindBidirectional(battle.backgroundCountProperty(), new NumberStringConverter());
            label_overwatchScore.textProperty().bind(battle.getHost().overwatchScoreProperty().asString());
            vbox_matrixProperties.visibleProperty().bind(battle.getHost().isInitalized());
            Bindings.bindBidirectional(label_time.textProperty(), battle.combatTurnProperty(), new IterationTimeConverter(battle.getTime()));

            battle.currentCharacterProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    tableView_masterTable.getSelectionModel().select(newValue);
                }
            });

            comboBox_weather.valueProperty().bindBidirectional(battle.selectedWeatherProperty());

            button_prevTurn.disableProperty()
                    .bind(battle.actionPhaseProperty().greaterThan(1)
                            .or(battle.initiativePassProperty().greaterThan(1)
                                    .or(battle.combatTurnProperty().greaterThan(1))));

            //tab selection
            tabPane.getSelectionModel().select(tab_battle);
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

        menu_battle.disableProperty().bind(battleLogic.hasBattle());
        tab_battle.disableProperty().bind(battleLogic.hasBattle());
        button_nextTurn.disableProperty().bind(battleLogic.hasBattle());
        button_prevTurn.disableProperty().bind(battleLogic.hasBattle());

        tableColumn_playerCharacters_character.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

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
            MenuItem deletePlayer = new MenuItem("Delete player");
            deletePlayer.setOnAction(event -> tableView_playerCharacters.getItems()
                    .remove(tableView_playerCharacters.getSelectionModel().getSelectedIndex()));
            MenuItem addPlayer = new MenuItem("Add player");
            addPlayer.setOnAction(event -> addPlayerOnAction());
            ContextMenu fullContextMenu = new ContextMenu(renamePlayer, deletePlayer, addPlayer);
            ContextMenu emptyContextMenu = new ContextMenu(addPlayer);

            tableRow.emptyProperty().addListener((observable, oldValue, newValue) ->
                    tableRow.setContextMenu(newValue ? emptyContextMenu : fullContextMenu));
            return tableRow;
        });

        tableColumn_masterTable_character.setCellFactory(param -> new CharacterCell());
        tableColumn_masterTable_character.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumn_masterTable_condition.setCellValueFactory(cellData -> Bindings.createStringBinding(
                () -> MessageFormat.format("{0}/{1}", cellData.getValue().getPhysicalMonitor(), cellData.getValue().getStunMonitor()),
                cellData.getValue().physicalMonitorProperty(),
                cellData.getValue().stunMonitorProperty()));
        tableColumn_masterTable_initiative.setCellValueFactory(cellData -> cellData.getValue().initiativeProperty().asObject());
        tableColumn_masterTable_turn1.setCellFactory(param -> new TurnTableCell());
        tableColumn_masterTable_turn2.setCellFactory(param -> new TurnTableCell());
        tableColumn_masterTable_turn3.setCellFactory(param -> new TurnTableCell());
        tableColumn_masterTable_turn4.setCellFactory(param -> new TurnTableCell());
        tableColumn_masterTable_turn5.setCellFactory(param -> new TurnTableCell());
        tableColumn_masterTable_turn6.setCellFactory(param -> new TurnTableCell());
        tableColumn_masterTable_turn7.setCellFactory(param -> new TurnTableCell());
        tableColumn_masterTable_turn1
                .setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().countTurn(1)));
        tableColumn_masterTable_turn2
                .setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().countTurn(2)));
        tableColumn_masterTable_turn3
                .setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().countTurn(3)));
        tableColumn_masterTable_turn4
                .setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().countTurn(4)));
        tableColumn_masterTable_turn5
                .setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().countTurn(5)));
        tableColumn_masterTable_turn6
                .setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().countTurn(6)));
        tableColumn_masterTable_turn7
                .setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().countTurn(7)));

        tableView_masterTable.setRowFactory(param -> {
            MenuItem moveToRealWorld = new MenuItem("Move to meatspace");
            moveToRealWorld.setOnAction(event -> {
                tableView_masterTable.getSelectionModel().getSelectedItem().setWorld(World.REAL);
                tableView_masterTable.refresh();
            });
            MenuItem moveToAstralPlane = new MenuItem("Move to astral world");
            moveToAstralPlane.setOnAction(event -> {
                tableView_masterTable.getSelectionModel().getSelectedItem().setWorld(World.ASTRAL);
                tableView_masterTable.refresh();
            });
            MenuItem moveToMatrixSpace = new MenuItem("Move to matrix");
            moveToMatrixSpace.setOnAction(event -> {
                tableView_masterTable.getSelectionModel().getSelectedItem().setWorld(World.MATRIX);
                tableView_masterTable.refresh();
            });
            SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
            MenuItem setInitiative = new MenuItem("Set initiative");
            setInitiative.setOnAction(event -> {
                Character selectedChar = tableView_masterTable.getSelectionModel().getSelectedItem();
                TextInputDialog dialog = InitiativeDialogFactory.createDialog(selectedChar);
                Optional<String> result = dialog.showAndWait();
                result.ifPresent(initiative -> {
                    selectedChar.setInitiative(Integer.parseInt(initiative));
                    battleLogic.getActiveBattle().updateCurrentCharacter();
                    tableView_masterTable.refresh();
                });
            });
            MenuItem addCharacter = new MenuItem("Add character");
            addCharacter.setOnAction(event -> addCharacterOnAction());
            MenuItem removeCharacter = new MenuItem("Remove character");
            removeCharacter.setOnAction(event -> battleLogic.getActiveBattle().getCharacters()
                    .remove(tableView_masterTable.getSelectionModel().getSelectedItem()));
            ContextMenu fullContextMenu = new ContextMenu(
                    moveToRealWorld,
                    moveToMatrixSpace,
                    moveToAstralPlane,
                    separatorMenuItem,
                    setInitiative,
                    addCharacter,
                    removeCharacter);
            ContextMenu emptyContextMenu = new ContextMenu(addCharacter);
            TableRow<Character> tableRow = new TableRow<Character>() {
                @Override
                protected void updateItem(Character item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        tableView_masterTable.getSortOrder().setAll(tableColumn_masterTable_initiative);
                        switch (item.getWorld()) {
                            case REAL:
                                setStyle(null);
                                break;
                            case ASTRAL:
                                setStyle("-fx-control-inner-background: tomato;");
                                break;
                            case MATRIX:
                                setStyle("-fx-control-inner-background: springgreen;");
                                break;
                        }
                    } else {
                        setStyle(null);
                    }
                }
            };
            tableRow.emptyProperty().addListener((observable, oldValue, newValue) ->
                    tableRow.setContextMenu(newValue ? emptyContextMenu : fullContextMenu));
            return tableRow;
        });
        tableView_masterTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                cleanSelectedPane();
            }

            if (newValue == null) {
                cleanSelectedPane();
            } else {
                tableView_barrier.getSelectionModel().clearSelection();
                tableView_devices.getSelectionModel().clearSelection();

                CharacterIconFactory.createIcon(newValue).ifPresent(fontAwesomeIcon -> {
                    fontAwesomeIcon_selected.setIcon(fontAwesomeIcon);
                    hbox_selected_glyph.setVisible(true);
                });

                textField_selected_physical.textProperty()
                        .bindBidirectional(newValue.physicalMonitorProperty(), new NumberStringConverter());
                textField_selected_stun.textProperty()
                        .bindBidirectional(newValue.stunMonitorProperty(), new NumberStringConverter());
                label_selectedCharacter.textProperty().bind(newValue.nameProperty());

                hbox_selected_character.setVisible(true);
                anchorPane_selected.setVisible(true);
            }
        });

        tableColumn_barrier_object.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumn_barrier_object.setCellFactory(param -> new ObjectCell());
        tableColumn_barrier_armor.setCellValueFactory(param -> param.getValue().armorProperty().asObject());
        tableColumn_barrier_structure.setCellValueFactory(param -> param.getValue().structureProperty().asObject());

        tableView_barrier.setRowFactory(param -> {
            TableRow<Barrier> tableRow = new TableRow<>();

            MenuItem renameBarrier = new MenuItem("Rename barrier");
            renameBarrier.setOnAction(event -> {
                Barrier selected = tableView_barrier.getSelectionModel().getSelectedItem();
                TextInputDialog dialog = new TextInputDialog(selected.getName());
                dialog.setTitle("Rename barrier");
                dialog.setHeaderText("Rename barrier " + selected.getName());
                dialog.setContentText("Please enter new name:");
                Optional<String> result = dialog.showAndWait();

                result.ifPresent(selected::setName);
            });
            MenuItem deleteBarrier = new MenuItem("Delete barrier");
            deleteBarrier.setOnAction(event -> tableView_barrier.getItems()
                    .remove(tableView_barrier.getSelectionModel().getSelectedIndex()));
            MenuItem addBarrier = new MenuItem("Add barrier");
            addBarrier.setOnAction(event -> addBarrierOnAction());
            ContextMenu fullContextMenu = new ContextMenu(addBarrier, deleteBarrier, renameBarrier);
            ContextMenu emptyContextMenu = new ContextMenu(addBarrier);

            tableRow.emptyProperty().addListener((observable, oldValue, newValue) ->
                    tableRow.setContextMenu(newValue ? emptyContextMenu : fullContextMenu));
            return tableRow;
        });

        tableView_barrier.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                cleanSelectedPane();
            }

            if (newValue == null) {
                cleanSelectedPane();
            } else {
                tableView_masterTable.getSelectionModel().clearSelection();
                tableView_devices.getSelectionModel().clearSelection();

                fontAwesomeIcon_selected.setIcon(FontAwesomeIcon.SQUARE);

                textField_selected_structure.textProperty()
                        .bindBidirectional(newValue.structureProperty(), new NumberStringConverter());
                textField_selected_armor.textProperty()
                        .bindBidirectional(newValue.armorProperty(), new NumberStringConverter());
                label_selectedCharacter.textProperty().bind(newValue.nameProperty());

                hbox_selected_barrier.setVisible(true);
                hbox_selected_glyph.setVisible(true);
                anchorPane_selected.setVisible(true);
            }
        });

        tableColumn_device_device.setCellValueFactory(param -> param.getValue().nameProperty());
        tableColumn_device_rating.setCellValueFactory(param -> param.getValue().ratingProperty().asObject());
        tableColumn_device_attack.setCellValueFactory(param -> param.getValue().attackProperty().asObject());
        tableColumn_device_sleeze.setCellValueFactory(param -> param.getValue().sleezeProperty().asObject());
        tableColumn_device_firewall.setCellValueFactory(param -> param.getValue().firewallProperty().asObject());
        tableColumn_device_dataProcessing.setCellValueFactory(param -> param.getValue().dataProcessingProperty().asObject());
        tableColumn_device_condition.setCellValueFactory(param -> param.getValue().conditionProperty().asObject());

        tableView_devices.setRowFactory(param -> {
            TableRow<Device> tableRow = new TableRow<>();

            MenuItem renameDevice = new MenuItem("Rename device");
            renameDevice.setOnAction(event -> {
                Device selected = tableView_devices.getSelectionModel().getSelectedItem();
                TextInputDialog dialog = new TextInputDialog(selected.getName());
                dialog.setTitle("Rename device");
                dialog.setHeaderText("Rename device " + selected.getName());
                dialog.setContentText("Please enter new name:");
                Optional<String> result = dialog.showAndWait();

                result.ifPresent(selected::setName);
            });
            MenuItem deleteDevice = new MenuItem("Delete device");
            deleteDevice.setOnAction(event -> tableView_devices.getItems()
                    .remove(tableView_devices.getSelectionModel().getSelectedIndex()));
            MenuItem addDevice = new MenuItem("Add device");
            addDevice.setOnAction(event -> addDeviceOnAction());
            ContextMenu fullContextMenu = new ContextMenu(addDevice, deleteDevice, renameDevice);
            ContextMenu emptyContextMenu = new ContextMenu(addDevice);

            tableRow.emptyProperty().addListener((observable, oldValue, newValue) ->
                    tableRow.setContextMenu(newValue ? emptyContextMenu : fullContextMenu));
            return tableRow;
        });

        tableView_devices.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                cleanSelectedPane();
            }

            if (newValue == null) {
                cleanSelectedPane();
            } else {
                tableView_masterTable.getSelectionModel().clearSelection();
                tableView_barrier.getSelectionModel().clearSelection();

                fontAwesomeIcon_selected.setIcon(FontAwesomeIcon.LAPTOP);

                textField_selected_condition.textProperty()
                        .bindBidirectional(newValue.conditionProperty(), new NumberStringConverter());
                label_selectedCharacter.textProperty().bind(newValue.nameProperty());

                hbox_selected_glyph.setVisible(true);
                hbox_selected_device.setVisible(true);
                anchorPane_selected.setVisible(true);
            }
        });

        textField_selected_physical.textProperty()
                .addListener(new NumericLimitListener(textField_selected_physical, -100, 100));
        textField_selected_stun.textProperty()
                .addListener(new NumericLimitListener(textField_selected_stun, -100, 100));
        textField_selected_structure.textProperty()
                .addListener(new NumericLimitListener(textField_selected_structure, 0, 100));
        textField_selected_armor.textProperty()
                .addListener(new NumericLimitListener(textField_selected_armor, 0, 100));

        hbox_selected_barrier.managedProperty().bind(hbox_selected_barrier.visibleProperty());
        hbox_selected_character.managedProperty().bind(hbox_selected_character.visibleProperty());
        hbox_selected_glyph.managedProperty().bind(hbox_selected_glyph.visibleProperty());
        anchorPane_selected.managedProperty().bind(anchorPane_selected.visibleProperty());

        cleanSelectedPane();

        label_overwatchScore.textProperty().addListener((observable, oldValue, newValue) -> {
            if (StringUtils.isNotEmpty(newValue)) {
                Integer value = Integer.parseInt(newValue);
                if (value >= 40) {
                    label_overwatchScore.setTextFill(Color.RED);
                } else if (value >= 30) {
                    label_overwatchScore.setTextFill(Color.DARKORANGE);
                } else if (value >= 20) {
                    label_overwatchScore.setTextFill(Color.ORANGE);
                } else {
                    label_overwatchScore.setTextFill(Color.BLACK);
                }
            }
        });

        appLogic.showRealWorldProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                vbox_realWorld.setVisible(newValue);
            }
        });
        appLogic.showAstralPlaneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                vbox_astralPlane.setVisible(newValue);
            }
        });
        appLogic.showMatrixProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                vbox_matrix.setVisible(newValue);
            }
        });

        comboBox_weather.setItems(FXCollections.observableArrayList(Weather.values()));
        comboBox_weather.setCellFactory(param -> new WeatherCell());
        comboBox_weather.setButtonCell(new WeatherCell());

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
        List<Path> recentCampaigns = appLogic.getConfig().getRecentFiles();
        if (recentCampaigns.isEmpty()) {
            menu_recentCampaigns.disableProperty().setValue(true);
        }
        for (int i = 0; i < 3; i++) {
            MenuItem recentCampaignMenuItem = menuItems.get(i);
            if (recentCampaigns.size() > i) {
                Path recentCampaignPath = recentCampaigns.get(i);
                recentCampaignMenuItem.setUserData(recentCampaignPath);
                recentCampaignMenuItem.textProperty().setValue(recentCampaignPath.getFileName().toString());
                recentCampaignMenuItem.disableProperty().setValue(false);
                recentCampaignMenuItem.visibleProperty().setValue(true);
            } else {
                recentCampaignMenuItem.disableProperty().setValue(true);
                recentCampaignMenuItem.visibleProperty().setValue(false);
                recentCampaignMenuItem.textProperty().setValue(StringUtils.EMPTY);
            }
        }
    }

    private void setNewInitiative() {
        for (Character character : battleLogic.getActiveBattle().getCharacters()) {
            TextInputDialog dialog = InitiativeDialogFactory.createDialog(character);
            Optional<String> result = dialog.showAndWait();
            try {
                character.setInitiative(result.map(Integer::parseInt).orElse(0));
            } catch (NumberFormatException ex) {
                character.setInitiative(0);
            }

        }
    }

    private void cleanSelectedPane() {
        textField_selected_physical.textProperty().unbind();
        textField_selected_stun.textProperty().unbind();
        textField_selected_armor.textProperty().unbind();
        textField_selected_structure.textProperty().unbind();
        textField_selected_condition.textProperty().unbind();
        label_selectedCharacter.textProperty().unbind();

        hbox_selected_glyph.setVisible(false);
        hbox_selected_character.setVisible(false);
        hbox_selected_barrier.setVisible(false);
        hbox_selected_device.setVisible(false);
        anchorPane_selected.setVisible(false);
    }
}