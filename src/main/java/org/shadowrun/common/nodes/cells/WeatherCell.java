package org.shadowrun.common.nodes.cells;

import de.jensd.fx.glyphs.weathericons.WeatherIconView;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import org.shadowrun.common.constants.Weather;

public class WeatherCell extends ListCell<Weather> {

    private static final String COMBO_BOX_PRIMARY = "comboBox-primary";

    @Override
    protected void updateItem(Weather item, boolean empty) {
        super.updateItem(item, empty);

        ObservableList<String> cellClasses = getStyleClass();
        if(empty) {
            setText(null);
            setGraphic(null);
            cellClasses.remove(COMBO_BOX_PRIMARY);
        } else {
            setText(item.getName());
            if(item.getIcon() != null)
                setGraphic(new WeatherIconView(item.getIcon()));
            if(item.isCategory())
                cellClasses.add(COMBO_BOX_PRIMARY);
            else
                cellClasses.remove(COMBO_BOX_PRIMARY);
            setDisabled(item.isCategory());
        }
    }
}
