package org.shadowrun.common.nodes.cells;

import javafx.scene.control.TreeCell;
import org.shadowrun.models.Vehicle;

public class VehicleTreeCell extends TreeCell<Vehicle>{

    @Override
    protected void updateItem(Vehicle item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setText(null);
        } else {
            setText(item.getName());
        }
    }
}
