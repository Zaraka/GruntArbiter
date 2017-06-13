package org.shadowrun.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Battle {

    private static final Logger LOG = LoggerFactory.getLogger(Battle.class);

    private ObjectProperty<Host> host;

    private IntegerProperty backgroundCount;

    private IntegerProperty iteration;

    private IntegerProperty combatTurn;

    private ObservableList<Character> characters;

    private ObjectProperty<Character> currentCharacter;

    private static final Function<PlayerCharacter, Character> player2Character = playerCharacter -> new Character(playerCharacter.getName(), 0, World.REAL);

    private static final Pattern UUID_GROUP_PATTERN = Pattern.compile(".*-(.*)-.*");

    public Battle(List<PlayerCharacter> players) {
        backgroundCount = new SimpleIntegerProperty(0);
        iteration = new SimpleIntegerProperty(0);
        combatTurn = new SimpleIntegerProperty(0);
        characters = FXCollections.observableArrayList(players.stream().map(player2Character).collect(Collectors.toList()));
        currentCharacter = new SimpleObjectProperty<>(characters.stream().max(Comparator.comparingInt(Character::getInitiative)).get());
        host = new SimpleObjectProperty<>(new Host());
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

    public int getIteration() {
        return iteration.get();
    }

    public IntegerProperty iterationProperty() {
        return iteration;
    }

    public int getCombatTurn() {
        return combatTurn.get();
    }

    public IntegerProperty combatTurnProperty() {
        return combatTurn;
    }

    public Character getCurrentCharacter() {
        return currentCharacter.get();
    }

    public ObjectProperty<Character> currentCharacterProperty() {
        return currentCharacter;
    }

    public List<Character> getCombatTurnCharacters() {
        return characters.stream().filter(character -> character.getInitiative() >= iteration.get()).sorted(Comparator.comparingInt(Character::getInitiative).reversed()).collect(Collectors.toList());
    }

    public void nextTurn() {
        List<Character> combatCharacters = getCombatTurnCharacters();
        combatTurn.set(getCombatTurn() + 1);
        if (combatTurn.get() > combatCharacters.size()) {
            combatTurn.set(0);
            iteration.set(getIteration() + 1);
        }

        List<Character> combatCharactersFinal = getCombatTurnCharacters();
        currentCharacter.setValue(combatCharactersFinal.get(combatTurn.get()));

    }

    public void previousturn() {
        if(iteration.get() > 0) {
            combatTurn.set(getCombatTurn() - 1);
            if(combatTurn.get() < 1) {
                iteration.set(getIteration() - 1);
            }
            List<Character> combatCharactersFinal = getCombatTurnCharacters();
            currentCharacter.setValue(combatCharactersFinal.get(combatTurn.get()));
        }
    }

    public void spawnICe(ICE ice, Integer initiative) {
        Matcher matcher = UUID_GROUP_PATTERN.matcher(UUID.randomUUID().toString());
        StringBuilder iceName = new StringBuilder();
        iceName.append(ice.getName());
        iceName.append(" ");
        iceName.append(matcher.group(1));
        Character ic = new Character(iceName.toString(), initiative, World.MATRIX, true);
        characters.add(ic);
    }
}
