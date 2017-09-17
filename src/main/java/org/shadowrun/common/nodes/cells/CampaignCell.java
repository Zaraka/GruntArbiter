package org.shadowrun.common.nodes.cells;

import javafx.scene.control.ListCell;

import java.nio.file.Path;

public class CampaignCell extends ListCell<Path> {
    @Override
    protected void updateItem(Path item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setText(null);
        } else {
            setText(item.getFileName().toString());
        }
    }
}
