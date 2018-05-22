package org.shadowrun.common.nodes.cells;

import javafx.scene.control.ListCell;
import org.shadowrun.common.constants.BarrierType;

public class BarrierPresetCell extends ListCell<BarrierType> {
    @Override
    protected void updateItem(BarrierType item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setText(null);
        } else {
            setText(item.getName());
        }
    }
}
