package org.shadowrun.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.shadowrun.common.constants.CharacterType;
import org.shadowrun.common.factories.DialogFactory;
import org.shadowrun.common.nodes.cells.CharacterPresetCell;
import org.shadowrun.common.nodes.cells.SquadPressetCell;
import org.shadowrun.models.Campaign;
import org.shadowrun.models.Character;
import org.shadowrun.models.Squad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class ControllerManageSquads implements Controller {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerManageSquads.class);

    private static final DialogFactory dialogFactory = new DialogFactory();

    private Campaign campaign;

    private Stage stage;

    private Squad squad;

    @FXML
    private Button button_saveSquad;
    @FXML
    private Button button_deleteSquad;
    @FXML
    private Button button_addSquad;

    @FXML
    private TextField textField_name;

    @FXML
    private ComboBox<Squad> comboBox_squads;
    @FXML
    private ComboBox<Character> comboBox_pressets;

    @FXML
    private TableView<Character> tableView_characters;
    @FXML
    private TableColumn<Character, String> tableColumn_characters_name;
    @FXML
    private TableColumn<Character, Integer> tableColumn_characters_physical;
    @FXML
    private TableColumn<Character, Integer> tableColumn_characters_stun;
    @FXML
    private TableColumn<Character, Character> tableColumn_characters_actions;

    @FXML
    private void saveSquadOnAction() {
        campaign.getSquads().removeIf(squad1 -> squad1.getName().equals(textField_name.getText()));
        campaign.getCharacterPresets()
                .removeIf(character1 -> Objects.equals(character1.getName(), textField_name.getText()));
        Squad squad = createSquad();
        campaign.getSquads().add(squad);
        comboBox_squads.getSelectionModel().select(squad);
    }

    @FXML
    private void deleteSquadOnAction() {
        campaign.getSquads().remove(comboBox_squads.getSelectionModel().getSelectedItem());
        textField_name.setText(StringUtils.EMPTY);
        tableView_characters.getItems().clear();
    }

    @FXML
    private void addToSquadOnAction() {
        tableView_characters.getItems().add(new Character(comboBox_pressets.getSelectionModel().getSelectedItem()));
    }

    @FXML
    private void createCharacterOnAction() {

        try {
            ControllerAddCharacter controllerAddCharacter = dialogFactory.createCharacterDialog(
                    campaign,
                    CharacterType.CLASSIC,
                    null);
            controllerAddCharacter.getStage().showAndWait();
            controllerAddCharacter.getCharacter().ifPresent(playerCharacter -> {
                tableView_characters.getItems().add(playerCharacter);
            });

        } catch (IOException ex) {
            LOG.error("Could not load addCharacter dialog: ", ex);
        }

    }

    @FXML
    private void okOnAction() {
        squad = createSquad();

        stage.close();
    }

    @FXML
    private void cancelOnAction() {
        stage.close();
    }

    public void onOpen(Stage stage, Campaign campaign) {
        this.stage = stage;
        this.campaign = campaign;
        this.squad = null;

        comboBox_squads.setItems(campaign.getSquads());
        comboBox_squads.setButtonCell(new SquadPressetCell());
        comboBox_squads.setCellFactory(param -> new SquadPressetCell());

        comboBox_squads.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                textField_name.setText(newValue.getName());
                tableView_characters.setItems(FXCollections.observableArrayList(newValue.getCharacters()));
            }
        });


        comboBox_pressets.setItems(campaign.getCharacterPresets());
        comboBox_pressets.setButtonCell(new CharacterPresetCell());
        comboBox_pressets.setCellFactory(param -> new CharacterPresetCell());

        tableColumn_characters_name.setCellValueFactory(param -> param.getValue().nameProperty());
        tableColumn_characters_physical.setCellValueFactory(param ->
                param.getValue().getPhysicalMonitor().maxProperty().asObject());
        tableColumn_characters_stun.setCellValueFactory(param ->
                param.getValue().getStunMonitor().maxProperty().asObject());

        tableView_characters.setRowFactory(param -> {
            TableRow<Character> tableRow = new TableRow<>();

            MenuItem renameCharacter = new MenuItem("Rename character");
            renameCharacter.setOnAction(event -> {
                Character selected = tableView_characters.getSelectionModel().getSelectedItem();
                TextInputDialog dialog = dialogFactory.createTextInputDialog(
                        "Rename character",
                        "Rename character " + selected.getName(),
                        "Please enter name:",
                        selected.getName());
                Optional<String> result = dialog.showAndWait();

                result.ifPresent(selected::setName);
            });
            MenuItem editCharacter = new MenuItem("Edit character");
            editCharacter.setOnAction(event -> {
                Character selected = tableView_characters.getSelectionModel().getSelectedItem();
                try {
                    ControllerAddCharacter controllerAddCharacter = dialogFactory.createCharacterDialog(
                            campaign, CharacterType.CLASSIC, selected);
                    controllerAddCharacter.getStage().showAndWait();

                    controllerAddCharacter.getCharacter().ifPresent(character -> {
                        int index = tableView_characters.getSelectionModel().getSelectedIndex();
                        tableView_characters.getItems().remove(index);
                        tableView_characters.getItems().add(index, character);
                    });
                } catch (IOException ex) {
                    LOG.info("Cannot create edit dialog: ", ex);
                }
            });
            MenuItem deleteCharacter = new MenuItem("Delete character");
            deleteCharacter.setOnAction(event -> tableView_characters.getItems()
                    .remove(tableView_characters.getSelectionModel().getSelectedIndex()));
            MenuItem addCharacter = new MenuItem("Add character");
            addCharacter.setOnAction(event -> createCharacterOnAction());
            ContextMenu fullContextMenu = new ContextMenu(addCharacter, deleteCharacter, renameCharacter, editCharacter);
            ContextMenu emptyContextMenu = new ContextMenu(addCharacter);

            tableRow.emptyProperty().addListener((observable, oldValue, newValue) ->
                    tableRow.setContextMenu(newValue ? emptyContextMenu : fullContextMenu));
            return tableRow;
        });


        button_addSquad.disableProperty().bind(comboBox_pressets.getSelectionModel().selectedItemProperty().isNull());

        button_deleteSquad.disableProperty().bind(comboBox_squads.getSelectionModel().selectedItemProperty().isNull());
        button_saveSquad.disableProperty().bind(textField_name.textProperty().isEmpty());

    }

    private Squad createSquad() {
        Squad squad = new Squad();
        squad.nameProperty().setValue(textField_name.getText());
        squad.getCharacters().addAll(tableView_characters.getItems());
        return squad;
    }

    public Optional<Squad> getSquad() {
        return Optional.ofNullable(squad);
    }

    @Override
    public Stage getStage() {
        return stage;
    }
}
