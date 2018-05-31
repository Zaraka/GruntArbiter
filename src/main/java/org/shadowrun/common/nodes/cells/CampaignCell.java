package org.shadowrun.common.nodes.cells;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import org.shadowrun.controllers.Main;

import java.nio.file.Path;

public class CampaignCell extends ListCell<Path> {

    private Main main;

    public CampaignCell(Main main) {
        this.main = main;
        MenuItem openCampaign = new MenuItem("Open campaign");
        openCampaign.setOnAction(event -> {
            main.closeCampaignOnAction();
            main.openCampaign(itemProperty().get().toFile());
        });
        ContextMenu contextMenu = new ContextMenu(openCampaign);

        itemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                setContextMenu(contextMenu);
            } else {
                setContextMenu(null);
            }
        });

    }

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
