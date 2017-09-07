package org.shadowrun.common.nodes.cells;


import javafx.scene.control.TableCell;
import org.shadowrun.models.Character;

public class TurnTableCell extends TableCell<Character, Integer>  {

    @Override
    protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);

        if(!empty && item != null) {
            setText(String.valueOf(item));
            if(item < 1) {
                setStyle("-fx-text-fill: lightgray");
            } else {
                setStyle(null);
            }
        } else {
            setText(null);
        }
    }
}
