package com.group3boot.sunspot.models;

public abstract class WeatherResult {

    private WeatherResult() {}

    public boolean isSuccess() {
        return this instanceof Success;
    }

    /**
     * Rappresenta un'operazione riuscita durante la chiamata
     * a Open-Meteo.
     */
    public static final class Success extends WeatherResult {
        private final WeatherResponse weatherResponse;

        public Success(WeatherResponse weatherResponse) {
            this.weatherResponse = weatherResponse;
        }

        public WeatherResponse getData() {
            return weatherResponse;
        }
    }

    /**
     * Rappresenta un errore avvenuto durante la chiamata
     * a Open-Meteo.
     */
    public static final class Error extends WeatherResult {
        private final String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}