package org.shadowrun.common.nodes.cells;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.ListCell;
import org.shadowrun.common.constants.CompanionType;

public class CompanionTypeCell extends ListCell<CompanionType> {
    @Override
    protected void updateItem(CompanionType item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.getName());
            setGraphic(new FontAwesomeIconView(item.getIcon()));
        }
    }
}
