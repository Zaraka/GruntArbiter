package org.shadowrun.common.nodes.cells;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import org.shadowrun.controllers.ControllerMain;

import java.nio.file.Path;

public class CampaignCell extends ListCell<Path> {

    private ControllerMain controllerMain;

    public CampaignCell(ControllerMain controllerMain) {
        this.controllerMain = controllerMain;
        MenuItem openCampaign = new MenuItem("Open campaign");
        openCampaign.setOnAction(event -> {
            controllerMain.closeCampaignOnAction();
            controllerMain.openCampaign(itemProperty().get().toFile());
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
