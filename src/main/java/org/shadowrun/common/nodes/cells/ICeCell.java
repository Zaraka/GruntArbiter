package org.shadowrun.common.nodes.cells;

import javafx.scene.control.ListCell;
import org.shadowrun.common.constants.ICE;

public class ICeCell extends ListCell<ICE> {
    @Override
    protected void updateItem(ICE item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setText(null);
        } else {
            setText(item.getName());
        }
    }
}

