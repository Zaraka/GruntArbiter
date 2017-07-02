package org.shadowrun.logic;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.shadowrun.common.constants.Weather;
import org.shadowrun.common.exceptions.NextTurnException;
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

    public void createNewBattle(List<PlayerCharacter> playerCharacters, Weather weather, Integer time) {
        activeBattle.setValue(new Battle(playerCharacters, weather, time));
    }

    public void nextPhase() throws NextTurnException {
        getActiveBattle().nextPhase();
        currentCharacterName.setValue(getActiveBattle().currentCharacterProperty().get().getName());
    }

    public void prevPhase() throws NextTurnException {
        getActiveBattle().previousPhase();
        currentCharacterName.setValue(getActiveBattle().currentCharacterProperty().get().getName());
    }

    public void refreshPhase() {
        getActiveBattle().refreshPhase();
        currentCharacterName.setValue(getActiveBattle().currentCharacterProperty().get().getName());
    }

    public void raiseOverwatchScore() {
        getActiveBattle().getHost().overwatchScoreProperty().setValue(getActiveBattle().getHost().getOverwatchScore() + 1);
    }

    public void decreaseOverwatchScore() {
        if (getActiveBattle().getHost().getOverwatchScore() > 0)
            getActiveBattle().getHost().overwatchScoreProperty().setValue(getActiveBattle().getHost().getOverwatchScore() - 1);
    }

    public void resetOverwatchScoore() {
        getActiveBattle().getHost().overwatchScoreProperty().setValue(0);
    }
}
