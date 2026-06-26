package com.abdelkhalek.storehub.order.domain.models;

public class Result<T> {
    private final T value;
    private final Throwable error;
    private final boolean success;

    private Result(T value, Throwable error, boolean success) {
        this.value = value;
        this.error = error;
        this.success = success;
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(value, null, true);
    }

    public static <T> Result<T> failure(Throwable error) {
        return new Result<>(null, error, false);
    }

    public boolean isSuccess() {
        return success;
    }

    public T getValue() {
        return value;
    }

    public Throwable getError() {
        return error;
    }
}