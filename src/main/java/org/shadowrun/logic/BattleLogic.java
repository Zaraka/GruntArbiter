package org.shadowrun.logic;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.shadowrun.models.Battle;
import org.shadowrun.models.PlayerCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BattleLogic {
    private static final Logger LOG = LoggerFactory.getLogger(BattleLogic.class);

    private ObjectProperty<Battle> activeBattle;

    private StringProperty currentCharacterName;

    public BattleLogic() {
        activeBattle = new SimpleObjectProperty<>(null);
        currentCharacterName = new SimpleStringProperty();
    }

    public BooleanBinding hasBattle() {
        return activeBattle.isNull();
    }

    public Battle getActiveBattle() {
        return activeBattle.get();
    }

    public String getCurrentCharacterName() {
        return currentCharacterName.get();
    }

    public StringProperty currentCharacterNameProperty() {
        return currentCharacterName;
    }

    public ObjectProperty<Battle> activeBattleProperty() {
        return activeBattle;
    }

    public void createNewBattle(List<PlayerCharacter> playerCharacters) {
        activeBattle.setValue(new Battle(playerCharacters));
    }

    public void nextTurn() {
        getActiveBattle().nextTurn();
        currentCharacterName.setValue(getActiveBattle().currentCharacterProperty().getName());
    }

    public void prevTurn() {
        getActiveBattle().previousturn();
        currentCharacterName.setValue(getActiveBattle().currentCharacterProperty().getName());
    }
}
