package org.shadowrun.common.nodes.cells;

import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import org.shadowrun.common.constants.NoisePreset;

public class NoisePresetCell extends ListCell<NoisePreset> {

    public static final String COMBO_BOX_PRIMARY = "comboBox-primary";

    @Override
    protected void updateItem(NoisePreset item, boolean empty) {
        super.updateItem(item, empty);

        ObservableList<String> cellClasses = getStyleClass();
        if(empty) {
            setText(null);
        } else {
            if(item.isCategory()) {
                setText(item.getName());
                cellClasses.add(COMBO_BOX_PRIMARY);
            } else {
                setText(item.getNoise() + " " + item.getName());
                cellClasses.remove(COMBO_BOX_PRIMARY);
            }
            setDisable(item.isCategory());
        }
    }
}
