package org.shadowrun.logic;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.shadowrun.models.Battle;
import org.shadowrun.models.PlayerCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BattleLogic {
    private static final Logger LOG = LoggerFactory.getLogger(BattleLogic.class);

    private ObjectProperty<Battle> activeBattle;

    public BattleLogic() {
        activeBattle = new SimpleObjectProperty<>(null);
    }

    public BooleanBinding hasBattle() {
        return activeBattle.isNull();
    }

    public Battle getActiveBattle() {
        return activeBattle.get();
    }

    public ObjectProperty<Battle> activeBattleProperty() {
        return activeBattle;
    }

    public void createNewBattle(List<PlayerCharacter> playerCharacters) {

    }

}
