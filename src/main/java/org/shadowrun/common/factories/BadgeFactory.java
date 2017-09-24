package org.shadowrun.common.factories;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import org.shadowrun.common.constants.CssClasses;

public class BadgeFactory {
    public static Label createBadge(String text, String tooltip, CssClasses classes) {
        Label result = new Label(text);
        if(tooltip != null) {
            result.setTooltip(new Tooltip(tooltip));
        }

        ObservableList<String> labelClassList = result.getStyleClass();
        labelClassList.add("badge");
        labelClassList.add("badge-" + classes.name().toLowerCase());

        return result;
    }
}
