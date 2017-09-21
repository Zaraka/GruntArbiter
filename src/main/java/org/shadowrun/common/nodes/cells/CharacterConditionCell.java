package org.shadowrun.common.nodes.cells;

import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import org.apache.commons.lang3.StringUtils;
import org.shadowrun.models.Character;

public class CharacterConditionCell extends TableCell<Character, Character> {

    @Override
    protected void updateItem(Character item, boolean empty) {
        super.updateItem(item, empty);

        setGraphic(null);
        StringBuilder builder = new StringBuilder(StringUtils.EMPTY);
        if (!empty) {
            if(tooltipProperty().isNull().get())
                setTooltip(new Tooltip("Character condition Stun/Physical"));

            builder.append(item.getStunMonitor().currentProperty().get());
            builder.append("/");
            builder.append(item.getPhysicalMonitor().currentProperty().get());
        }

        setText(builder.toString());
    }
}