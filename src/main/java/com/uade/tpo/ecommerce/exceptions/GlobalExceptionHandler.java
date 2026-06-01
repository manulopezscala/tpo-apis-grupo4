package com.uade.tpo.ecommerce.exceptions;

import com.uade.tpo.ecommerce.entity.dto.CustomResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Maneja de forma centralizada las excepciones de la API y devuelve
 * respuestas de error consistentes para el cliente.
 */
// TODO: Re-enable @ControllerAdvice after fixing springdoc-openapi compatibility
// @ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Agrupa errores de entrada del cliente y responde con HTTP 400.
     *
     * @param ex excepción capturada durante el binding o la validación de la request
     * @param request request HTTP actual
     * @return cuerpo de error normalizado con estado 400
     */
    @ExceptionHandler({
        MethodArgumentTypeMismatchException.class,
        MissingPathVariableException.class,
        HttpMessageNotReadableException.class,
        MissingServletRequestParameterException.class,
        IllegalArgumentException.class,
        InvalidOrderStatusException.class
    })
    public ResponseEntity<CustomResponse> handleBadRequest(Exception ex, HttpServletRequest request) {
        if (ex instanceof MethodArgumentTypeMismatchException mismatch) {
            String paramName = mismatch.getName();
            Object invalidValue = mismatch.getValue();
            String message = "El parametro '" + paramName + "' debe ser numerico";

            if (invalidValue != null && invalidValue.toString().startsWith(":")) {
                message = "El parametro de path '" + paramName + "' es invalido. Reemplace ':" + paramName + "' por un numero";
            }

            return buildError(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
        }

        if (ex instanceof MissingPathVariableException missingPathVariable) {
            String message = "Falta el parametro de path '" + missingPathVariable.getVariableName() + "'";
            return buildError(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
        }

        if (ex instanceof MissingServletRequestParameterException missingParam) {
            String message = "Falta el parametro requerido '" + missingParam.getParameterName() + "'";
            return buildError(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
        }

        if (ex instanceof HttpMessageNotReadableException) {
            return buildError(HttpStatus.BAD_REQUEST, "El body de la solicitud tiene un formato invalido", request.getRequestURI());
        }

        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    /**
     * Maneja credenciales inválidas al autenticar.
     *
     * @param ex excepción de credenciales inválidas
     * @param request request HTTP actual
     * @return cuerpo de error con estado 401
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CustomResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        return buildError(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas", request.getRequestURI());
    }

    /**
     * Maneja errores de autenticación no cubiertos por casos más específicos.
     *
     * @param ex excepción de autenticación
     * @param request request HTTP actual
     * @return cuerpo de error con estado 401
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CustomResponse> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        return buildError(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
    }

    /**
     * Maneja excepciones de aplicación y respeta @ResponseStatus cuando esté presente.
     *
     * @param ex excepción no controlada por handlers más específicos
     * @param request request HTTP actual
     * @return cuerpo de error con estado definido por @ResponseStatus o 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponse> handleApplicationException(Exception ex, HttpServletRequest request) {
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            String message = responseStatus.reason().isBlank() ? ex.getMessage() : responseStatus.reason();
            return buildError(HttpStatus.valueOf(responseStatus.code().value()), message, request.getRequestURI());
        }
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI());
    }

    /**
     * Fallback para errores de runtime no interceptados por otros handlers.
     *
     * @param ex excepción de runtime
     * @param request request HTTP actual
     * @return cuerpo de error con estado 500
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CustomResponse> handleRuntimeError(RuntimeException ex, HttpServletRequest request) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI());
    }

    /**
     * Construye el payload estándar de error utilizado por la API.
     *
     * @param status código HTTP a devolver
     * @param message mensaje de error para el cliente
     * @param path ruta solicitada donde ocurrió el error
     * @return respuesta HTTP con el cuerpo de error normalizado
     */
    private ResponseEntity<CustomResponse> buildError(HttpStatus status, String message, String path) {
        CustomResponse body = new CustomResponse(false, defaultMessage(message));
        return ResponseEntity.status(status).body(body);
    }

    /**
     * Devuelve un mensaje por defecto cuando no se recibió uno válido.
     *
     * @param message mensaje recibido
     * @return mensaje original o "Unexpected error" si está vacío
     */
    private String defaultMessage(String message) {
        return (message == null || message.isBlank()) ? "Unexpected error" : message;
    }
}
