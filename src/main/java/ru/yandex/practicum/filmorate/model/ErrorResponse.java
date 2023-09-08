package ru.yandex.practicum.filmorate.model;

public class ErrorResponse {
    private final String error;
    private StackTraceElement[] stackTrace;

    public ErrorResponse(String error) {
        this.error = error;
    }


    public ErrorResponse(String error, StackTraceElement[] stackTrace) {
        this.error = error;
        this.stackTrace = stackTrace;
    }

    public String getError() {
        return error;
    }


    public StackTraceElement[] getStackTrace() {
        return stackTrace;
    }
}
