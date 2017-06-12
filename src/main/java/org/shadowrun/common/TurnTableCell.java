package org.shadowrun.common;

import javafx.scene.control.TableCell;

public class TurnTableCell<Character, Integer> extends TableCell<Character, Integer> {
    @Override
    protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);
        if(!empty) {
            setText(String.valueOf(item));
            if(item != null && item < 1) {
                setStyle("-fx-text-fill: lightgray");
            } else {
                setStyle(null);
            }
        }
    }
}
