package com.uade.tpo.ecommerce.exceptions;

public class InvalidOrderStatusException extends Exception {
    public InvalidOrderStatusException() {
        super();
    }
    public InvalidOrderStatusException(String message) {
        super(message);
    }
}