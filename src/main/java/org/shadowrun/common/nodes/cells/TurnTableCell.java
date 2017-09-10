package org.shadowrun.common.nodes.cells;


import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import org.shadowrun.models.Character;

public class TurnTableCell extends TableCell<Character, Integer>  {

    @Override
    protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);

        if(!empty && item != null) {
            setText(String.valueOf(item));
            ObservableList<String> cellClasses = getStyleClass();
            if(item < 1) {
                cellClasses.add("turn-cell-empty");
            } else {
                cellClasses.add("turn-cell-full");
            }
        } else {
            setText(null);
        }
    }
}
