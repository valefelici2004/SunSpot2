package com.group3boot.sunspot.models;

public class CurrentWeather {
    private String time;
    private int interval;
    private double temperature_2m;
    private int weather_code;
    private int is_day;

    public CurrentWeather() {}

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public double getTemperature_2m() { return temperature_2m; }
    public void setTemperature_2m(double temperature_2m) { this.temperature_2m = temperature_2m; }
    public int getWeather_code() { return weather_code; }
    public void setWeather_code(int weather_code) { this.weather_code = weather_code; }
    public int getIs_day() { return is_day; }
    public void setIs_day(int is_day) { this.is_day = is_day; }
}
