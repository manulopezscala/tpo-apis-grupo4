package com.uade.tpo.ecommerce.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "El usuario ya tiene un carrito activo")
public class ActiveCartAlreadyExistsException extends Exception {
}
