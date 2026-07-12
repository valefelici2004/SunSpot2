package com.group3boot.sunspot.models;

public class CurrentUnits {
    private String time;
    private String interval;
    private String temperature_2m;
    private String weather_code;
    private String is_day;

    public CurrentUnits() {}

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getInterval() { return interval; }
    public void setInterval(String interval) { this.interval = interval; }
    public String getTemperature_2m() { return temperature_2m; }
    public void setTemperature_2m(String temperature_2m) { this.temperature_2m = temperature_2m; }
    public String getWeather_code() { return weather_code; }
    public void setWeather_code(String weather_code) { this.weather_code = weather_code; }
    public String getIs_day() { return is_day; }
    public void setIs_day(String is_day) { this.is_day = is_day; }
}
