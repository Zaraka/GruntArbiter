package org.shadowrun.common.nodes.cells;

import javafx.scene.control.ListCell;
import org.shadowrun.models.Device;

public class DevicePresetCell extends ListCell<Device> {
    @Override
    protected void updateItem(Device item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setText(null);
        } else {
            setText(item.getName());
        }
    }
}