package org.shadowrun.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Pair;
import javafx.util.converter.NumberStringConverter;
import org.apache.commons.lang3.StringUtils;
import org.shadowrun.common.NumericLimitListener;
import org.shadowrun.common.constants.*;
import org.shadowrun.common.converters.IterationTimeConverter;
import org.shadowrun.common.converters.SpiritIndexReputationConverter;
import org.shadowrun.common.factories.BadgeFactory;
import org.shadowrun.common.factories.CharacterIconFactory;
import org.shadowrun.common.factories.DialogFactory;
import org.shadowrun.common.nodes.VehicleChaseCanvas;
import org.shadowrun.common.nodes.cells.*;
import org.shadowrun.common.nodes.rows.CompanionTableRow;
import org.shadowrun.common.utils.CSSUtils;
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
    private Campaign campaign;
    private GraphicsContext context;

    private BooleanBinding allPlayersIncluded;

    private static final DialogFactory dialogFactory = new DialogFactory();

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
    private TableView<Vehicle> tableView_vehicles;
    @FXML
    private TableColumn<Vehicle, String> tableColumn_vehicles_name;
    @FXML
    private TableColumn<Vehicle, String> tableColumn_vehicles_condition;
    @FXML
    private TableColumn<Vehicle, VehicleAttribute> tableColumn_vehicles_handling;
    @FXML
    private TableColumn<Vehicle, VehicleAttribute> tableColumn_vehicles_speed;
    @FXML
    private TableColumn<Vehicle, VehicleAttribute> tableColumn_vehicles_acceleration;
    @FXML
    private TableColumn<Vehicle, Integer> tableColumn_vehicles_body;
    @FXML
    private TableColumn<Vehicle, Integer> tableColumn_vehicles_armor;
    @FXML
    private TableColumn<Vehicle, Integer> tableColumn_vehicles_pilot;
    @FXML
    private TableColumn<Vehicle, Integer> tableColumn_vehicles_sensor;

    @FXML
    private TableView<Barrier> tableView_barrier;
    @FXML
    private TableColumn<Barrier, Barrier> tableColumn_barrier_object;
    @FXML
    private TableColumn<Barrier, String> tableColumn_barrier_type;
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
    private TableView<Companion> tableView_selected_companions;
    @FXML
    private TableColumn<Companion, String> tableColumn_selected_companions_name;
    @FXML
    private TableColumn<Companion, Companion> tableColumn_selected_companions_action;

    @FXML
    private TableView<Character> tableView_selected_passengers;
    @FXML
    private TableColumn<Character, String> tableColumn_selected_passengers_name;

    @FXML
    private VehicleChaseCanvas canvas_chaseScreen;

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
    private VBox vbox_selected_matrix;
    @FXML
    private VBox vbox_selected_companions;
    @FXML
    private VBox vbox_selected_vehicle_chase;
    @FXML
    private VBox vbox_vehicleChase;


    @FXML
    private HBox hbox_selected_character;
    @FXML
    private HBox hbox_selected_barrier;
    @FXML
    private HBox hbox_selected_glyph;
    @FXML
    private HBox hbox_selected_device;
    @FXML
    private HBox hbox_selected_vehicle;


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
    private Label label_selected_deviceCondition;
    @FXML
    private Label label_selected_vehicleCondition;

    @FXML
    private Button button_nextTurn;
    @FXML
    private Button button_prevTurn;
    @FXML
    private Button button_hostAction;
    @FXML
    private Button button_spawnPlayer;
    @FXML
    private Button button_vehicleCombat;
    @FXML
    private Button button_selected_matrix;
    @FXML
    private Button button_selected_addCompanion;
    @FXML
    private Button button_selected_forward;
    @FXML
    private Button button_selected_back;

    @FXML
    private TextField textField_selected_initiative;
    @FXML
    private TextField textField_selected_spiritIndex;
    @FXML
    private TextField textField_backgroundCount;
    @FXML
    private TextField textField_backgroundNoise;
    @FXML
    private TextField textField_terrainModifier;

    @FXML
    private ComboBox<Weather> comboBox_weather;
    @FXML
    private ComboBox<CompanionType> comboBox_selected_companionType;

    @FXML
    private AnchorPane anchorPane_selected;
    @FXML
    private AnchorPane anchorPane_barriers;
    @FXML
    private AnchorPane anchorPane_devices;
    @FXML
    private AnchorPane anchorPane_vehicles;
    @FXML
    private AnchorPane anchorPane_bottomTables;

    @FXML
    private StackPane anchorPane_chaseScreen;

    @FXML
    private SplitPane splitPane_horizontal;
    @FXML
    private SplitPane splitPane_centerContent;
    @FXML
    private SplitPane splitPane_bottomTables;

    @FXML
    private FontAwesomeIconView fontAwesomeIcon_selected;
    @FXML
    private FontAwesomeIconView fontAwesomeIcon_selected_matrix;
    @FXML
    private ImageView imageView_selected;

    @FXML
    private FlowPane flowPane_selected_badges;

    @FXML
    private TitledPane titledPane_selected_companions;
    @FXML
    private TitledPane titledPane_selcted_passengers;

    private List<TableView> contentTables;
    private List<TableColumn<Character, Integer>> turnTableColumns;

    //------------------FXML methods-------------------

    @FXML
    private void addICeOnAction() {
        if (battle.getICe().size() >= battle.getHost().getRating()) {
            Alert alert = dialogFactory.createConfirmationDialog(
                    "Too much ICe",
                    "ICe number exceeds host rating",
                    "Spawn another anyway?");
            Optional<ButtonType> result = alert.showAndWait();
            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return;
            }
        }

        Dialog<Pair<String, String>> dialog = dialogFactory.createICeDialog(battle.getHost());

        Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(res -> {
            ICE ice = ICE.fromName(res.getKey().replaceAll("\\s+", "").toUpperCase());
            battleLogic.spawnICe(ice, Integer.parseInt(res.getValue()));
            tableView_masterTable.refresh();
        });
    }

    @FXML
    public void nextTurnOnAction() {
        battleLogic.nextPhase();
        refreshMasterTable();
    }

    @FXML
    public void prevTurnOnAction() {
        battleLogic.prevPhase();
        refreshMasterTable();
    }

    @FXML
    private void hostActionOnAction() {

        if (battleLogic.hasHost()) {
            battleLogic.disconectFromHost();
        } else {
            try {
                AddHost addHost = dialogFactory.createHostDialog();

                addHost.getStage().showAndWait();
                addHost.getHost().ifPresent(host -> {
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
        try {
            MonitorSettings monitorSettings =
                    dialogFactory.createMonitorDialog(character.getPhysicalMonitor(), "Physical monitor");
            monitorSettings.getStage().showAndWait();
            monitorSettings.getMonitor().ifPresent(monitor -> {
                character.getPhysicalMonitor().setFrom(monitor);
            });
        } catch (IOException ex) {
            LOG.error("Could not load monitorSettings dialog: ", ex);
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
        try {
            MonitorSettings monitorSettings =
                    dialogFactory.createMonitorDialog(character.getStunMonitor(), "Stun monitor");
            monitorSettings.getStage().showAndWait();
            monitorSettings.getMonitor().ifPresent(monitor -> {
                character.getStunMonitor().setFrom(monitor);
            });
        } catch (IOException ex) {
            LOG.error("Could not load monitorSettings dialog: ", ex);
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
        try {
            MonitorSettings monitorSettings =
                    dialogFactory.createMonitorDialog(barrier.getStructureMonitor(), "Structure monitor");
            monitorSettings.getStage().showAndWait();
            monitorSettings.getMonitor().ifPresent(monitor -> {
                barrier.getStructureMonitor().setFrom(monitor);
            });
        } catch (IOException ex) {
            LOG.error("Could not load monitorSettings dialog: ", ex);
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

        try {
            MonitorSettings monitorSettings =
                    dialogFactory.createMonitorDialog(device.getConditionMonitor(), "Condition monitor");
            monitorSettings.getStage().showAndWait();
            monitorSettings.getMonitor().ifPresent(monitor -> {
                device.getConditionMonitor().setFrom(monitor);
            });
        } catch (IOException ex) {
            LOG.error("Could not load monitorSettings dialog: ", ex);
        }
    }

    @FXML
    private void vehicleConditionPlusOnAction() {
        Vehicle vehicle = tableView_vehicles.getSelectionModel().getSelectedItem();
        vehicle.getConditionMonitor().increase(1);
    }

    @FXML
    private void vehicleConditionMinusOnAction() {
        Vehicle vehicle = tableView_vehicles.getSelectionModel().getSelectedItem();
        vehicle.getConditionMonitor().decrease(1);
    }

    @FXML
    private void vehicleConditionMonitorSettingsOnAction() {
        Vehicle vehicle = tableView_vehicles.getSelectionModel().getSelectedItem();

        try {
            MonitorSettings monitorSettings =
                    dialogFactory.createMonitorDialog(vehicle.getConditionMonitor(), "Vehicle condition monitor");
            monitorSettings.getStage().showAndWait();
            monitorSettings.getMonitor().ifPresent(monitor -> {
                vehicle.getConditionMonitor().setFrom(monitor);
            });
        } catch (IOException ex) {
            LOG.error("Could not load monitorSettings dialog: ", ex);
        }
    }


    @FXML
    private void terrainModifierPlusOnAction() {
        battle.getVehicleChase().terrainModifierProperty().setValue(
                battle.getVehicleChase().getTerrainModifier() + 1
        );
    }

    @FXML
    private void terrainModifierMinusOnAction() {
        battle.getVehicleChase().terrainModifierProperty().setValue(
                battle.getVehicleChase().getTerrainModifier() - 1
        );
    }

    @FXML
    private void spiritIndexPlusOnAction() {
        Character character = tableView_masterTable.getSelectionModel().getSelectedItem();
        PlayerCharacter playerCharacter = appLogic.getPlayer(character.getPlayerUUID());
        playerCharacter.setSpiritIndex(playerCharacter.getSpiritIndex() + 1);
    }

    @FXML
    private void spiritIndexMinusOnAction() {
        PlayerCharacter playerCharacter = appLogic.getPlayer(
                tableView_masterTable.getSelectionModel().getSelectedItem().getPlayerUUID());
        if (playerCharacter.getSpiritIndex() > 0) {
            playerCharacter.setSpiritIndex(playerCharacter.getSpiritIndex() - 1);
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
    private void backgroundNoisePlusOnAction() {
        battleLogic.raiseBackgroundNoise();
    }

    @FXML
    private void backgroundNoiseMinusOnAction() {
        battleLogic.decreaseBackgroundNoise();
    }

    @FXML
    private void overwatchScorePlusOnAction() {
        Character character = tableView_masterTable.getSelectionModel().getSelectedItem();
        ObservableMap<String, IntegerProperty> connectedCharacters = battle.getHost().getConnectedCharacters();
        IntegerProperty overwatchProperty = connectedCharacters.get(character.getUuid());

        int currentValue = overwatchProperty.get();
        if (currentValue < 40)
            overwatchProperty.set(currentValue + 1);
    }

    @FXML
    private void overwatchScoreMinusOnAction() {
        Character character = tableView_masterTable.getSelectionModel().getSelectedItem();
        ObservableMap<String, IntegerProperty> connectedCharacters = battle.getHost().getConnectedCharacters();
        IntegerProperty overwatchProperty = connectedCharacters.get(character.getUuid());

        int currentValue = overwatchProperty.get();
        if (currentValue > 0)
            overwatchProperty.set(currentValue - 1);
    }

    @FXML
    private void overwatchScoreResetOnAction() {
        Character character = tableView_masterTable.getSelectionModel().getSelectedItem();
        ObservableMap<String, IntegerProperty> connectedCharacters = battle.getHost().getConnectedCharacters();
        IntegerProperty overwatchProperty = connectedCharacters.get(character.getUuid());

        overwatchProperty.set(0);
    }

    @FXML
    private void addBarrierOnAction() {
        try {
            AddBarrier addBarrier = dialogFactory.createBarrierDialog(appLogic);
            addBarrier.getStage().showAndWait();
            addBarrier.getBarrier()
                    .ifPresent(barrier -> {
                        battle.getBarriers().add(barrier);
                        appLogic.getConfig().setLatestBarrierName(barrier.getName());
                    });

        } catch (IOException ex) {
            LOG.error("Could not load addBarier dialog: ", ex);
        }
    }

    @FXML
    private void addDeviceOnAction() {
        try {
            AddDevice addDevice =
                    dialogFactory.createDeviceDialog(appLogic.getActiveCampaign(), null);
            addDevice.getStage().showAndWait();
            addDevice.getDevice().ifPresent(device -> battle.getDevices().add(device));

        } catch (IOException ex) {
            LOG.error("Could not load addDevice dialog: ", ex);
        }
    }

    @FXML
    private void addVehicleOnAction() {
        try {
            AddVehicle addVehicle =
                    dialogFactory.createVehicleDialog(appLogic.getActiveCampaign(), null);
            addVehicle.getStage().showAndWait();
            addVehicle.getVehicle().ifPresent(vehicle -> {
                LOG.info("add vehicle " + vehicle);
                battle.getVehicles().add(vehicle);
            });
        } catch (IOException ex) {
            LOG.error("Could not load addVehicle dialog: ", ex);
        }
    }

    @FXML
    private void addCharacterOnAction() {
        try {
            AddCharacter addCharacter =
                    dialogFactory.createCharacterDialog(appLogic.getActiveCampaign(),
                            CharacterType.CLASSIC,
                            null,
                            appLogic);
            addCharacter.getStage().showAndWait();
            addCharacter.getCharacter().ifPresent(character -> {
                battle.getCharacters().add(character);
                appLogic.getConfig().setLatestCharacterName(character.getName());
                appLogic.getConfig().setLatestCharacterInitiative(character.getInitiative());
            });

        } catch (IOException ex) {
            LOG.error("Could not load addCharacter dialog: ", ex);
        }
    }

    @FXML
    private void addPlayerOnAction() {
        Set<PlayerCharacter> playersInThisBattle = battle.getCharacters().stream()
                .filter(character -> character.playerUUIDProperty() != null)
                .map(character -> appLogic.getPlayer(character.getPlayerUUID())).collect(Collectors.toSet());

        List<PlayerCharacter> availablePlayers =
                appLogic.getActiveCampaign().getPlayers().stream()
                        .filter(playerCharacter -> !playersInThisBattle.contains(playerCharacter))
                        .collect(Collectors.toList());

        if (!availablePlayers.isEmpty()) {
            ChoiceDialog<PlayerCharacter> dialog = new ChoiceDialog<>(availablePlayers.get(0), availablePlayers);
            GridPane gridPane = (GridPane) dialog.getDialogPane().getContent();
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getStylesheets().add(
                    getClass().getClassLoader().getResource("css/dark.css").toExternalForm());
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

    @FXML
    private void matrixConnectOnAction() {
        Character character = tableView_masterTable.getSelectionModel().getSelectedItem();
        ObservableMap<String, IntegerProperty> connectedCharacters = battle.getHost().getConnectedCharacters();
        if (connectedCharacters.containsKey(character.getUuid())) {
            connectedCharacters.remove(character.getUuid());
            character.setWorld(World.REAL);
            fontAwesomeIcon_selected_matrix.setGlyphName(FontAwesomeIcon.SIGN_IN.name());
        } else {
            connectedCharacters.put(character.getUuid(), new SimpleIntegerProperty(0));
            character.setWorld(World.MATRIX);
            fontAwesomeIcon_selected_matrix.setGlyphName(FontAwesomeIcon.SIGN_OUT.name());
        }
        tableView_masterTable.getSelectionModel().clearSelection();
        tableView_masterTable.getSelectionModel().select(character);
        tableView_masterTable.refresh();
    }

    @FXML
    private void removeCharacterOnAction() {
        if (!tableView_masterTable.getSelectionModel().isEmpty()) {
            Character character = tableView_masterTable.getSelectionModel().getSelectedItem();
            battle.getCharacters().remove(character);
        } else if (!tableView_vehicles.getSelectionModel().isEmpty()) {
            Vehicle vehicle = tableView_vehicles.getSelectionModel().getSelectedItem();
            battle.getVehicles().remove(vehicle);
        } else if (!tableView_barrier.getSelectionModel().isEmpty()) {
            Barrier barrier = tableView_barrier.getSelectionModel().getSelectedItem();
            battle.getBarriers().remove(barrier);
        } else if (!tableView_devices.getSelectionModel().isEmpty()) {
            Device device = tableView_devices.getSelectionModel().getSelectedItem();
            battle.getDevices().remove(device);
        }
    }

    @FXML
    private void addSquadOnAction() {
        try {
            ManageSquads manageSquads = dialogFactory.createSquadDialog(
                    appLogic.getActiveCampaign(), appLogic);
            manageSquads.getStage().showAndWait();
            manageSquads.getSquad().ifPresent(squad -> squad.getCharacters().forEach(character -> {
                setCharacterInitiative(character);
                battle.getCharacters().add(character);
            }));

        } catch (IOException ex) {
            LOG.error("Could not load addCharacter dialog: ", ex);
        }
    }

    @FXML
    private void addCompanionOnAction() {
        Character selectedCharacter = tableView_masterTable.getSelectionModel().getSelectedItem();
        switch (comboBox_selected_companionType.getSelectionModel().getSelectedItem()) {
            case CHARACTER:
                try {
                    AddCharacter addCharacter =
                            dialogFactory.createCharacterDialog(
                                    appLogic.getActiveCampaign(),
                                    CharacterType.COMPANION,
                                    null,
                                    appLogic);
                    addCharacter.getStage().showAndWait();
                    addCharacter.getCharacter().ifPresent(companionCharacter -> {
                        selectedCharacter.getCompanions().add(new Companion(companionCharacter));
                    });

                } catch (IOException ex) {
                    LOG.error("Could not load addCharacter dialog: ", ex);
                }
                break;
            case VEHICLE:
                try {
                    AddVehicle addVehicle =
                            dialogFactory.createVehicleDialog(appLogic.getActiveCampaign(), null);
                    addVehicle.getStage().showAndWait();
                    addVehicle.getVehicle().ifPresent(vehicle -> {
                        selectedCharacter.getCompanions().add(new Companion(vehicle));
                    });
                } catch (IOException ex) {
                    LOG.error("Could not load addVehicle dialog: ", ex);
                }
                break;
            case DEVICE:
                try {
                    AddDevice addDevice =
                            dialogFactory.createDeviceDialog(appLogic.getActiveCampaign(), null);
                    addDevice.getStage().showAndWait();
                    addDevice.getDevice().ifPresent(device -> {
                        selectedCharacter.getCompanions().add(new Companion(device));
                    });

                } catch (IOException ex) {
                    LOG.error("Could not load addDevice dialog: ", ex);
                }
                break;
        }
    }

    @FXML
    public void engageVehicleCombat() {
        if (battle.getVehicleChase() == null) {
            try {
                VehicleCombat vehicleCombat = dialogFactory.createVehicleChaseDialog(battle);
                vehicleCombat.getStage().showAndWait();

                if (vehicleCombat.getValues().isPresent()) {
                    battle.vehicleChaseProperty().setValue(vehicleCombat.getValues().get());

                    for (Map.Entry<Character, String> playerVehicle : vehicleCombat.getCharacterVehicleMap().entrySet()) {
                        playerVehicle.getKey().vehicleProperty().set(playerVehicle.getValue());
                    }
                    initializeCanvas();
                }
            } catch (IOException ex) {
                LOG.error("Could not load addVehicleCombat dialog: ", ex);
            }
        } else {
            deinitializeCanvas();
            battle.vehicleChaseProperty().setValue(null);
        }

    }

    @FXML
    public void vehicleMoveForwardOnAction() {
        ObservableMap<String, Integer> positions = battle.getVehicleChase().getPositions();
        Vehicle vehicle = tableView_vehicles.selectionModelProperty().get().getSelectedItem();
        Integer position = positions.get(vehicle.getUuid());
        if (position != null) {
            positions.put(vehicle.getUuid(), position + 1);
        }
    }

    @FXML
    public void vehicleMoveBackwardOnAction() {
        ObservableMap<String, Integer> positions = battle.getVehicleChase().getPositions();
        Vehicle vehicle = tableView_vehicles.selectionModelProperty().get().getSelectedItem();
        Integer position = positions.get(vehicle.getUuid());
        if (position != null) {
            positions.put(vehicle.getUuid(), position - 1);
        }
    }

    @FXML
    public void vehicleChangeRoleOnAction() {
        ObservableMap<String, VehicleChaseRole> roles = battle.getVehicleChase().getChaseRoles();
        Vehicle vehicle = tableView_vehicles.selectionModelProperty().get().getSelectedItem();
        VehicleChaseRole role = roles.get(vehicle.getUuid());
        if (role != null) {
            roles.put(vehicle.getUuid(),
                    (role == VehicleChaseRole.PURSUER) ? VehicleChaseRole.RUNNER : VehicleChaseRole.PURSUER);
            cleanSelectedPane();
        }
    }

    public void setNewInitiative() {
        for (Character character : battle.getCharacters()) {
            setCharacterInitiative(character);
        }
    }

    private void setCharacterInitiative(Character character) {
        Dialog<DialogFactory.InitiativeDialogResult> dialog =
                dialogFactory.createInitiativeDialog(character, appLogic.getConfig().getApplyWound());
        dialog.showAndWait().ifPresent(result -> {
            try {
                character.setInitiative(result.getInitiative());
            } catch (NumberFormatException ex) {
                character.setInitiative(0);
            }
            appLogic.getConfig().setApplyWound(result.getApplyWound());
        });
    }

    private void cleanSelectedPane() {
        label_selectedCharacter.textProperty().unbind();

        vbox_selected_matrix.setVisible(false);
        vbox_selected_companions.setVisible(false);
        hbox_selected_glyph.setVisible(false);
        hbox_selected_character.setVisible(false);
        hbox_selected_barrier.setVisible(false);
        hbox_selected_device.setVisible(false);
        hbox_selected_vehicle.setVisible(false);
        vbox_selected_player.setVisible(false);
        anchorPane_selected.setVisible(false);
        fontAwesomeIcon_selected.setVisible(false);
        button_selected_matrix.setVisible(false);
        imageView_selected.setVisible(false);
        vbox_selected_vehicle_chase.setVisible(false);

        flowPane_selected_badges.getChildren().clear();
    }

    public void setStageAndListeners(Battle battle, AppLogic appLogic, BattleLogic battleLogic, boolean loaded) {
        this.battle = battle;
        this.appLogic = appLogic;
        this.battleLogic = battleLogic;

        contentTables = Arrays.asList(
                tableView_masterTable,
                tableView_vehicles,
                tableView_devices,
                tableView_barrier
        );

        turnTableColumns = Arrays.asList(
                tableColumn_masterTable_turn1,
                tableColumn_masterTable_turn2,
                tableColumn_masterTable_turn3,
                tableColumn_masterTable_turn4,
                tableColumn_masterTable_turn5,
                tableColumn_masterTable_turn6,
                tableColumn_masterTable_turn7
        );

        button_nextTurn.disableProperty().bind(battleLogic.hasBattle().not());
        button_prevTurn.disableProperty().bind(battleLogic.hasBattle().not());

        //Code for table is way too long
        setupMasterTable();
        setupBarrierTable();
        setupDeviceTable();
        setupVehicleTable();
        setupCompanionsTable();

        textField_selected_initiative.textProperty()
                .addListener(new NumericLimitListener(textField_selected_initiative, -100, 100));

        vbox_selected_matrix.managedProperty().bind(vbox_selected_matrix.visibleProperty());
        vbox_selected_companions.managedProperty().bind(vbox_selected_companions.visibleProperty());
        hbox_selected_barrier.managedProperty().bind(hbox_selected_barrier.visibleProperty());
        hbox_selected_character.managedProperty().bind(hbox_selected_character.visibleProperty());
        vbox_selected_player.managedProperty().bind(vbox_selected_player.visibleProperty());
        hbox_selected_glyph.managedProperty().bind(hbox_selected_glyph.visibleProperty());
        hbox_selected_vehicle.managedProperty().bind(hbox_selected_vehicle.visibleProperty());
        anchorPane_selected.managedProperty().bind(anchorPane_selected.visibleProperty());
        fontAwesomeIcon_selected.managedProperty().bind(fontAwesomeIcon_selected.visibleProperty());
        imageView_selected.managedProperty().bind(imageView_selected.visibleProperty());
        button_selected_matrix.managedProperty().bind(button_selected_matrix.visibleProperty());
        vbox_vehicleChase.managedProperty().bindBidirectional(vbox_vehicleChase.visibleProperty());
        vbox_selected_vehicle_chase.managedProperty().bindBidirectional(vbox_selected_vehicle_chase.visibleProperty());
        tableView_selected_passengers.managedProperty().bindBidirectional(tableView_selected_passengers.visibleProperty());
        titledPane_selcted_passengers.managedProperty().bindBidirectional(titledPane_selcted_passengers.visibleProperty());

        cleanSelectedPane();

        label_overwatchScore.textProperty().addListener((observable, oldValue, newValue) -> {
            if (StringUtils.isNotEmpty(newValue)) {
                Integer value = Integer.parseInt(newValue);
                ObservableList<String> overwatchClasses = label_overwatchScore.getStyleClass();
                if (value >= 40) {
                    overwatchClasses.clear();
                    overwatchClasses.add("overwatch-score-critical");
                } else if (value >= 30) {
                    overwatchClasses.clear();
                    overwatchClasses.add("overwatch-score-high");
                } else if (value >= 20) {
                    overwatchClasses.clear();
                    overwatchClasses.add("overwatch-score-medium");
                } else {
                    overwatchClasses.clear();
                    overwatchClasses.add("overwatch-score-clear");
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

        comboBox_selected_companionType.setItems(FXCollections.observableArrayList(CompanionType.values()));
        comboBox_selected_companionType.setCellFactory(param -> new CompanionTypeCell());
        comboBox_selected_companionType.setButtonCell(new CompanionTypeCell());
        button_selected_addCompanion.disableProperty().bind(comboBox_selected_companionType.valueProperty().isNull());

        //Items
        ObservableList<Character> firableCharacters = FXCollections.
                observableArrayList(param -> new Observable[]{param.initiativeProperty()});
        Bindings.bindContentBidirectional(firableCharacters, battle.getCharacters());
        SortedList<Character> sortedCharacters = new SortedList<>(firableCharacters,
                Comparator.comparingInt(Character::getInitiative).reversed()).sorted();
        sortedCharacters.comparatorProperty().bind(tableView_masterTable.comparatorProperty());
        tableView_masterTable.setItems(sortedCharacters);
        tableView_barrier.setItems(battle.getBarriers());
        tableView_devices.setItems(battle.getDevices());
        tableView_vehicles.setItems(battle.getVehicles());

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
        textField_backgroundCount.textProperty().bindBidirectional(battle.backgroundCountProperty(),
                new NumberStringConverter());
        textField_backgroundNoise.textProperty().bindBidirectional(battle.backgroundNoiseProperty(),
                new NumberStringConverter());
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

        button_hostAction.textProperty().bind(Bindings.createStringBinding(() -> {
            if (battle.getHost().getRating() > 0) {
                return LABEL_DISCONNECT;
            } else {
                return LABEL_GENERATE_HOST;
            }
        }, battle.getHost().ratingProperty()));

        allPlayersIncluded = Bindings.createBooleanBinding(() ->
                appLogic.getActiveCampaign().getPlayers().size() <=
                        battle.getCharacters().stream().filter(character -> character.getPlayerUUID() != null).count());

        button_spawnPlayer.disableProperty().bind(allPlayersIncluded);

        battle.getVehicles().addListener((InvalidationListener) observable -> {
            if (battle.getVehicles().isEmpty() && (Boolean) anchorPane_vehicles.getUserData()) {
                splitPane_centerContent.getItems().remove(anchorPane_vehicles);
                anchorPane_vehicles.setUserData(false);
            } else if (!battle.getVehicles().isEmpty() && !(Boolean) anchorPane_vehicles.getUserData()) {
                splitPane_centerContent.getItems().add(anchorPane_vehicles);
                anchorPane_vehicles.setUserData(true);
            }
        });

        InvalidationListener bottomTablesListener = observable -> {
            boolean bothEmpty = battle.getBarriers().isEmpty() && battle.getDevices().isEmpty();
            if (bothEmpty && (Boolean) anchorPane_bottomTables.getUserData()) {
                splitPane_centerContent.getItems().remove(anchorPane_bottomTables);
                anchorPane_bottomTables.setUserData(false);
            } else if (!bothEmpty && !(Boolean) anchorPane_bottomTables.getUserData()) {
                splitPane_centerContent.getItems().add(anchorPane_bottomTables);
                anchorPane_bottomTables.setUserData(true);
            }
        };

        battle.getBarriers().addListener(bottomTablesListener);
        battle.getDevices().addListener(bottomTablesListener);


        if (!loaded) {
            setNewInitiative();
        }

        boolean bottomTables = battle.getDevices().isEmpty() && battle.getBarriers().isEmpty();
        anchorPane_bottomTables.setUserData(!bottomTables);
        if (bottomTables) {
            splitPane_centerContent.getItems().remove(anchorPane_bottomTables);

        }
        boolean vehicleTable = battle.getVehicles().isEmpty();
        anchorPane_vehicles.setUserData(!vehicleTable);
        if (vehicleTable) {
            splitPane_centerContent.getItems().remove(anchorPane_vehicles);
        }

        splitPane_centerContent.getItems().remove(anchorPane_chaseScreen);

        canvas_chaseScreen.setBattle(battle);
        vbox_vehicleChase.setVisible(false);
        Bindings.isNull(battle.vehicleChaseProperty()).addListener((observable, oldValue, newValue) -> {
            vbox_vehicleChase.setVisible(!newValue);
            if (newValue) {
                button_vehicleCombat.textProperty().setValue("Engage vehicle combat");
                textField_terrainModifier.textProperty().unbind();
            } else {
                button_vehicleCombat.textProperty().setValue("Disengage vehicle combat");
                textField_terrainModifier.textProperty().bind(battle.getVehicleChase().terrainModifierProperty().asString());
            }
        });
    }

    private void setupVehicleTable() {
        tableColumn_vehicles_name.setCellValueFactory(param -> param.getValue().nameProperty());
        tableColumn_vehicles_condition.setCellValueFactory(param ->
                Bindings.concat(
                        param.getValue().getConditionMonitor().getCurrent(),
                        "/",
                        param.getValue().getConditionMonitor().getMax()));
        tableColumn_vehicles_handling.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().getHandling()));
        tableColumn_vehicles_handling.setCellFactory(param -> new VehicleAttributeCell());
        tableColumn_vehicles_speed.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().getSpeed()));
        tableColumn_vehicles_speed.setCellFactory(param -> new VehicleAttributeCell());
        tableColumn_vehicles_acceleration.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().getAcceleration()));
        tableColumn_vehicles_acceleration.setCellFactory(param -> new VehicleAttributeCell());
        tableColumn_vehicles_body.setCellValueFactory(param -> param.getValue().bodyProperty().asObject());
        tableColumn_vehicles_armor.setCellValueFactory(param -> param.getValue().armorProperty().asObject());
        tableColumn_vehicles_pilot.setCellValueFactory(param -> param.getValue().pilotProperty().asObject());
        tableColumn_vehicles_sensor.setCellValueFactory(param -> param.getValue().sensorProperty().asObject());
        tableColumn_selected_passengers_name.setCellValueFactory(param -> param.getValue().nameProperty());
        tableView_vehicles.setPlaceholder(new Label("No vehicles in combat"));
        tableView_selected_passengers.setPlaceholder(new Label("No passengers in this vehicle"));

        tableView_vehicles.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                label_selected_vehicleCondition.textProperty().unbind();
            }

            cleanSelectedPane();

            if (newValue != null) {
                clearTableSelection(tableView_vehicles);

                fontAwesomeIcon_selected.setIcon(FontAwesomeIcon.CAR);

                label_selected_vehicleCondition.textProperty().bind(Bindings.concat(
                        newValue.getConditionMonitor().currentProperty(),
                        "/",
                        newValue.getConditionMonitor().maxProperty()
                ));
                label_selectedCharacter.textProperty().bind(newValue.nameProperty());

                hbox_selected_glyph.setVisible(true);
                hbox_selected_vehicle.setVisible(true);
                anchorPane_selected.setVisible(true);
                fontAwesomeIcon_selected.setVisible(true);

                if (newValue.getType() == VehicleType.VEHICLE) {
                    flowPane_selected_badges.getChildren()
                            .add(BadgeFactory.createBadge(
                                    "Vehicle",
                                    "This vehicle is a vehicle!",
                                    CssClasses.INFO));
                } else {
                    flowPane_selected_badges.getChildren()
                            .add(BadgeFactory.createBadge(
                                    "Drone",
                                    "This vehicle is a drone.",
                                    CssClasses.INFO));
                }

                titledPane_selcted_passengers.setVisible(newValue.getType() == VehicleType.VEHICLE);

                if (battle.getVehicleChase() != null) {
                    if (battle.getVehicleChase().getChaseRoles().get(newValue.getUuid()) == VehicleChaseRole.PURSUER) {
                        flowPane_selected_badges.getChildren()
                                .add(BadgeFactory.createBadge(
                                        "Pursuer",
                                        "This vehicle is pursuer in ongoing vehicle chase",
                                        CssClasses.DANGER));
                    } else {
                        flowPane_selected_badges.getChildren()
                                .add(BadgeFactory.createBadge(
                                        "Runner",
                                        "This vehicle is escapee in ongoing vehicle chase",
                                        CssClasses.SUCCESS));
                    }

                    vbox_selected_vehicle_chase.setVisible(true);

                    tableView_selected_passengers.setItems(FXCollections.observableArrayList(
                            battle.getCharacters().stream()
                                    .filter(character -> character.getVehicle().equals(newValue.getUuid()))
                                    .collect(Collectors.toList())));
                }

            }

            canvas_chaseScreen.selectedVehicleProperty().setValue(newValue);

        });

        canvas_chaseScreen.selectedVehicleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != tableView_vehicles.selectionModelProperty().getValue().getSelectedItem()) {
                tableView_vehicles.selectionModelProperty().getValue().select(newValue);
            }
        });

        MenuItem renameVehicle = new MenuItem("Rename vehicle");
        renameVehicle.setOnAction(event -> {
            Vehicle selected = tableView_vehicles.getSelectionModel().getSelectedItem();
            TextInputDialog dialog = dialogFactory.createTextInputDialog(
                    "Rename vehicle",
                    "Rename vehicle " + selected.getName(),
                    "Please enter name:",
                    selected.getName());
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(selected::setName);
        });
        MenuItem deleteVehicle = new MenuItem("Delete vehicle");
        deleteVehicle.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.TRASH));
        deleteVehicle.setOnAction(event -> tableView_vehicles.getItems()
                .remove(tableView_vehicles.getSelectionModel().getSelectedIndex()));
        MenuItem addVehicle = new MenuItem("Add vehicle");
        addVehicle.setOnAction(event -> addVehicleOnAction());

        ContextMenu fullContextMenu = new ContextMenu(addVehicle, deleteVehicle, renameVehicle);
        ContextMenu emptyContextMenu = new ContextMenu(addVehicle);
        tableView_devices.setContextMenu(emptyContextMenu);

        tableView_vehicles.setRowFactory(param -> {
            TableRow<Vehicle> tableRow = new TableRow<>();

            tableRow.emptyProperty().addListener((observable, oldValue, newValue) -> {
                tableRow.setContextMenu((newValue) ? emptyContextMenu : fullContextMenu);
            });
            return tableRow;
        });
    }

    private void clearTableSelection(TableView ignoredTable) {
        for (TableView table : contentTables) {
            if (table == ignoredTable)
                continue;
            table.getSelectionModel().clearSelection();
        }
    }

    private void setupMasterTable() {
        tableColumn_masterTable_character.setCellFactory(param -> new CharacterCell());
        tableColumn_masterTable_character.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumn_masterTable_condition.setCellFactory(param -> new CharacterConditionCell());
        tableColumn_masterTable_condition.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumn_masterTable_woundModifier.setCellFactory(param -> new CharacterWoundModifierCell());
        tableColumn_masterTable_woundModifier.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumn_masterTable_initiative.setCellValueFactory(cellData -> cellData.getValue().initiativeProperty().asObject());
        tableColumn_masterTable_turn1.setCellFactory(param -> new TurnTableCell(0));
        tableColumn_masterTable_turn2.setCellFactory(param -> new TurnTableCell(1));
        tableColumn_masterTable_turn3.setCellFactory(param -> new TurnTableCell(2));
        tableColumn_masterTable_turn4.setCellFactory(param -> new TurnTableCell(3));
        tableColumn_masterTable_turn5.setCellFactory(param -> new TurnTableCell(4));
        tableColumn_masterTable_turn6.setCellFactory(param -> new TurnTableCell(5));
        tableColumn_masterTable_turn7.setCellFactory(param -> new TurnTableCell(6));

        tableColumn_masterTable_turn1.setCellValueFactory(param -> param.getValue().initiativeProperty().asObject());
        tableColumn_masterTable_turn2.setCellValueFactory(param -> param.getValue().initiativeProperty().asObject());
        tableColumn_masterTable_turn3.setCellValueFactory(param -> param.getValue().initiativeProperty().asObject());
        tableColumn_masterTable_turn4.setCellValueFactory(param -> param.getValue().initiativeProperty().asObject());
        tableColumn_masterTable_turn5.setCellValueFactory(param -> param.getValue().initiativeProperty().asObject());
        tableColumn_masterTable_turn6.setCellValueFactory(param -> param.getValue().initiativeProperty().asObject());
        tableColumn_masterTable_turn7.setCellValueFactory(param -> param.getValue().initiativeProperty().asObject());

        tableView_masterTable.setPlaceholder(new Label("No characters in combat"));
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
                Dialog<DialogFactory.InitiativeDialogResult> dialog =
                        dialogFactory.createInitiativeDialog(selectedChar, appLogic.getConfig().getApplyWound());
                dialog.showAndWait().ifPresent(result -> {
                    try {
                        selectedChar.setInitiative(result.getInitiative());
                    } catch (NumberFormatException ex) {
                        selectedChar.setInitiative(0);
                    }
                    appLogic.getConfig().setApplyWound(result.getApplyWound());
                    battle.updateCurrentCharacter();
                    refreshMasterTable();
                });
            });
            MenuItem addCharacter = new MenuItem("Add character");
            addCharacter.setOnAction(event -> addCharacterOnAction());
            MenuItem removeCharacter = new MenuItem("Remove character");
            removeCharacter.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.TRASH));
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
            tableView_masterTable.setContextMenu(emptyContextMenu);
            TableRow<Character> tableRow = new TableRow<Character>() {
                @Override
                protected void updateItem(Character item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        ObservableList<String> classes = getStyleClass();
                        CSSUtils.setCharacterBackground(item, classes);
                    } else {
                        setStyle(null);
                    }
                }
            };
            tableRow.emptyProperty().addListener((observable, oldValue, newValue) -> {
                tableRow.setContextMenu((newValue) ? emptyContextMenu : fullContextMenu);
            });
            return tableRow;
        });
        tableView_masterTable.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldCharacter, newCharacter) -> {
                    if (oldCharacter != null) {
                        label_selected_physical.textProperty().unbind();
                        label_selected_stun.textProperty().unbind();
                        label_overwatchScore.textProperty().unbind();
                        textField_selected_initiative.textProperty().unbindBidirectional(oldCharacter.initiativeProperty());
                        if (oldCharacter.playerUUIDProperty().isNotNull().get()) {
                            textField_selected_spiritIndex.textProperty()
                                    .unbindBidirectional(
                                            appLogic.getPlayer(oldCharacter.getPlayerUUID()).spiritIndexProperty());
                            label_selected_astralReputation.textProperty()
                                    .unbindBidirectional(
                                            appLogic.getPlayer(oldCharacter.getPlayerUUID()).spiritIndexProperty());
                        }
                    }

                    cleanSelectedPane();

                    if (newCharacter != null) {
                        clearTableSelection(tableView_masterTable);

                        CharacterIconFactory.createIcon(newCharacter).ifPresent(fontAwesomeIcon -> {
                            fontAwesomeIcon_selected.setIcon(fontAwesomeIcon);
                            hbox_selected_glyph.setVisible(true);
                        });
                        label_selectedCharacter.textProperty().bind(newCharacter.nameProperty());

                        if (!newCharacter.getType().equals(CharacterType.ICE)) {
                            button_selected_matrix.setVisible(true);
                            if (battle.getHost().getConnectedCharacters().containsKey(newCharacter.getUuid())) {
                                vbox_selected_matrix.setVisible(true);
                                label_overwatchScore.textProperty().bind(
                                        battle.getHost().getConnectedCharacters().get(newCharacter.getUuid()).asString());
                                button_selected_matrix.textProperty().setValue("Disconnect");
                                fontAwesomeIcon_selected_matrix.setGlyphName(FontAwesomeIcon.SIGN_OUT.name());
                            } else {
                                button_selected_matrix.textProperty().setValue("Connect");
                                fontAwesomeIcon_selected_matrix.setGlyphName(FontAwesomeIcon.SIGN_IN.name());
                            }
                        }

                        if (newCharacter.getType().equals(CharacterType.CLASSIC)) {
                            tableView_selected_companions.setItems(newCharacter.getCompanions());
                            titledPane_selected_companions.setExpanded(!newCharacter.getCompanions().isEmpty());
                            vbox_selected_companions.setVisible(true);
                        }

                        label_selected_physical.textProperty().bind(Bindings.concat(
                                newCharacter.getPhysicalMonitor().currentProperty(),
                                "/",
                                newCharacter.getPhysicalMonitor().maxProperty()
                        ));

                        label_selected_stun.textProperty().bind(Bindings.concat(
                                newCharacter.getStunMonitor().currentProperty(),
                                "/",
                                newCharacter.getStunMonitor().maxProperty()
                        ));
                        textField_selected_initiative.textProperty()
                                .bindBidirectional(newCharacter.initiativeProperty(), new NumberStringConverter());
                        if (newCharacter.playerUUIDProperty().isNotNull().get()) {
                            textField_selected_spiritIndex.textProperty()
                                    .bindBidirectional(
                                            appLogic.getPlayer(newCharacter.getPlayerUUID()).spiritIndexProperty(),
                                            new NumberStringConverter());
                            label_selected_astralReputation.textProperty()
                                    .bindBidirectional(appLogic.getPlayer(newCharacter.getPlayerUUID()).spiritIndexProperty(),
                                            new SpiritIndexReputationConverter());
                        }

                        if (newCharacter.isNpc()) {
                            flowPane_selected_badges.getChildren()
                                    .add(BadgeFactory.createBadge(
                                            "NPC",
                                            "This character is a NPC",
                                            CssClasses.INFO));
                        } else {
                            flowPane_selected_badges.getChildren()
                                    .add(BadgeFactory.createBadge(
                                            "PC",
                                            "This character is player controlled",
                                            CssClasses.PRIMARY));
                        }

                        if (newCharacter.isIce()) {
                            flowPane_selected_badges.getChildren()
                                    .add(BadgeFactory.createBadge(
                                            "ICe",
                                            "This character is part of host defense countermeasures",
                                            CssClasses.INFO));
                        }

                        if (newCharacter.getWorld() == World.MATRIX) {
                            flowPane_selected_badges.getChildren()
                                    .add(BadgeFactory.createBadge(
                                            "Matrix",
                                            "This character is inside matrix",
                                            CssClasses.SUCCESS));
                        }

                        if (newCharacter.playerUUIDProperty().isNotEmpty().get()) {
                            vbox_selected_player.setVisible(true);
                        }

                        if (newCharacter.getPortrait().imageProperty().get() != null) {
                            imageView_selected.setImage(newCharacter.getPortrait().imageProperty().getValue());
                            imageView_selected.setVisible(true);
                        } else {
                            fontAwesomeIcon_selected.setVisible(true);
                        }

                        hbox_selected_character.setVisible(true);
                        anchorPane_selected.setVisible(true);
                    }
                });

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
    }

    private void setupCompanionsTable() {
        tableColumn_selected_companions_name.setCellValueFactory(param -> param.getValue().nameProperty());
        tableColumn_selected_companions_action.setCellFactory(param ->
                new CompanionActionCell(event ->
                        spawnCompanion(event.getPayload())));
        tableColumn_selected_companions_action.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue()));

        tableView_selected_companions.setPlaceholder(new Label("No companions for this character"));
        tableView_selected_companions.setRowFactory(param -> {
            TableRow<Companion> tableRow = new CompanionTableRow();

            MenuItem deleteCompanion = new MenuItem("Delete companion");
            deleteCompanion.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.TRASH));
            deleteCompanion.setOnAction(event -> {
                tableView_selected_companions.getItems()
                        .remove(tableView_selected_companions.getSelectionModel().getSelectedItem());
            });

            MenuItem spawnCompanionItem = new MenuItem("Spawn companion");
            spawnCompanionItem.setOnAction(event -> {
                spawnCompanion(tableView_selected_companions.getSelectionModel().getSelectedItem());
            });

            MenuItem editCompanion = new MenuItem("Edit companion");
            editCompanion.setOnAction(event -> {
                Companion companion = tableView_selected_companions.getSelectionModel().getSelectedItem();
                try {
                    switch (companion.getCompanionType()) {
                        case CHARACTER:
                            AddCharacter addCharacter =
                                    dialogFactory.createCharacterDialog(appLogic.getActiveCampaign(),
                                            CharacterType.COMPANION,
                                            ((Character) companion.getCompanion()),
                                            appLogic);
                            addCharacter.getStage().showAndWait();
                            addCharacter.getCharacter().ifPresent(character -> {
                                ((Character) companion.getCompanion()).setFrom(character);
                            });
                            break;
                        case DEVICE:
                            AddDevice addDevice =
                                    dialogFactory.createDeviceDialog(appLogic.getActiveCampaign(),
                                            ((Device) companion.getCompanion()));
                            addDevice.getStage().showAndWait();
                            addDevice.getDevice().ifPresent(device -> {
                                ((Device) companion.getCompanion()).setFrom(device);
                            });
                            break;
                        case VEHICLE:
                            AddVehicle addVehicle =
                                    dialogFactory.createVehicleDialog(campaign, (Vehicle) companion.getCompanion());
                            addVehicle.getStage().showAndWait();
                            addVehicle.getVehicle().ifPresent(vehicle -> {
                                ((Vehicle) companion.getCompanion()).setFrom(vehicle);
                            });
                    }
                } catch (IOException ex) {
                    LOG.error(ex.toString());
                }
            });

            tableRow.emptyProperty().addListener((observable, oldValue, newValue) -> {
                tableRow.setContextMenu((newValue) ? null :
                        new ContextMenu(spawnCompanionItem, editCompanion, deleteCompanion));
            });
            return tableRow;
        });
    }

    private void setupBarrierTable() {
        tableColumn_barrier_object.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumn_barrier_object.setCellFactory(param -> new ObjectCell());
        tableColumn_barrier_type.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getType().getName()));
        tableColumn_barrier_armor.setCellValueFactory(param -> param.getValue().armorProperty().asObject());
        tableColumn_barrier_structure.setCellValueFactory(param -> param.getValue()
                .getStructureMonitor().currentProperty().asObject());
        tableView_barrier.setPlaceholder(new Label("No barriers in combat"));

        MenuItem renameBarrier = new MenuItem("Rename barrier");
        renameBarrier.setOnAction(event -> {
            Barrier selected = tableView_barrier.getSelectionModel().getSelectedItem();
            TextInputDialog dialog = dialogFactory.createTextInputDialog(
                    "Rename barrier",
                    "Rename barrier " + selected.getName(),
                    "Please enter name:",
                    selected.getName());
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(selected::setName);
        });
        MenuItem deleteBarrier = new MenuItem("Delete barrier");
        deleteBarrier.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.TRASH));
        deleteBarrier.setOnAction(event -> tableView_barrier.getItems()
                .remove(tableView_barrier.getSelectionModel().getSelectedIndex()));
        MenuItem addBarrier = new MenuItem("Add barrier");
        addBarrier.setOnAction(event -> addBarrierOnAction());

        ContextMenu fullContextMenu = new ContextMenu(addBarrier, deleteBarrier, renameBarrier);
        ContextMenu emptyContextMenu = new ContextMenu(addBarrier);
        tableView_barrier.setContextMenu(emptyContextMenu);
        tableView_barrier.setContextMenu(fullContextMenu);

        tableView_barrier.setRowFactory(param -> {
            TableRow<Barrier> tableRow = new TableRow<>();

            tableRow.emptyProperty().addListener((observable, oldValue, newValue) -> {
                tableRow.setContextMenu((newValue) ? emptyContextMenu : fullContextMenu);
            });
            return tableRow;
        });

        tableView_barrier.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                label_selected_armor.textProperty().unbind();
                label_selected_structure.textProperty().unbind();
            }

            cleanSelectedPane();

            if (newValue != null) {
                clearTableSelection(tableView_barrier);

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
                fontAwesomeIcon_selected.setVisible(true);
            }
        });
    }

    private void setupDeviceTable() {
        tableColumn_device_device.setCellValueFactory(param -> param.getValue().nameProperty());
        tableColumn_device_rating.setCellValueFactory(param -> param.getValue().ratingProperty().asObject());
        tableColumn_device_attack.setCellValueFactory(param -> param.getValue().attackProperty().asObject());
        tableColumn_device_sleeze.setCellValueFactory(param -> param.getValue().sleezeProperty().asObject());
        tableColumn_device_firewall.setCellValueFactory(param -> param.getValue().firewallProperty().asObject());
        tableColumn_device_dataProcessing.setCellValueFactory(
                param -> param.getValue().dataProcessingProperty().asObject());
        tableColumn_device_condition.setCellValueFactory(
                param -> param.getValue().getConditionMonitor().currentProperty().asObject());
        tableView_devices.setPlaceholder(new Label("No devices in combat"));

        MenuItem renameDevice = new MenuItem("Rename device");
        renameDevice.setOnAction(event -> {
            Device selected = tableView_devices.getSelectionModel().getSelectedItem();
            TextInputDialog dialog = dialogFactory.createTextInputDialog(
                    "Rename device",
                    "Rename device " + selected.getName(),
                    "Please enter name:",
                    selected.getName());
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(selected::setName);
        });
        MenuItem deleteDevice = new MenuItem("Delete device");
        deleteDevice.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.TRASH));
        deleteDevice.setOnAction(event -> tableView_devices.getItems()
                .remove(tableView_devices.getSelectionModel().getSelectedIndex()));
        MenuItem addDevice = new MenuItem("Add device");
        addDevice.setOnAction(event -> addDeviceOnAction());

        ContextMenu fullContextMenu = new ContextMenu(addDevice, deleteDevice, renameDevice);
        ContextMenu emptyContextMenu = new ContextMenu(addDevice);
        tableView_devices.setContextMenu(emptyContextMenu);

        tableView_devices.setRowFactory(param -> {
            TableRow<Device> tableRow = new TableRow<>();

            tableRow.emptyProperty().addListener((observable, oldValue, newValue) -> {
                tableRow.setContextMenu((newValue) ? emptyContextMenu : fullContextMenu);
            });
            return tableRow;
        });

        tableView_devices.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                label_selected_deviceCondition.textProperty().unbind();
            }

            cleanSelectedPane();

            if (newValue != null) {
                clearTableSelection(tableView_devices);

                fontAwesomeIcon_selected.setIcon(FontAwesomeIcon.LAPTOP);

                label_selected_deviceCondition.textProperty().bind(Bindings.concat(
                        newValue.getConditionMonitor().currentProperty(),
                        "/",
                        newValue.getConditionMonitor().maxProperty()
                ));
                label_selectedCharacter.textProperty().bind(newValue.nameProperty());

                hbox_selected_glyph.setVisible(true);
                hbox_selected_device.setVisible(true);
                anchorPane_selected.setVisible(true);
                fontAwesomeIcon_selected.setVisible(true);
            }
        });
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

    private void spawnCompanion(Companion companion) {
        switch (companion.getCompanionType()) {
            case CHARACTER:
                if (!battle.getCharacters().contains((Character) companion.getCompanion()))
                    battle.getCharacters().add((Character) companion.getCompanion());
                break;
            case DEVICE:
                if (!battle.getDevices().contains((Device) companion.getCompanion()))
                    battle.getDevices().add((Device) companion.getCompanion());
                break;
            case VEHICLE:
                if (!battle.getVehicles().contains((Vehicle) companion.getCompanion()))
                    battle.getVehicles().add((Vehicle) companion.getCompanion());
        }
    }

    private TableColumn<Character, Integer> passToColumn(int initiativePass) {
        switch (initiativePass) {
            default:
            case 1:
                return tableColumn_masterTable_turn1;
            case 2:
                return tableColumn_masterTable_turn2;
            case 3:
                return tableColumn_masterTable_turn3;
            case 4:
                return tableColumn_masterTable_turn4;
            case 5:
                return tableColumn_masterTable_turn5;
            case 6:
                return tableColumn_masterTable_turn6;
            case 7:
                return tableColumn_masterTable_turn7;
        }
    }

    private void refreshMasterTable() {
        tableView_masterTable.refresh();
        for (TableColumn<Character, Integer> tableColumn : turnTableColumns) {
            tableColumn.getStyleClass().remove("current-pass");
        }
        passToColumn(battle.getInitiativePass()).getStyleClass().add("current-pass");
    }

    private void initializeCanvas() {
        splitPane_centerContent.getItems().add(0, anchorPane_chaseScreen);
        canvas_chaseScreen.heightProperty().bind(anchorPane_chaseScreen.heightProperty());
        canvas_chaseScreen.widthProperty().bind(anchorPane_chaseScreen.widthProperty());
        cleanSelectedPane();
    }

    private void deinitializeCanvas() {
        canvas_chaseScreen.heightProperty().unbind();
        canvas_chaseScreen.widthProperty().unbind();
        splitPane_centerContent.getItems().remove(anchorPane_chaseScreen);
        cleanSelectedPane();
    }

}
