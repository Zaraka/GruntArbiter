package org.shadowrun.common;

import javafx.scene.control.TableCell;

public class TurnTableCell<T, E> extends TableCell {
    @Override
    protected void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);
        if(!empty && item != null) {
            setText(String.valueOf(item));
            if((int)item < 1) {
                setStyle("-fx-text-fill: lightgray");
            } else {
                setStyle(null);
            }
        } else {
            setText(null);
        }
    }
}
