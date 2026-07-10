package com.group3boot.sunspot.models;

import java.util.List;

public abstract class SpotResult {

    private SpotResult() {}

    public boolean isSuccess() {
        return this instanceof Success;
    }

    /**
     * Rappresenta un'operazione riuscita durante l'interazione
     * con Firestore o con il database locale.
     */
    public static final class Success extends SpotResult {
        private final List<Spot> spotList;

        public Success(List<Spot> spotList) {
            this.spotList = spotList;
        }

        public List<Spot> getData() {
            return spotList;
        }
    }

    /**
     * Rappresenta un errore avvenuto durante l'interazione
     * con Firestore o con il database locale.
     */
    public static final class Error extends SpotResult {
        private final String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}