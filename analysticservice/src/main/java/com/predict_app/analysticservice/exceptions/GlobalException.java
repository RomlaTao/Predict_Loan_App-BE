package com.predict_app.analysticservice.exceptions;

public class GlobalException {
    public static class AnalysticNotFoundException extends RuntimeException {
        public AnalysticNotFoundException(String message) {
            super(message);
        }
    }

    public static class AnalysticDataNotFoundException extends RuntimeException {
        public AnalysticDataNotFoundException(String message) {
            super(message);
        }
    }

    public static class AnalysticDataNotValidException extends RuntimeException {
        public AnalysticDataNotValidException(String message) {
            super(message);
        }
    }
}
