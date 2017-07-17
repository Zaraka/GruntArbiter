package org.shadowrun.logic;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.shadowrun.common.constants.ICE;
import org.shadowrun.common.constants.Weather;
import org.shadowrun.common.constants.World;
import org.shadowrun.common.exceptions.NextTurnException;
import org.shadowrun.models.Battle;
import org.shadowrun.models.Character;
import org.shadowrun.models.Host;
import org.shadowrun.models.PlayerCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BattleLogic {
    private static final Logger LOG = LoggerFactory.getLogger(BattleLogic.class);

    private static final Pattern UUID_GROUP_PATTERN = Pattern.compile(".*-(.*)-.*");

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

    public void raiseBackgroundCount() {
        getActiveBattle().backgroundCountProperty().setValue(
                getActiveBattle().getBackgroundCount() + 1
        );
    }

    public void decreaseBackgroundCount() {
        if (getActiveBattle().getBackgroundCount() > 0) {
            getActiveBattle().backgroundCountProperty().setValue(
                    getActiveBattle().getBackgroundCount() - 1
            );
        }
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
                true,
                getActiveBattle().getHost().getRating() / 2 + 8);
        getActiveBattle().getCharacters().add(ic);
    }

    public void setHost(Host host) {
        getActiveBattle().getHost().setRating(host.getRating());
        getActiveBattle().getHost().setAttack(host.getAttack());
        getActiveBattle().getHost().setSleeze(host.getSleeze());
        getActiveBattle().getHost().setFirewall(host.getFirewall());
        getActiveBattle().getHost().setDataProcessing(host.getDataProcessing());

    }
}
