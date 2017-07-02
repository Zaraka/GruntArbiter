package org.shadowrun.common.factories;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.shadowrun.common.WeatherCell;
import org.shadowrun.common.constants.Weather;


public class WeatherCellFactory implements Callback<ListView<Weather>, ListCell<Weather>> {
    @Override
    public ListCell<Weather> call(ListView<Weather> param) {
        return new WeatherCell();
    }
}
