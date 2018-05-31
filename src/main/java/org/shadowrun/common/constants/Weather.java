package org.shadowrun.common.constants;

import de.jensd.fx.glyphs.weathericons.WeatherIcon;

public enum Weather {
    DAY("Day", DayTime.DAY, null, true),
    DAY_SUNNY("Day sunny", DayTime.DAY, WeatherIcon.DAY_SUNNY, false),
    DAY_CLOUDY("Day cloudy", DayTime.DAY, WeatherIcon.DAY_CLOUDY, false),
    DAY_WINDY("Day windy", DayTime.DAY, WeatherIcon.DAY_WINDY, false),
    DAY_FOG("Day fog", DayTime.DAY, WeatherIcon.DAY_FOG, false),
    DAY_RAIN("Day rain", DayTime.DAY, WeatherIcon.DAY_RAIN, false),
    DAY_THUNDERSTORM("Day thunderstorm", DayTime.DAY, WeatherIcon.DAY_THUNDERSTORM, false),
    NIGHT("Night", DayTime.NIGHT, null, true),
    NIGHT_CLEAR("Night clear", DayTime.NIGHT, WeatherIcon.NIGHT_CLEAR, false),
    NIGHT_CLOUDY("Night cloudy", DayTime.NIGHT, WeatherIcon.NIGHT_CLOUDY, false),
    NIGHT_WINDY("Night windy", DayTime.NIGHT, WeatherIcon.NIGHT_CLOUDY_WINDY, false),
    NIGHT_FOG("Night fog", DayTime.NIGHT, WeatherIcon.NIGHT_FOG, false),
    NIGHT_RAIN("Night rain", DayTime.NIGHT, WeatherIcon.NIGHT_RAIN, false),
    NIGHT_THUNDERSTORM("Night thunderstorm", DayTime.NIGHT, WeatherIcon.NIGHT_THUNDERSTORM, false);

    public enum DayTime {
        DAY,
        NIGHT
    }

    private String name;
    private WeatherIcon icon;
    private DayTime dayTime;
    private boolean isCategory;

    Weather(String name, DayTime dayTime, WeatherIcon icon, boolean isCategory) {
        this.name = name;
        this.icon = icon;
        this.dayTime = dayTime;
        this.isCategory = isCategory;
    }

    public String getName() {
        return name;
    }

    public WeatherIcon getIcon() {
        return icon;
    }

    public DayTime getDayTime() {
        return dayTime;
    }

    public boolean isCategory() {
        return isCategory;
    }
}
