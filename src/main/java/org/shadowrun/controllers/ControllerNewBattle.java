package org.shadowrun.controllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.shadowrun.models.PlayerCharacter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ControllerNewBattle {
    private class PickPlayer {
        private ObjectProperty<PlayerCharacter> playerCharacter;
        private BooleanProperty include;

        public PickPlayer(PlayerCharacter playerCharacter) {
            this.playerCharacter = new SimpleObjectProperty<>(playerCharacter);
            this.include = new SimpleBooleanProperty(false);
        }

        public PlayerCharacter getPlayerCharacter() {
            return playerCharacter.get();
        }

        public ObjectProperty<PlayerCharacter> playerCharacterProperty() {
            return playerCharacter;
        }

        public boolean isInclude() {
            return include.get();
        }

        public BooleanProperty includeProperty() {
            return include;
        }
    }

    private Stage stage;
    private ObservableList<PickPlayer> players;
    private Optional<List<PlayerCharacter>> includedPlayers;


    @FXML
    private TableView<PickPlayer> tableView_players;
    @FXML
    private TableColumn<PickPlayer, String> tableColumn_character;
    @FXML
    private TableColumn<PickPlayer, Boolean> tableColumn_include;

    @FXML
    public void closeOnAction() {
        stage.close();
    }

    @FXML
    public void okOnAction() {
        this.includedPlayers = Optional.of(players.stream().filter(PickPlayer::isInclude).map(PickPlayer::getPlayerCharacter).collect(Collectors.toList()));
        stage.close();
    }

    public void onOpen(Stage stage, List<PlayerCharacter> players) {
        this.stage = stage;
        this.includedPlayers = Optional.empty();
        this.players = FXCollections.observableArrayList(players.stream().map(PickPlayer::new).collect(Collectors.toList()));

        tableView_players.setItems(this.players);
        tableColumn_character.setCellValueFactory(cellData -> cellData.getValue().getPlayerCharacter().nameProperty());
        tableColumn_include.setCellValueFactory(cellData -> cellData.getValue().include.asObject());
    }

    public Optional<List<PlayerCharacter>> getIncludedPlayers() {
        return includedPlayers;
    }
}
