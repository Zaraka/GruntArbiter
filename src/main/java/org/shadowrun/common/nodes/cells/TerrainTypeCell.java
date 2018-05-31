package org.shadowrun.common.nodes.cells;

import javafx.scene.control.ListCell;
import org.shadowrun.common.constants.TerrainType;

public class TerrainTypeCell extends ListCell<TerrainType> {
    @Override
    protected void updateItem(TerrainType item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.getName());
            //setGraphic(new WeatherIconView(item.getIcon()));
        }
    }
}
