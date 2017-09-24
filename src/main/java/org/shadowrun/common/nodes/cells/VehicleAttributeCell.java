package org.shadowrun.common.nodes.cells;

import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import org.shadowrun.models.Vehicle;
import org.shadowrun.models.VehicleAttribute;

public class VehicleAttributeCell extends TableCell<Vehicle, VehicleAttribute> {
    @Override
    protected void updateItem(VehicleAttribute item, boolean empty) {
        super.updateItem(item, empty);

        setGraphic(null);
        if(empty || item == null) {
            setText(null);
        } else {
            if(item.getOnRoad() == item.getOffRoad()) {
                setText(String.valueOf(item.getOnRoad()));
                if(getTooltip() != null)
                    setTooltip(null);
            }  else {
                setText(item.getOnRoad() + "/" + item.getOffRoad());
                if(getTooltip() == null) {
                    setTooltip(new Tooltip("On road / Off road"));
                }
            }

        }
    }
}
