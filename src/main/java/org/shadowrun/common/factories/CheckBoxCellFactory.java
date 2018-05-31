package org.shadowrun.common.factories;

import javafx.scene.control.TableCell;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;
import org.shadowrun.controllers.NewBattle;

public class CheckBoxCellFactory implements Callback {
    @Override
    public TableCell call(Object param) {
        CheckBoxTableCell<NewBattle.PickPlayer,Boolean> checkBoxCell = new CheckBoxTableCell<>();
        return checkBoxCell;
    }
}