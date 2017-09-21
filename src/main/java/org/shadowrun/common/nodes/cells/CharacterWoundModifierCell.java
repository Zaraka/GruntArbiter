package org.shadowrun.common.nodes.cells;

import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import org.shadowrun.models.Character;

public class CharacterWoundModifierCell extends TableCell<Character, Character> {

    @Override
    protected void updateItem(Character item, boolean empty) {
        super.updateItem(item, empty);

        setGraphic(null);
        ObservableList<String> cellClasses = getStyleClass();
        if(empty || item == null) {
            setText(null);
        } else {
            if(tooltipProperty().isNull().get())
                setTooltip(new Tooltip("Wound modifier"));

            int woundModifier = item.getPhysicalMonitor().countWoundModifier() +
                    item.getStunMonitor().countWoundModifier();

            setText(String.valueOf(woundModifier));

            if(woundModifier > 0) {
                cellClasses.add("wound-cell-hurt");
            } else {
                cellClasses.add("wound-cell-healthy");
            }



        }
    }
}