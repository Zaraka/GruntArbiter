package org.shadowrun.common.cells;

import javafx.scene.control.TableCell;
import org.shadowrun.models.Barrier;

public class ObjectCell extends TableCell<Barrier, Barrier> {

    @Override
    protected void updateItem(Barrier item, boolean empty) {
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
