package org.shadowrun.common.events;

import javafx.event.ActionEvent;
import org.shadowrun.models.Companion;

public class CompanionEvent extends ActionEvent {

    private Companion payload;

    public CompanionEvent(Companion companion) {
        payload = companion;
    }

    public Companion getPayload() {
        return payload;
    }
}
