package org.shadowrun.common.nodes.cells;


import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import org.shadowrun.models.Character;

public class TurnTableCell extends TableCell<Character, Integer>  {

    private int turn;

    public TurnTableCell(int turn) {
        this.turn = turn * 10;
    }

    @Override
    protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);

        if(!empty && item != null) {
            int result = item - turn;
            setText(String.valueOf(result));
            ObservableList<String> cellClasses = getStyleClass();
            if(result < 1) {
                cellClasses.add("turn-cell-empty");
            } else {
                cellClasses.add("turn-cell-full");
            }
        } else {
            setText(null);
        }
    }
}
