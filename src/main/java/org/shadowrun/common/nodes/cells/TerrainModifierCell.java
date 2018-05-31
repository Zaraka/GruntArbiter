package org.shadowrun.common.nodes.cells;

import javafx.scene.control.ListCell;
import org.shadowrun.common.constants.TerrainModifier;

public class TerrainModifierCell extends ListCell<TerrainModifier> {

    @Override
    protected void updateItem(TerrainModifier item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setText(null);
        } else {
            setText(item.getName() + " +" + String.valueOf(item.getModifier()));
        }
    }
}
