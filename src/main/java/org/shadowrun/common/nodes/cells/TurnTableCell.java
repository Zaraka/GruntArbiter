package org.shadowrun.common.nodes.cells;


import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import org.shadowrun.models.Character;

public class TurnTableCell extends TableCell<Character, Integer>  {

    private static final String TURN_CELL_FULL = "turn-cell-full";
    private static final String TURN_CELL_EMPTY = "turn-cell-empty";

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
                cellClasses.remove(TURN_CELL_FULL);
                cellClasses.add(TURN_CELL_EMPTY);
            } else {
                cellClasses.remove(TURN_CELL_EMPTY);
                cellClasses.add(TURN_CELL_FULL);
            }
        } else {
            setText(null);
        }
    }
}
