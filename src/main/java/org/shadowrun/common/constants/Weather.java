package org.shadowrun.common.constants;


import de.jensd.fx.glyphs.weathericons.WeatherIcon;

public enum Weather {
    DAY_SUNNY("Day sunny", WeatherIcon.DAY_SUNNY),
    DAY_CLOUDY("Day cloudy", WeatherIcon.DAY_CLOUDY),
    DAY_WINDY("Day windy", WeatherIcon.DAY_WINDY),
    DAY_FOG("Day fog", WeatherIcon.DAY_FOG),
    DAY_RAIN("Day rain", WeatherIcon.DAY_RAIN),
    DAY_THUNDERSTORM("Day thunderstorm", WeatherIcon.DAY_THUNDERSTORM),
    NIGHT_CLEAR("Night clear", WeatherIcon.NIGHT_CLEAR),
    NIGHT_CLOUDY("Night cloudy", WeatherIcon.NIGHT_CLOUDY),
    NIGHT_WINDY("Night windy", WeatherIcon.NIGHT_CLOUDY_WINDY),
    NIGHT_FOG("Night fog", WeatherIcon.NIGHT_FOG),
    NIGHT_RAIN("Night rain", WeatherIcon.NIGHT_RAIN),
    NIGHT_THUNDERSTORM("Night thunderstorm", WeatherIcon.NIGHT_THUNDERSTORM);

    private String name;
    private WeatherIcon icon;

    Weather(String name, WeatherIcon icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public WeatherIcon getIcon() {
        return icon;

    }
}
