package org.shadowrun.common.cells;

import javafx.scene.control.TableCell;
import org.shadowrun.models.Object;

public class ObjectCell extends TableCell<Object, Object> {

    @Override
    protected void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.getName());
            setGraphic(null);
        }
    }
}
