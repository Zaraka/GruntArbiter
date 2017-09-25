package org.shadowrun.common.nodes.cells;

import javafx.scene.control.ListCell;
import org.shadowrun.models.Squad;

public class SquadPressetCell extends ListCell<Squad> {
    @Override
    protected void updateItem(Squad item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setText(null);
        } else {
            setText(item.getName());
        }
    }
}
