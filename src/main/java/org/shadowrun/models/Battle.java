package org.shadowrun.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Battle {

    private ObjectProperty<Host> host;

    private IntegerProperty backgroundCount;

    private IntegerProperty iteration;

    private IntegerProperty combatTurn;

    private ObservableList<Character> characters;

    private ObjectProperty<Character> currentCharacter;

    private static final Function<PlayerCharacter, Character> player2Character = playerCharacter -> new Character(playerCharacter.getName(), 0, World.REAL, false);

    public Battle(List<PlayerCharacter> players) {
        backgroundCount = new SimpleIntegerProperty(0);
        iteration = new SimpleIntegerProperty(0);
        combatTurn = new SimpleIntegerProperty(0);
        characters = FXCollections.observableArrayList(players.stream().map(player2Character).collect(Collectors.toList()));
        currentCharacter = new SimpleObjectProperty<>(characters.stream().max(Comparator.comparingInt(Character::getInitiative)).get());
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
        combatTurn.add(1);
        if (combatTurn.get() > combatCharacters.size()) {
            combatTurn.set(1);
            iteration.add(1);
        }

        List<Character> combatCharactersFinal = getCombatTurnCharacters();
        currentCharacter.setValue(combatCharactersFinal.get(combatTurn.get()));
    }

    public void previousturn() {
        if(iteration.get() > 0) {
            combatTurn.subtract(1);
            if(combatTurn.get() < 1) {
                iteration.subtract(1);
            }
            List<Character> combatCharactersFinal = getCombatTurnCharacters();
            currentCharacter.setValue(combatCharactersFinal.get(combatTurn.get()));
        }
    }
}
