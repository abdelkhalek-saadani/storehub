package com.abdelkhalek.storehub.order.domain.exceptions;

public class UnavailableException extends RuntimeException{
    public UnavailableException(String message) {
        super(message);
    }
}
