package org.shadowrun.models;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.shadowrun.common.constants.Weather;
import org.shadowrun.common.constants.World;
import org.shadowrun.common.exceptions.NextTurnException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Battle {

    private static final Logger LOG = LoggerFactory.getLogger(Battle.class);

    private static final Function<PlayerCharacter, Character> player2Character =
            playerCharacter -> new Character(
                    playerCharacter.getName(),
                    0,
                    World.REAL,
                    false,
                    false,
                    playerCharacter.getPhysicalMonitor(),
                    playerCharacter.getStunMonitor(),
                    playerCharacter);

    private ObjectProperty<Host> host;

    private IntegerProperty backgroundCount;

    private IntegerProperty combatTurn;

    private IntegerProperty initiativePass;

    private IntegerProperty actionPhase;

    private IntegerProperty time;

    private ObservableList<Character> characters;

    private ObservableList<Barrier> barriers;

    private ObservableList<Device> devices;

    private ObservableList<Vehicle> vehicles;

    private ObjectProperty<Character> currentCharacter;

    private ObjectProperty<Weather> selectedWeather;

    private NumberBinding maxInitiative;

    private StringProperty name;

    public Battle(String name, List<PlayerCharacter> players, Weather weather, Integer startingTime) {
        backgroundCount = new SimpleIntegerProperty(0);
        combatTurn = new SimpleIntegerProperty(1);
        initiativePass = new SimpleIntegerProperty(1);
        actionPhase = new SimpleIntegerProperty(1);
        characters = FXCollections.observableArrayList(players.stream()
                .map(player2Character).collect(Collectors.toList()));
        barriers = FXCollections.observableArrayList();
        devices = FXCollections.observableArrayList();
        vehicles = FXCollections.observableArrayList();
        currentCharacter = new SimpleObjectProperty<>(null);
        characters.stream()
                .max(Comparator.comparingInt(Character::getInitiative))
                .ifPresent(character -> currentCharacter.setValue(character));
        host = new SimpleObjectProperty<>(new Host());
        selectedWeather = new SimpleObjectProperty<>(weather);
        time = new SimpleIntegerProperty(startingTime);
        maxInitiative = null;
        this.name = new SimpleStringProperty(name);
    }

    public Host getHost() {
        return host.get();
    }

    public ObjectProperty<Host> hostProperty() {
        return host;
    }

    public int getBackgroundCount() {
        return backgroundCount.get();
    }

    public IntegerProperty backgroundCountProperty() {
        return backgroundCount;
    }

    public ObservableList<Character> getCharacters() {
        return characters;
    }

    public int getCombatTurn() {
        return combatTurn.get();
    }

    public IntegerProperty combatTurnProperty() {
        return combatTurn;
    }

    public int getInitiativePass() {
        return initiativePass.get();
    }

    public IntegerProperty initiativePassProperty() {
        return initiativePass;
    }

    public Character getCurrentCharacter() {
        return currentCharacter.get();
    }

    public ObjectProperty<Character> currentCharacterProperty() {
        return currentCharacter;
    }

    public List<Character> getCombatTurnCharacters() {
        return characters.stream().filter(character -> character.getInitiative() > (initiativePass.get() - 1) * 10)
                .sorted(Comparator.comparingInt(Character::getInitiative).reversed()).collect(Collectors.toList());
    }

    public int getActionPhase() {
        return actionPhase.get();
    }

    public IntegerProperty actionPhaseProperty() {
        return actionPhase;
    }

    public Weather getSelectedWeather() {
        return selectedWeather.get();
    }

    public ObjectProperty<Weather> selectedWeatherProperty() {
        return selectedWeather;
    }

    public int getTime() {
        return time.get();
    }

    public IntegerProperty timeProperty() {
        return time;
    }

    public ObservableList<Barrier> getBarriers() {
        return barriers;
    }

    public ObservableList<Device> getDevices() {
        return devices;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public ObservableList<Vehicle> getVehicles() {
        return vehicles;
    }

    public void updateCurrentCharacter() {
        Optional<Character> current = characters.stream().max(Comparator.comparingInt(Character::getInitiative));
        current.ifPresent(character -> currentCharacter.setValue(character));
    }

    public void nextPhase() throws NextTurnException {
        actionPhase.setValue(getActionPhase() + 1);
        List<Character> combatCharacters = getCombatTurnCharacters();
        if (getActionPhase() > combatCharacters.size()) {
            actionPhase.setValue(1);
            initiativePass.setValue(getInitiativePass() + 1);
            if (getInitiativePass() > passLimit()) {
                initiativePass.setValue(1);
                combatTurn.setValue(getCombatTurn() + 1);
                throw new NextTurnException();
            }
        }

        refreshPhase();
    }

    private int passLimit() {
        Optional<Character> ch = characters.stream().max(Comparator.comparingInt(Character::getInitiative));

        return (int) Math.ceil((double) ch.map(Character::getInitiative).orElse(0) / 10);
    }

    public void previousPhase() throws NextTurnException {
        if (combatTurn.get() > 1 || actionPhase.get() > 1 || initiativePass.get() > 1) {
            actionPhase.setValue(getActionPhase() - 1);
            if (getActionPhase() < 1) {
                actionPhase.setValue(1);
                initiativePass.setValue(getInitiativePass() - 1);
                if (getInitiativePass() < 1) {
                    initiativePass.setValue(1);
                    combatTurn.setValue(getCombatTurn() - 1);
                    throw new NextTurnException();
                }
            }
            refreshPhase();
        }
    }

    public void refreshPhase() {
        List<Character> combatCharactersFinal = getCombatTurnCharacters();
        if (!combatCharactersFinal.isEmpty())
            currentCharacter.setValue(combatCharactersFinal.get(getActionPhase() - 1));
    }

    public List<Character> getICe() {
        return characters.stream().filter(character -> character.isNpc() && character.isIce())
                .collect(Collectors.toList());
    }

    public NumberBinding maxInitiativeBinding() {
        if(maxInitiative == null) {
            maxInitiative = Bindings.createIntegerBinding(() -> characters.stream()
                    .max(Character::compareTo).map(Character::getInitiative).orElse(0));
            characters.addListener((InvalidationListener) observable -> maxInitiative.invalidate());
        }
        return maxInitiative;
    }

    public void insertPlayer(PlayerCharacter playerCharacter) {
        characters.add(player2Character.apply(playerCharacter));
    }
}
