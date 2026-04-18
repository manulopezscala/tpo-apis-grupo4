package com.uade.tpo.ecommerce.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "No tiene permisos para acceder a este recurso")
public class ForbiddenOperationException extends RuntimeException {

    public ForbiddenOperationException() {
        super("No tiene permisos para acceder a este recurso");
    }

    public ForbiddenOperationException(String message) {
        super(message);
    }
}