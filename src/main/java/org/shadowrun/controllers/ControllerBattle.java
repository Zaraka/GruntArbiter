package org.shadowrun.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
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
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.converter.NumberStringConverter;
import org.apache.commons.lang3.StringUtils;
import org.shadowrun.common.NumericLimitListener;
import org.shadowrun.common.nodes.cells.*;
import org.shadowrun.common.constants.ICE;
import org.shadowrun.common.constants.Weather;
import org.shadowrun.common.constants.World;
import org.shadowrun.common.converters.IterationTimeConverter;
import org.shadowrun.common.converters.SpiritIndexReputationConverter;
import org.shadowrun.common.exceptions.NextTurnException;
import org.shadowrun.common.factories.CharacterIconFactory;
import org.shadowrun.common.factories.InitiativeDialogFactory;
import org.shadowrun.logic.AppLogic;
import org.shadowrun.logic.BattleLogic;
import org.shadowrun.models.*;
import org.shadowrun.models.Character;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ControllerBattle {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerBattle.class);

    private static final String LABEL_DISCONNECT = "Disconnect";
    private static final String LABEL_GENERATE_HOST = "Generate Host";

    private AppLogic appLogic;
    private BattleLogic battleLogic;
    private Battle battle;

    private BooleanBinding allPlayersIncluded;

    @FXML
    private TableView<Character> tableView_masterTable;
    @FXML
    private TableColumn<Character, Character> tableColumn_masterTable_character;
    @FXML
    private TableColumn<Character, Character> tableColumn_masterTable_condition;
    @FXML
    private TableColumn<Character, Character> tableColumn_masterTable_woundModifier;
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
    private VBox vbox_realWorld;
    @FXML
    private VBox vbox_matrix;
    @FXML
    private VBox vbox_astralPlane;
    @FXML
    private VBox vbox_matrixProperties;
    @FXML
    private VBox vbox_selected_player;


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
    private Label label_selected_astralReputation;
    @FXML
    private Label label_selected_physical;
    @FXML
    private Label label_selected_stun;
    @FXML
    private Label label_selected_structure;
    @FXML
    private Label label_selected_armor;
    @FXML
    private Label label_selected_condition;

    @FXML
    private Button button_nextTurn;
    @FXML
    private Button button_prevTurn;
    @FXML
    private Button button_hostAction;
    @FXML
    private Button button_spawnPlayer;

    @FXML
    private TextField textField_selected_initiative;
    @FXML
    private TextField textField_selected_spiritIndex;
    @FXML
    private TextField textField_backgroundCount;

    @FXML
    private ComboBox<Weather> comboBox_weather;

    @FXML
    private AnchorPane anchorPane_selected;
    @FXML
    private AnchorPane anchorPane_barriers;
    @FXML
    private AnchorPane anchorPane_devices;
    @FXML
    private AnchorPane anchorPane_bottomTables;

    @FXML
    private SplitPane splitPane_horizontal;

    @FXML
    private FontAwesomeIconView fontAwesomeIcon_selected;

    //------------------FXML methods-------------------

    @FXML
    private void addICeOnAction() {
        if (battle.getICe().size() >= battle.getHost().getRating()) {
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
        });
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
    private void hostActionOnAction() {

        if (battleLogic.hasHost()) {
            battleLogic.disconectFromHost();
        } else {
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
                LOG.error("Could not load addHost dialog: ", ex);
            }
        }
    }

    @FXML
    private void physicalPlusOnAction() {
        Character character = tableView_masterTable.getSelectionModel().getSelectedItem();
        character.getPhysicalMonitor().increase(1);
        tableView_masterTable.refresh();
    }

    @FXML
    private void physicalMinusOnAction() {
        Character character = tableView_masterTable.getSelectionModel().getSelectedItem();
        character.getPhysicalMonitor().decrease(1);
        tableView_masterTable.refresh();
    }

    @FXML
    private void physicalMonitorSettingsOnAction() {
        Character character = tableView_masterTable.getSelectionModel().getSelectedItem();
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/monitorSettings.fxml"));
            root = loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Physical monitor settings");
            dialog.setScene(new Scene(root));
            ControllerMonitorSettings controllerMonitorSettings = loader.getController();
            controllerMonitorSettings.onOpen(dialog, character.getPhysicalMonitor(), "Physical monitor");
            dialog.showAndWait();
            controllerMonitorSettings.getMonitor().ifPresent(monitor -> {
                character.getPhysicalMonitor().maxProperty().setValue(monitor.getMax());
                character.getPhysicalMonitor().currentProperty().setValue(monitor.getCurrent());
            });

        } catch (IOException ex) {
            LOG.error("Could not load momnitorSettings dialog: ", ex);
        }
    }

    @FXML
    private void stunPlusOnAction() {
        Character character = tableView_masterTable.getSelectionModel().getSelectedItem();
        character.getStunMonitor().increase(1);
        tableView_masterTable.refresh();
    }

    @FXML
    private void stunMinusOnAction() {
        Character character = tableView_masterTable.getSelectionModel().getSelectedItem();
        character.getStunMonitor().decrease(1);
        tableView_masterTable.refresh();
    }

    @FXML
    private void stunMonitorSettingsOnAction() {
        Character character = tableView_masterTable.getSelectionModel().getSelectedItem();
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/monitorSettings.fxml"));
            root = loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Stun monitor settings");
            dialog.setScene(new Scene(root));
            ControllerMonitorSettings controllerMonitorSettings = loader.getController();
            controllerMonitorSettings.onOpen(dialog, character.getStunMonitor(), "Stun monitor");
            dialog.showAndWait();
            controllerMonitorSettings.getMonitor().ifPresent(monitor -> {
                character.getStunMonitor().maxProperty().setValue(monitor.getMax());
                character.getStunMonitor().currentProperty().setValue(monitor.getCurrent());
            });

        } catch (IOException ex) {
            LOG.error("Could not load momnitorSettings dialog: ", ex);
        }
    }

    @FXML
    private void initiativePlusOnAction() {
        Character character = tableView_masterTable.getSelectionModel().getSelectedItem();
        character.initiativeProperty().setValue(character.getInitiative() + 1);
    }

    @FXML
    private void initiativeMinusOnAction() {
        Character character = tableView_masterTable.getSelectionModel().getSelectedItem();
        if (character.getInitiative() > 0) {
            character.initiativeProperty().setValue(character.getInitiative() - 1);
        }
    }

    @FXML
    private void structurePlusOnAction() {
        Barrier barrier = tableView_barrier.getSelectionModel().getSelectedItem();
        barrier.getStructureMonitor().increase(1);
    }

    @FXML
    private void structureMinusOnAction() {
        Barrier barrier = tableView_barrier.getSelectionModel().getSelectedItem();
        barrier.getStructureMonitor().decrease(1);
    }

    @FXML
    private void structureMonitorSettingsOnAction() {
        Barrier barrier = tableView_barrier.getSelectionModel().getSelectedItem();
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/monitorSettings.fxml"));
            root = loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Structure monitor settings");
            dialog.setScene(new Scene(root));
            ControllerMonitorSettings controllerMonitorSettings = loader.getController();
            controllerMonitorSettings.onOpen(dialog, barrier.getStructureMonitor(), "Structure monitor");
            dialog.showAndWait();
            controllerMonitorSettings.getMonitor().ifPresent(monitor -> {
                barrier.getStructureMonitor().maxProperty().setValue(monitor.getMax());
                barrier.getStructureMonitor().currentProperty().setValue(monitor.getCurrent());
            });

        } catch (IOException ex) {
            LOG.error("Could not load momnitorSettings dialog: ", ex);
        }
    }


    @FXML
    private void armorPlusOnAction() {
        Barrier barrier = tableView_barrier.getSelectionModel().getSelectedItem();
        barrier.armorProperty().setValue(barrier.getArmor() + 1);
    }

    @FXML
    private void armorMinusOnAction() {
        Barrier barrier = tableView_barrier.getSelectionModel().getSelectedItem();
        if (barrier.getArmor() > 0) {
            barrier.armorProperty().setValue(barrier.getArmor() - 1);
        }
    }

    @FXML
    private void conditionPlusOnAction() {
        Device device = tableView_devices.getSelectionModel().getSelectedItem();
        device.getConditionMonitor().increase(1);
    }

    @FXML
    private void conditionMinusOnAction() {
        Device device = tableView_devices.getSelectionModel().getSelectedItem();
        device.getConditionMonitor().decrease(1);
    }

    @FXML
    private void conditionMonitorSettingsOnAction() {
        Device device = tableView_devices.getSelectionModel().getSelectedItem();
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/monitorSettings.fxml"));
            root = loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Condition monitor settings");
            dialog.setScene(new Scene(root));
            ControllerMonitorSettings controllerMonitorSettings = loader.getController();
            controllerMonitorSettings.onOpen(dialog, device.getConditionMonitor(), "Condition monitor");
            dialog.showAndWait();
            controllerMonitorSettings.getMonitor().ifPresent(monitor -> {
                device.getConditionMonitor().maxProperty().setValue(monitor.getMax());
                device.getConditionMonitor().currentProperty().setValue(monitor.getCurrent());
            });

        } catch (IOException ex) {
            LOG.error("Could not load momnitorSettings dialog: ", ex);
        }
    }


    @FXML
    private void spiritIndexPlusOnAction() {
        Character character = tableView_masterTable.getSelectionModel().getSelectedItem();
        character.getPlayer().setSpiritIndex(character.getPlayer().getSpiritIndex() + 1);
    }

    @FXML
    private void spiritIndexMinusOnAction() {
        PlayerCharacter player = tableView_masterTable.getSelectionModel().getSelectedItem().getPlayer();
        if (player.getSpiritIndex() > 0) {
            player.setSpiritIndex(player.getSpiritIndex() - 1);
        }
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
                    .ifPresent(barrier -> battle.getBarriers().add(barrier));

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
            controllerAddDevice.getDevice().ifPresent(device -> battle.getDevices().add(device));

        } catch (IOException ex) {
            LOG.error("Could not load addDevice dialog: ", ex);
        }
    }

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
            controllerAddCharacter.onOpen(dialog, appLogic.getActiveCampaign());
            dialog.showAndWait();
            controllerAddCharacter.getCharacter().ifPresent(playerCharacter -> {
                battle.getCharacters().add(playerCharacter);
            });

        } catch (IOException ex) {
            LOG.error("Could not load addCharacter dialog: ", ex);
        }
    }

    @FXML
    private void addPlayerOnAction() {
        Set<PlayerCharacter> playersInThisBattle = battle.getCharacters().stream()
                .filter(character -> character.getPlayer() != null)
                .map(Character::getPlayer).collect(Collectors.toSet());

        List<PlayerCharacter> availablePlayers =
                appLogic.getActiveCampaign().getPlayers().stream()
                        .filter(playerCharacter -> !playersInThisBattle.contains(playerCharacter))
                        .collect(Collectors.toList());

        if(!availablePlayers.isEmpty()) {
            ChoiceDialog<PlayerCharacter> dialog = new ChoiceDialog<>(availablePlayers.get(0), availablePlayers);
            GridPane gridPane = (GridPane) dialog.getDialogPane().getContent();
            ComboBox<PlayerCharacter> playerCharacterComboBox;
            try {
                playerCharacterComboBox = (ComboBox<PlayerCharacter>) gridPane.getChildren().stream()
                                .filter(node -> node.getClass() == ComboBox.class)
                                .collect(Collectors.toList()).get(0);
                playerCharacterComboBox.setCellFactory(param -> new PlayerCell());
                playerCharacterComboBox.setButtonCell(new PlayerCell());
                allPlayersIncluded.invalidate();
            } catch (ClassCastException ex) {
                LOG.error("CastException: ", ex);
            }


            dialog.setTitle("Add player");
            dialog.setHeaderText("Add player");
            dialog.setContentText("Choose player:");

            Optional<PlayerCharacter> result = dialog.showAndWait();

            result.ifPresent(playerCharacter -> battle.insertPlayer(playerCharacter));
        }
    }

    private void setNewInitiative() {
        for (Character character : battle.getCharacters()) {
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
        label_selectedCharacter.textProperty().unbind();

        hbox_selected_glyph.setVisible(false);
        hbox_selected_character.setVisible(false);
        hbox_selected_barrier.setVisible(false);
        hbox_selected_device.setVisible(false);
        vbox_selected_player.setVisible(false);
        anchorPane_selected.setVisible(false);
    }

    public void setStageAndListeners(Battle battle, AppLogic appLogic, BattleLogic battleLogic, boolean loaded) {
        this.battle = battle;
        this.appLogic = appLogic;
        this.battleLogic = battleLogic;

        button_nextTurn.disableProperty().bind(battleLogic.hasBattle());
        button_prevTurn.disableProperty().bind(battleLogic.hasBattle());

        tableColumn_masterTable_character.setCellFactory(param -> new CharacterCell());
        tableColumn_masterTable_character.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumn_masterTable_condition.setCellFactory(param -> new CharacterConditionCell());
        tableColumn_masterTable_condition.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumn_masterTable_woundModifier.setCellFactory(param -> new CharacterWoundModifierCell());
        tableColumn_masterTable_woundModifier.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
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

        tableView_masterTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
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
                    battle.updateCurrentCharacter();
                    tableView_masterTable.refresh();
                });
            });
            MenuItem addCharacter = new MenuItem("Add character");
            addCharacter.setOnAction(event -> addCharacterOnAction());
            MenuItem removeCharacter = new MenuItem("Remove character");
            removeCharacter.setOnAction(event -> {
                battle.getCharacters()
                        .remove(tableView_masterTable.getSelectionModel().getSelectedItem());
                allPlayersIncluded.invalidate();
            });
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
                    //tableView_masterTable.sort();
                    //tableView_masterTable.getSortOrder().clear();
                    //tableView_masterTable.getSortOrder().add(tableColumn_masterTable_initiative);
                }
            };
            tableRow.emptyProperty().addListener((observable, oldValue, newValue) ->
                    tableRow.setContextMenu(newValue ? emptyContextMenu : fullContextMenu));
            return tableRow;
        });
        tableView_masterTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                label_selected_physical.textProperty().unbind();
                label_selected_stun.textProperty().unbind();
                textField_selected_initiative.textProperty().unbindBidirectional(oldValue.initiativeProperty());
                if (oldValue.playerProperty().isNotNull().get()) {
                    textField_selected_spiritIndex.textProperty()
                            .unbindBidirectional(oldValue.getPlayer().spiritIndexProperty());
                    label_selected_astralReputation.textProperty()
                            .unbindBidirectional(oldValue.getPlayer().spiritIndexProperty());
                }
            }

            cleanSelectedPane();

            if (newValue != null) {
                tableView_barrier.getSelectionModel().clearSelection();
                tableView_devices.getSelectionModel().clearSelection();

                CharacterIconFactory.createIcon(newValue).ifPresent(fontAwesomeIcon -> {
                    fontAwesomeIcon_selected.setIcon(fontAwesomeIcon);
                    hbox_selected_glyph.setVisible(true);
                });

                label_selected_physical.textProperty().bind(Bindings.concat(
                        newValue.getPhysicalMonitor().currentProperty(),
                        "/",
                        newValue.getPhysicalMonitor().maxProperty()
                ));

                label_selected_stun.textProperty().bind(Bindings.concat(
                        newValue.getStunMonitor().currentProperty(),
                        "/",
                        newValue.getStunMonitor().maxProperty()
                ));
                textField_selected_initiative.textProperty()
                        .bindBidirectional(newValue.initiativeProperty(), new NumberStringConverter());
                label_selectedCharacter.textProperty().bind(newValue.nameProperty());
                if (newValue.playerProperty().isNotNull().get()) {
                    textField_selected_spiritIndex.textProperty()
                            .bindBidirectional(newValue.getPlayer().spiritIndexProperty(), new NumberStringConverter());
                    label_selected_astralReputation.textProperty()
                            .bindBidirectional(newValue.getPlayer().spiritIndexProperty(), new SpiritIndexReputationConverter());
                }

                hbox_selected_character.setVisible(true);
                vbox_selected_player.setVisible(true);
                anchorPane_selected.setVisible(true);
            }
        });

        tableColumn_barrier_object.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumn_barrier_object.setCellFactory(param -> new ObjectCell());
        tableColumn_barrier_armor.setCellValueFactory(param -> param.getValue().armorProperty().asObject());
        tableColumn_barrier_structure.setCellValueFactory(param -> param.getValue()
                .getStructureMonitor().currentProperty().asObject());

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
                label_selected_armor.textProperty().unbind();
                label_selected_structure.textProperty().unbind();
            }

            cleanSelectedPane();

            if (newValue != null) {
                tableView_masterTable.getSelectionModel().clearSelection();
                tableView_devices.getSelectionModel().clearSelection();

                fontAwesomeIcon_selected.setIcon(FontAwesomeIcon.SQUARE);

                label_selected_armor.textProperty().bind(newValue.armorProperty().asString());

                label_selected_structure.textProperty().bind(Bindings.concat(
                        newValue.getStructureMonitor().currentProperty(),
                        "/",
                        newValue.getStructureMonitor().maxProperty()
                ));
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
        tableColumn_device_dataProcessing.setCellValueFactory(
                param -> param.getValue().dataProcessingProperty().asObject());
        tableColumn_device_condition.setCellValueFactory(
                param -> param.getValue().getConditionMonitor().currentProperty().asObject());

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
                label_selected_condition.textProperty().unbind();
            }

            cleanSelectedPane();

            if (newValue != null) {
                tableView_masterTable.getSelectionModel().clearSelection();
                tableView_barrier.getSelectionModel().clearSelection();

                fontAwesomeIcon_selected.setIcon(FontAwesomeIcon.LAPTOP);

                label_selected_condition.textProperty().bind(Bindings.concat(
                        newValue.getConditionMonitor().currentProperty(),
                        "/",
                        newValue.getConditionMonitor().maxProperty()
                ));
                label_selectedCharacter.textProperty().bind(newValue.nameProperty());

                hbox_selected_glyph.setVisible(true);
                hbox_selected_device.setVisible(true);
                anchorPane_selected.setVisible(true);
            }
        });

        textField_selected_initiative.textProperty()
                .addListener(new NumericLimitListener(textField_selected_initiative, -100, 100));

        hbox_selected_barrier.managedProperty().bind(hbox_selected_barrier.visibleProperty());
        hbox_selected_character.managedProperty().bind(hbox_selected_character.visibleProperty());
        vbox_selected_player.managedProperty().bind(vbox_selected_player.visibleProperty());
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

        anchorPane_barriers.managedProperty().bind(anchorPane_barriers.visibleProperty());
        anchorPane_devices.managedProperty().bind(anchorPane_devices.visibleProperty());
        anchorPane_bottomTables.managedProperty().bind(anchorPane_bottomTables.visibleProperty());

        //Items
        ObservableList<Character> firableCharacters = FXCollections.
                observableArrayList(param -> new Observable[]{param.initiativeProperty()});
        Bindings.bindContentBidirectional(firableCharacters, battle.getCharacters());
        SortedList<Character> sortedCharacters = new SortedList<>(firableCharacters,
                Comparator.comparingInt(Character::getInitiative).reversed()).sorted();
        sortedCharacters.comparatorProperty().bind(tableView_masterTable.comparatorProperty());
        tableView_masterTable.setItems(sortedCharacters);
        //tableView_masterTable.sort();
        tableView_barrier.setItems(battle.getBarriers());
        tableView_devices.setItems(battle.getDevices());

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
        comboBox_weather.valueProperty().bindBidirectional(battle.selectedWeatherProperty());


        battle.currentCharacterProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tableView_masterTable.getSelectionModel().select(newValue);
            }
        });

        button_prevTurn.disableProperty()
                .bind(battle.actionPhaseProperty().greaterThan(1).not()
                        .or(battle.initiativePassProperty().greaterThan(1).not()
                                .or(battle.combatTurnProperty().greaterThan(1)).not()));

        battle.maxInitiativeBinding().greaterThan(0).addListener((observable, oldValue, newValue) ->
                tableColumn_masterTable_turn1.setVisible(newValue));
        battle.maxInitiativeBinding().greaterThan(10).addListener((observable, oldValue, newValue) ->
                tableColumn_masterTable_turn2.setVisible(newValue));
        battle.maxInitiativeBinding().greaterThan(20).addListener((observable, oldValue, newValue) ->
                tableColumn_masterTable_turn3.setVisible(newValue));
        battle.maxInitiativeBinding().greaterThan(30).addListener((observable, oldValue, newValue) ->
                tableColumn_masterTable_turn4.setVisible(newValue));
        battle.maxInitiativeBinding().greaterThan(40).addListener((observable, oldValue, newValue) ->
                tableColumn_masterTable_turn5.setVisible(newValue));
        battle.maxInitiativeBinding().greaterThan(50).addListener((observable, oldValue, newValue) ->
                tableColumn_masterTable_turn6.setVisible(newValue));
        battle.maxInitiativeBinding().greaterThan(60).addListener((observable, oldValue, newValue) ->
                tableColumn_masterTable_turn7.setVisible(newValue));

        anchorPane_barriers.visibleProperty().bind(Bindings.isEmpty(tableView_barrier.getItems()).not());
        anchorPane_devices.visibleProperty().bind(Bindings.isEmpty(tableView_devices.getItems()).not());
        anchorPane_bottomTables.visibleProperty().bind(anchorPane_barriers.visibleProperty()
                .or(anchorPane_devices.visibleProperty()));


        battleLogic.activeBattleProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == battle) {
                battleLogic.hasHostProperty().addListener((host, oldHost, newHost) -> {
                    if (newHost) {
                        button_hostAction.textProperty().setValue(LABEL_DISCONNECT);
                    } else {
                        button_hostAction.textProperty().setValue(LABEL_GENERATE_HOST);
                    }
                });
            }
        });

        allPlayersIncluded = Bindings.createBooleanBinding(() ->
                appLogic.getActiveCampaign().getPlayers().size() <=
                        battle.getCharacters().stream().filter(character -> character.getPlayer() != null).count());

        button_spawnPlayer.disableProperty().bind(allPlayersIncluded);

        if(!loaded) {
            setNewInitiative();
        }
    }

    public void remove() {
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
        anchorPane_devices.visibleProperty().unbind();
        anchorPane_barriers.visibleProperty().unbind();
        anchorPane_bottomTables.visibleProperty().unbind();

        tableColumn_masterTable_turn1.visibleProperty().unbind();
        tableColumn_masterTable_turn2.visibleProperty().unbind();
        tableColumn_masterTable_turn3.visibleProperty().unbind();
        tableColumn_masterTable_turn4.visibleProperty().unbind();
        tableColumn_masterTable_turn5.visibleProperty().unbind();
        tableColumn_masterTable_turn6.visibleProperty().unbind();
        tableColumn_masterTable_turn7.visibleProperty().unbind();
    }

    public Battle getBattle() {
        return battle;
    }
}
