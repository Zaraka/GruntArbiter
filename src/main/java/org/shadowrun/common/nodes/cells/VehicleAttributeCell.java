package org.shadowrun.common.nodes.cells;

import javafx.scene.control.TableCell;
import org.shadowrun.models.Character;
import org.shadowrun.models.VehicleAttribute;

public class VehicleAttributeCell extends TableCell<Character, VehicleAttribute> {

    @Override
    protected void updateItem(VehicleAttribute item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setText(null);
        } else {
            setText(item.getOnRoad() + "/" + item.getOffRoad());
        }
    }
}
