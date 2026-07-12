package com.group3boot.sunspot.models;

import java.util.List;

public class DailyWeather {
    private List<String> time;
    private List<String> sunrise;
    private List<String> sunset;

    public DailyWeather() {}

    public List<String> getSunrise() { return sunrise; }
    public void setSunrise(List<String> sunrise) { this.sunrise = sunrise; }
    public List<String> getSunset() { return sunset; }
    public void setSunset(List<String> sunset) { this.sunset = sunset; }
    public List<String> getTime() { return time; }
    public void setTime(List<String> time) { this.time = time; }
}
