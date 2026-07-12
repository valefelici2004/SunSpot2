package com.group3boot.sunspot.models;

public abstract class UserResult {

    private UserResult() {}

    public boolean isSuccess() {
        return this instanceof Success;
    }

    public static final class Success extends UserResult {
        private final User user;

        public Success(User user) {
            this.user = user;
        }

        public User getData() {
            return user;
        }
    }

    public static final class Error extends UserResult {
        private final String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}