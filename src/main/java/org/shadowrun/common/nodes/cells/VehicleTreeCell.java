package org.shadowrun.common.nodes.cells;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.TreeCell;
import org.shadowrun.models.VehiclePreset;

public class VehicleTreeCell extends TreeCell<VehiclePreset>{

    @Override
    protected void updateItem(VehiclePreset item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.getVehicle().getName());
            if(item.getIcon() != null && getGraphic() == null) {
                setGraphic(new FontAwesomeIconView(item.getIcon()));
            }
        }
    }
}
