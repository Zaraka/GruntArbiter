package org.shadowrun.common.cells;

import de.jensd.fx.glyphs.weathericons.WeatherIconView;
import javafx.scene.control.ListCell;
import org.shadowrun.common.constants.Weather;

public class WeatherCell extends ListCell<Weather> {

    @Override
    protected void updateItem(Weather item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.getName());
            setGraphic(new WeatherIconView(item.getIcon()));
        }
    }
}
