package org.shadowrun.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.shadowrun.common.constants.ICE;
import org.shadowrun.common.constants.Weather;
import org.shadowrun.common.constants.World;
import org.shadowrun.common.exceptions.NextTurnException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Battle {

    private static final Logger LOG = LoggerFactory.getLogger(Battle.class);

    private ObjectProperty<Host> host;

    private IntegerProperty backgroundCount;

    private IntegerProperty combatTurn;

    private IntegerProperty initiativePass;

    private IntegerProperty actionPhase;

    private ObservableList<Character> characters;

    private ObjectProperty<Character> currentCharacter;

    private ObservableList<Weather> weatherList;

    private static final Function<PlayerCharacter, Character> player2Character =
            playerCharacter -> new Character(playerCharacter.getName(), 0, World.REAL);

    private static final Pattern UUID_GROUP_PATTERN = Pattern.compile(".*-(.*)-.*");

    public Battle(List<PlayerCharacter> players) {
        backgroundCount = new SimpleIntegerProperty(0);
        combatTurn = new SimpleIntegerProperty(1);
        initiativePass = new SimpleIntegerProperty(1);
        actionPhase = new SimpleIntegerProperty(1);
        characters = FXCollections.observableArrayList(players.stream()
                .map(player2Character).collect(Collectors.toList()));
        weatherList = FXCollections.observableArrayList(Weather.values());
        currentCharacter = new SimpleObjectProperty<>(characters.stream()
                .max(Comparator.comparingInt(Character::getInitiative)).get());
        host = new SimpleObjectProperty<>(new Host());
        currentCharacterProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.setSelected(false);
            }
            if (newValue != null) {
                newValue.setSelected(true);
            }
        });
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

    public ObservableList<Weather> getWeatherList() {
        return weatherList;
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
            if(getInitiativePass() > passLimit() + 1) {
                initiativePass.setValue(1);
                combatTurn.setValue(getCombatTurn() + 1);
                throw new NextTurnException();
            }
        }

        refreshPhase();
    }

    private int passLimit() {
        Optional<Character> ch = characters.stream().max(Comparator.comparingInt(Character::getInitiative));
        return ch.map(character -> character.getInitiative() / 10).orElse(0);
    }

    public void previousPhase() throws NextTurnException {
        if (combatTurn.get() > 1) {
            actionPhase.setValue(getActionPhase() - 1);
            if(getActionPhase() > 1) {
                actionPhase.setValue(1);
                initiativePass.setValue(getInitiativePass() - 1);
                if (getInitiativePass() > 1) {
                    combatTurn.setValue(getCombatTurn() - 1);
                    throw new NextTurnException();
                }
            }
            refreshPhase();
        }
    }

    public void refreshPhase() {
        List<Character> combatCharactersFinal = getCombatTurnCharacters();
        if(!combatCharactersFinal.isEmpty())
            currentCharacter.setValue(combatCharactersFinal.get(getActionPhase() - 1));
    }

    public void spawnICe(ICE ice, Integer initiative) {
        String UUIDs = UUID.randomUUID().toString();
        Matcher matcher = UUID_GROUP_PATTERN.matcher(UUIDs);
        StringBuilder iceName = new StringBuilder();
        iceName.append(ice.getName());
        if (matcher.matches()) {
            iceName.append(" ");
            iceName.append(matcher.group(1));
        }
        Character ic = new Character(iceName.toString(),
                initiative,
                World.MATRIX,
                true,
                getHost().getRating() / 2 + 8);
        characters.add(ic);
    }
}
