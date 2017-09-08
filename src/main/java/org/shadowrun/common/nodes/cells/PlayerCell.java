package org.shadowrun.common.nodes.cells;

import javafx.scene.control.ListCell;
import org.shadowrun.models.PlayerCharacter;

public class PlayerCell extends ListCell<PlayerCharacter> {

    @Override
    protected void updateItem(PlayerCharacter item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setText(null);
        } else {
            setText(item.getName());
        }
        setGraphic(null);
    }
}
