package org.shadowrun.common.nodes.cells;

import javafx.scene.control.TableCell;
import org.apache.commons.lang3.StringUtils;
import org.shadowrun.common.constants.VehicleChaseRole;
import org.shadowrun.controllers.VehicleCombat;

public class VehicleChaseRoleCell extends TableCell<VehicleCombat.VehicleValues, VehicleChaseRole> {
    @Override
    protected void updateItem(VehicleChaseRole item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setText(StringUtils.EMPTY);
        } else {
            switch(item) {
                case RUNNER:
                    setText("Runner");
                    break;
                case PURSUER:
                    setText("Pursuer");
                    break;
            }
        }
    }
}
