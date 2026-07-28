package com.abdelkhalek.storehub.order.order.models;

public record Result<T> (T value, Throwable error, boolean isSuccess) {
    public static <T> Result<T> success(T value) {
        return new Result<>(value, null, true);
    }

    public static <T> Result<T> failure(Throwable error) {
        return new Result<>(null, error, false);
    }

}