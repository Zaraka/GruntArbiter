package org.shadowrun.controllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.shadowrun.common.NumericLimitListener;
import org.shadowrun.common.cells.WeatherCell;
import org.shadowrun.common.constants.Weather;
import org.shadowrun.models.PlayerCharacter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ControllerNewBattle {
    public class PickPlayer {
        private ObjectProperty<PlayerCharacter> playerCharacter;
        private BooleanProperty checked;

        public PickPlayer(PlayerCharacter playerCharacter) {
            this.playerCharacter = new SimpleObjectProperty<>(playerCharacter);
            this.checked = new SimpleBooleanProperty(true);
        }

        public PlayerCharacter getPlayerCharacter() {
            return playerCharacter.get();
        }

        public ObjectProperty<PlayerCharacter> playerCharacterProperty() {
            return playerCharacter;
        }

        public BooleanProperty checkedProperty() {
            return this.checked;
        }

        public java.lang.Boolean getChecked() {
            return this.checkedProperty().get();
        }
        
    }

    private Stage stage;
    private ObservableList<PickPlayer> players;
    private Optional<List<PlayerCharacter>> includedPlayers;
    private Weather weather;
    private Integer time;

    @FXML
    private TableView<PickPlayer> tableView_players;
    @FXML
    private TableColumn<PickPlayer, String> tableColumn_character;
    @FXML
    private TableColumn<PickPlayer, Boolean> tableColumn_include;

    @FXML
    private TextField textField_hours;
    @FXML
    private TextField textField_minutes;
    @FXML
    private TextField textField_seconds;

    @FXML
    private ComboBox<Weather> comboBox_weather;

    @FXML
    private Button button_ok;

    @FXML
    public void closeOnAction() {
        stage.close();
    }

    @FXML
    public void okOnAction() {
        includedPlayers = Optional.of(players.stream().filter(PickPlayer::getChecked).map(PickPlayer::getPlayerCharacter).collect(Collectors.toList()));
        time = Integer.parseInt(textField_hours.getText()) * 3600 +
                Integer.parseInt(textField_minutes.getText()) * 60 +
                Integer.parseInt(textField_seconds.getText());
        weather = comboBox_weather.getSelectionModel().getSelectedItem();

        stage.close();
    }

    public void onOpen(Stage stage, List<PlayerCharacter> players) {
        this.stage = stage;
        this.includedPlayers = Optional.empty();
        this.players = FXCollections.observableArrayList(players.stream().map(PickPlayer::new).collect(Collectors.toList()));

        tableView_players.setItems(this.players);
        tableColumn_character.setCellValueFactory(cellData -> cellData.getValue().getPlayerCharacter().nameProperty());
        tableColumn_include.setCellValueFactory(cellData -> cellData.getValue().checkedProperty());

        textField_hours.textProperty().addListener(new NumericLimitListener(textField_hours, 23));
        textField_minutes.textProperty().addListener(new NumericLimitListener(textField_minutes, 59));
        textField_seconds.textProperty().addListener(new NumericLimitListener(textField_seconds, 59));

        comboBox_weather.setCellFactory(param -> new WeatherCell());
        comboBox_weather.setButtonCell(new WeatherCell());
        comboBox_weather.setItems(FXCollections.observableArrayList(Weather.values()));

        button_ok.requestFocus();
    }

    public Optional<List<PlayerCharacter>> getIncludedPlayers() {
        return includedPlayers;
    }

    public Weather getWeather() {
        return weather;
    }

    public Integer getTime() {
        return time;
    }
}