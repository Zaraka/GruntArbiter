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
import org.shadowrun.common.constants.NoisePreset;
import org.shadowrun.common.nodes.cells.NoisePresetCell;
import org.shadowrun.common.nodes.cells.WeatherCell;
import org.shadowrun.common.constants.Weather;
import org.shadowrun.models.PlayerCharacter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ControllerNewBattle implements Controller {
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
    private List<PlayerCharacter> includedPlayers;
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
    private TextField textField_name;

    @FXML
    private ComboBox<Weather> comboBox_weather;
    @FXML
    private ComboBox<NoisePreset> comboBox_backgroundNoise;

    @FXML
    private CheckBox checkBox_includeAll;

    @FXML
    private Button button_ok;

    @FXML
    private void closeOnAction() {
        stage.close();
    }

    @FXML
    private void okOnAction() {
        includedPlayers = players.stream().filter(PickPlayer::getChecked).map(PickPlayer::getPlayerCharacter).collect(Collectors.toList());
        time = Integer.parseInt(textField_hours.getText()) * 3600 +
                Integer.parseInt(textField_minutes.getText()) * 60 +
                Integer.parseInt(textField_seconds.getText());
        weather = comboBox_weather.getSelectionModel().getSelectedItem();

        stage.close();
    }

    @FXML
    private void includeAllOnAction() {
        boolean checked = checkBox_includeAll.isSelected();
        players.forEach(pickPlayer -> pickPlayer.checkedProperty().setValue(checked));
    }

    public void onOpen(Stage stage, String initialName, List<PlayerCharacter> players) {
        this.stage = stage;
        this.includedPlayers = null;
        this.players = FXCollections.observableArrayList(players.stream().map(PickPlayer::new).collect(Collectors.toList()));

        tableView_players.setItems(this.players);
        tableColumn_character.setCellValueFactory(cellData -> cellData.getValue().getPlayerCharacter().nameProperty());
        tableColumn_include.setCellValueFactory(cellData -> cellData.getValue().checkedProperty());

        textField_hours.textProperty().addListener(new NumericLimitListener(textField_hours, 0,23));
        textField_minutes.textProperty().addListener(new NumericLimitListener(textField_minutes, 0,59));
        textField_seconds.textProperty().addListener(new NumericLimitListener(textField_seconds, 0,59));

        comboBox_weather.setCellFactory(param -> new WeatherCell());
        comboBox_weather.setButtonCell(new WeatherCell());
        comboBox_weather.setItems(FXCollections.observableArrayList(Weather.values()));

        textField_name.setText(initialName);
        textField_name.selectAll();

        comboBox_backgroundNoise.setCellFactory(param -> new NoisePresetCell());
        comboBox_backgroundNoise.setButtonCell(new NoisePresetCell());
        comboBox_backgroundNoise.setItems(FXCollections.observableArrayList(NoisePreset.values()));

        button_ok.disableProperty().bind(textField_name.textProperty().isEmpty());

        button_ok.requestFocus();
    }

    public Optional<List<PlayerCharacter>> getIncludedPlayers() {
        return Optional.ofNullable(includedPlayers);
    }

    public Weather getWeather() {
        return weather;
    }

    public Integer getTime() {
        return time;
    }

    public String getName() {
        return textField_name.getText();
    }

    public Integer getNoise() {
        NoisePreset noisePreset = comboBox_backgroundNoise.getSelectionModel().getSelectedItem();
        return (noisePreset == null) ? 0 : noisePreset.getNoise();
    }

    @Override
    public Stage getStage() {
        return stage;
    }
}
