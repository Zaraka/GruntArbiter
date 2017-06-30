package org.shadowrun.common.factories;

import javafx.scene.control.TableCell;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;
import org.shadowrun.controllers.ControllerNewBattle;

public class CheckBoxCellFactory implements Callback {
    @Override
    public TableCell call(Object param) {
        CheckBoxTableCell<ControllerNewBattle.PickPlayer,Boolean> checkBoxCell = new CheckBoxTableCell<>();
        return checkBoxCell;
    }
}