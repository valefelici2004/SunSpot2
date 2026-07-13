package com.group3boot.sunspot.util;

public class WeatherUtil {

    public static String getWeatherDescription(int code) {
        if (code == 0) return "Sereno";
        if (code == 1 || code == 2 || code == 3) return "Nuvoloso";
        if (code == 45 || code == 48) return "Nebbia";
        if (code >= 51 && code <= 57) return "Pioviggine";
        if (code >= 61 && code <= 67) return "Pioggia";
        if (code >= 71 && code <= 77) return "Neve";
        if (code >= 80 && code <= 82) return "Rovesci";
        if (code >= 95 && code <= 99) return "Temporale";
        return "Sconosciuto";
    }

    public static String formatTime(String isoDateTime) {
        if (isoDateTime == null || !isoDateTime.contains("T")) return "";
        return isoDateTime.split("T")[1];
    }
}