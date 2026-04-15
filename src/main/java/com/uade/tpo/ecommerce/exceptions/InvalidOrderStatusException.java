package com.uade.tpo.ecommerce.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "La transición de estado de la orden no es válida")
public class InvalidOrderStatusException extends Exception {
	public InvalidOrderStatusException() {
		super();
	}
	public InvalidOrderStatusException(String message) {
		super(message);
	}
}
