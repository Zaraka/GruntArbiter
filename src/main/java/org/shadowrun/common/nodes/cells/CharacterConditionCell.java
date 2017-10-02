package org.shadowrun.common.nodes.cells;

import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import org.apache.commons.lang3.StringUtils;
import org.shadowrun.common.constants.CharacterType;
import org.shadowrun.models.Character;

public class CharacterConditionCell extends TableCell<Character, Character> {

    @Override
    protected void updateItem(Character item, boolean empty) {
        super.updateItem(item, empty);

        setGraphic(null);
        StringBuilder builder = new StringBuilder(StringUtils.EMPTY);
        if (!empty) {
            if(item.getType().equals(CharacterType.ICE)) {
                if(tooltipProperty().isNull().get())
                    setTooltip(new Tooltip("ICE Matrix condition"));

                builder.append(item.getPhysicalMonitor().getCurrent());
            } else {
                if(tooltipProperty().isNull().get())
                    setTooltip(new Tooltip("Character condition Stun/Physical"));

                builder.append(item.getStunMonitor().getCurrent());
                builder.append("/");
                builder.append(item.getPhysicalMonitor().getCurrent());
            }
        }

        setText(builder.toString());
    }
}