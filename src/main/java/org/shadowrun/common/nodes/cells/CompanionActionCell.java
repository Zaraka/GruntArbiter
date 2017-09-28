package org.shadowrun.common.nodes.cells;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import org.shadowrun.common.events.CompanionEvent;
import org.shadowrun.common.utils.CSSUtils;
import org.shadowrun.models.Companion;

public class CompanionActionCell extends TableCell<Companion, Companion> {

    private EventHandler<CompanionEvent> spawnHandler;

    public CompanionActionCell(EventHandler<CompanionEvent> spawnHandler) {
        this.spawnHandler = spawnHandler;
    }

    @Override
    protected void updateItem(Companion item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setText(null);
            setGraphic(null);
        } else {

            Button spawnButton = new Button("Spawn");
            spawnButton.setOnAction(event -> {
                spawnHandler.handle(new CompanionEvent(item));
            });
            spawnButton.getStyleClass().add(CSSUtils.BUTTON_PRIMARY);
            setText(null);
            setGraphic(spawnButton);
        }
    }
}
