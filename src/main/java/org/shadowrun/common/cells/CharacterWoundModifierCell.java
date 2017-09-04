package org.shadowrun.common.cells;

import javafx.scene.control.TableCell;
import org.shadowrun.models.Character;

public class CharacterWoundModifierCell extends TableCell<Character, Character> {

    @Override
    protected void updateItem(Character item, boolean empty) {
        super.updateItem(item, empty);

        setGraphic(null);
        if(empty || item == null) {
            setText(null);
        } else {
            setText(String.valueOf(item.getPhysicalMonitor().countWoundModifier() +
                    item.getStunMonitor().countWoundModifier()));
        }
    }
}