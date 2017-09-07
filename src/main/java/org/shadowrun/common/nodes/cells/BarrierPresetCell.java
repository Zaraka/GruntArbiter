package org.shadowrun.common.nodes.cells;

import javafx.scene.control.ListCell;
import org.shadowrun.common.constants.BarrierPreset;

public class BarrierPresetCell extends ListCell<BarrierPreset> {
    @Override
    protected void updateItem(BarrierPreset item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setText(null);
        } else {
            setText(item.getName());
        }
    }
}
