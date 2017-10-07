package org.shadowrun.logic;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.shadowrun.common.constants.CharacterType;
import org.shadowrun.common.constants.ICE;
import org.shadowrun.common.constants.World;
import org.shadowrun.common.exceptions.NextTurnException;
import org.shadowrun.controllers.ControllerBattle;
import org.shadowrun.models.Battle;
import org.shadowrun.models.Character;
import org.shadowrun.models.Host;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BattleLogic {
    private static final Logger LOG = LoggerFactory.getLogger(BattleLogic.class);

    private static final Pattern UUID_GROUP_PATTERN = Pattern.compile(".*-(.*)-.*");

    private ObjectProperty<Battle> activeBattle;

    private ObjectProperty<ControllerBattle> activeBattleController;

    private StringProperty currentCharacterName;

    private BooleanProperty hasHost;

    public BattleLogic() {
        activeBattle = new SimpleObjectProperty<>(null);
        activeBattleController = new SimpleObjectProperty<>(null);
        currentCharacterName = new SimpleStringProperty();
        hasHost = new SimpleBooleanProperty(false);
    }

    public BooleanBinding hasBattle() {
        return activeBattle.isNotNull();
    }

    public boolean hasHost() {
        return hasHost.get();
    }

    public BooleanProperty hasHostProperty() {
        return hasHost;
    }

    public String getCurrentCharacterName() {
        return currentCharacterName.get();
    }

    public StringProperty currentCharacterNameProperty() {
        return currentCharacterName;
    }

    public Pair<Battle, ControllerBattle> getActiveBattle() {
        return new Pair<>(activeBattle.get(), activeBattleController.get());
    }

    public void setActiveBattle(Battle battle, ControllerBattle controllerBattle) {
        activeBattle.setValue(battle);
        activeBattleController.setValue(controllerBattle);
    }

    public void nextPhase() {
        try {
            activeBattle.get().nextPhase();
        } catch (NextTurnException e) {
            activeBattleController.get().setNewInitiative();
            activeBattle.get().refreshPhase();
        } finally {
            currentCharacterName.setValue(activeBattle.get().currentCharacterProperty().get().getName());
        }
    }

    public void prevPhase() {
        try {
            activeBattle.get().previousPhase();
        } catch (NextTurnException e) {
            activeBattleController.get().setNewInitiative();
            activeBattle.get().refreshPhase();
        } finally {
            currentCharacterName.setValue(activeBattle.get().currentCharacterProperty().get().getName());
        }
    }

    public void raiseBackgroundCount() {
        activeBattle.get().backgroundCountProperty().setValue(
                activeBattle.get().getBackgroundCount() + 1
        );
    }

    public void decreaseBackgroundCount() {
        if (activeBattle.get().getBackgroundCount() > 0) {
            activeBattle.get().backgroundCountProperty().setValue(
                    activeBattle.get().getBackgroundCount() - 1
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
                CharacterType.ICE,
                activeBattle.get().getHost().getRating() / 2 + 8,
                0,
                null, null);
        activeBattle.get().getCharacters().add(ic);
    }

    public void setHost(Host host) {
        Host activeHost = activeBattle.get().getHost();
        activeHost.setRating(host.getRating());
        activeHost.setAttack(host.getAttack());
        activeHost.setSleeze(host.getSleeze());
        activeHost.setFirewall(host.getFirewall());
        activeHost.setDataProcessing(host.getDataProcessing());
        hasHost.setValue(true);
    }

    public void disconectFromHost() {
        Host activeHost = activeBattle.get().getHost();
        activeHost.setRating(0);
        activeHost.setAttack(0);
        activeHost.setSleeze(0);
        activeHost.setFirewall(0);
        activeHost.setDataProcessing(0);

        activeBattle.get().getCharacters().removeIf(Character::isIce);
        hasHost.setValue(false);
    }

    public void clear() {
        currentCharacterName.setValue(StringUtils.EMPTY);
        activeBattle.setValue(null);
        hasHost.setValue(false);
    }
}
