package com.uade.tpo.ecommerce.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uade.tpo.ecommerce.entity.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
/**
 * Escribe respuestas de error JSON estandarizadas directamente en la respuesta HTTP.
 */
public class ErrorResponseWriter {

    private final ObjectMapper objectMapper;

    /**
     * Crea el writer usando el ObjectMapper configurado por Spring.
     *
     * @param objectMapper mapper utilizado para serializar el cuerpo de error
     */
    public ErrorResponseWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Escribe en la respuesta un error con estado HTTP y mensaje normalizado.
     *
     * @param response respuesta HTTP donde se escribirá el cuerpo
     * @param status estado HTTP a devolver
     * @param message mensaje de error para el cliente
     * @throws IOException si ocurre un error de escritura en el stream de salida
     */
    public void write(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), new ErrorResponse(true, defaultMessage(message)));
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