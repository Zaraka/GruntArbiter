package org.shadowrun.controllers;

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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.converter.NumberStringConverter;
import org.apache.commons.lang3.StringUtils;
import org.shadowrun.common.IterationTimeConverter;
import org.shadowrun.common.cells.CharacterCell;
import org.shadowrun.common.cells.TurnTableCell;
import org.shadowrun.common.cells.WeatherCell;
import org.shadowrun.common.constants.ICE;
import org.shadowrun.common.constants.Weather;
import org.shadowrun.common.constants.World;
import org.shadowrun.common.exceptions.NextTurnException;
import org.shadowrun.common.factories.ExceptionDialogFactory;
import org.shadowrun.common.factories.InitiativeDialogFactory;
import org.shadowrun.logic.AppLogic;
import org.shadowrun.logic.BattleLogic;
import org.shadowrun.models.Character;
import org.shadowrun.models.PlayerCharacter;
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
    private Menu menu_campaign;
    @FXML
    private MenuItem menuItem_addPlayer;
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
    @FXML
    private Tab tab_battle;

    @FXML
    private VBox vbox_selected;
    @FXML
    private VBox vbox_realWorld;
    @FXML
    private VBox vbox_matrix;
    @FXML
    private VBox vbox_astralPlane;
    @FXML
    private VBox vbox_matrixProperties;


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
    private TextField textField_backgroundCount;

    @FXML
    private ComboBox<Weather> comboBox_weather;


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
        if(battleLogic.getActiveBattle().getICe().size() >= battleLogic.getActiveBattle().getHost().getRating()){
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
        TextInputDialog dialog = new TextInputDialog("3");
        dialog.setTitle("Generate host");
        dialog.setHeaderText("Attributes will be randomized");
        dialog.setContentText("Please enter host ratting:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(rating -> battleLogic.getActiveBattle().getHost().randomize(Integer.parseInt(rating.replaceAll("[^\\d.]", ""))));
    }

    @FXML
    private void physicalPlusOnAction() {
        Character character = tableView_masterTable.getSelectionModel().getSelectedItem();
        character.physicalMonitorProperty().set(character.getPhysicalMonitor() + 1);
    }

    @FXML
    private void physicalMinusOnAction() {
        Character character = tableView_masterTable.getSelectionModel().getSelectedItem();
        character.physicalMonitorProperty().set(character.getPhysicalMonitor() - 1);
    }

    @FXML
    private void stunPlusOnAction() {
        Character character = tableView_masterTable.getSelectionModel().getSelectedItem();
        character.stunMonitorProperty().set(character.getStunMonitor() + 1);
    }

    @FXML
    private void stunMinusOnAction() {
        Character character = tableView_masterTable.getSelectionModel().getSelectedItem();
        character.stunMonitorProperty().set(character.getStunMonitor() - 1);
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

    private void addCampaignHooks() {
        if (appLogic.getActiveCampaign() != null) {
            //Items
            tableView_playerCharacters.setItems(appLogic.getActiveCampaign().getPlayers());
            //tab selection
            tabPane.getSelectionModel().select(tab_characters);
        }
    }

    private void addBattleHooks() {
        //Items
        tableView_masterTable.setItems(battleLogic.getActiveBattle().getCharacters());

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

        if (battleLogic.getActiveBattle() != null) {
            //binds
            label_combatTurnCounter.textProperty().bind(battleLogic.getActiveBattle().combatTurnProperty().asString());
            label_intiativePassCounter.textProperty().bind(battleLogic.getActiveBattle().initiativePassProperty().asString());
            label_actionPhaseCounter.textProperty().bind(battleLogic.getActiveBattle().actionPhaseProperty().asString());
            label_currentCharacter.textProperty().bind(battleLogic.currentCharacterNameProperty());
            label_host_attack.textProperty().bind(battleLogic.getActiveBattle().getHost().attackProperty().asString());
            label_host_sleeze.textProperty().bind(battleLogic.getActiveBattle().getHost().sleezeProperty().asString());
            label_host_firewall.textProperty().bind(battleLogic.getActiveBattle().getHost().firewallProperty().asString());
            label_host_dataProcessing.textProperty().bind(battleLogic.getActiveBattle().getHost().dataProcessingProperty().asString());
            label_host_rating.textProperty().bind(battleLogic.getActiveBattle().getHost().ratingProperty().asString());
            textField_backgroundCount.textProperty().bindBidirectional(battleLogic.getActiveBattle().backgroundCountProperty(), new NumberStringConverter());
            label_overwatchScore.textProperty().bind(battleLogic.getActiveBattle().getHost().overwatchScoreProperty().asString());
            vbox_matrixProperties.visibleProperty().bind(battleLogic.getActiveBattle().getHost().isInitalized());
            Bindings.bindBidirectional(label_time.textProperty(), battleLogic.getActiveBattle().combatTurnProperty(), new IterationTimeConverter(battleLogic.getActiveBattle().getTime()));

            battleLogic.getActiveBattle().currentCharacterProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    tableView_masterTable.getSelectionModel().select(newValue);
                }
            });

            comboBox_weather.valueProperty().bindBidirectional(battleLogic.getActiveBattle().selectedWeatherProperty());

            button_prevTurn.disableProperty()
                    .bind(battleLogic.getActiveBattle().actionPhaseProperty().greaterThan(1)
                            .or(battleLogic.getActiveBattle().initiativePassProperty().greaterThan(1)
                                    .or(battleLogic.getActiveBattle().combatTurnProperty().greaterThan(1))));

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

        tab_battle.disableProperty().bind(battleLogic.hasBattle());
        button_nextTurn.disableProperty().bind(battleLogic.hasBattle());
        button_prevTurn.disableProperty().bind(battleLogic.hasBattle());

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
        tableColumn_masterTable_turn1.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().countTurn(1)));
        tableColumn_masterTable_turn2.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().countTurn(2)));
        tableColumn_masterTable_turn3.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().countTurn(3)));
        tableColumn_masterTable_turn4.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().countTurn(4)));
        tableColumn_masterTable_turn5.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().countTurn(5)));
        tableColumn_masterTable_turn6.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().countTurn(6)));
        tableColumn_masterTable_turn7.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().countTurn(7)));

        tableView_masterTable.setRowFactory(param -> new TableRow<Character>() {
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
        });
        tableView_masterTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                textField_selected_physical.textProperty().unbind();
                textField_selected_stun.textProperty().unbind();
                label_selectedCharacter.textProperty().unbind();
            }

            if (newValue == null) {
                vbox_selected.setVisible(false);
            } else {
                textField_selected_physical.textProperty().bind(newValue.physicalMonitorProperty().asString());
                textField_selected_stun.textProperty().bind(newValue.stunMonitorProperty().asString());
                label_selectedCharacter.textProperty().bind(newValue.nameProperty());
                vbox_selected.setVisible(true);
            }
        });

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
        tableView_masterTable.setContextMenu(new ContextMenu(
                moveToRealWorld,
                moveToMatrixSpace,
                moveToAstralPlane,
                separatorMenuItem,
                setInitiative,
                addCharacter,
                removeCharacter));

        textField_selected_physical.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("-?\\d*")) {
                textField_selected_physical.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        textField_selected_stun.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("-?\\d*")) {
                textField_selected_stun.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        vbox_selected.setVisible(false);

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
}