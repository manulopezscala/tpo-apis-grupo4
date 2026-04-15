package com.uade.tpo.ecommerce.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uade.tpo.ecommerce.entity.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ErrorResponseWriter {

    private final ObjectMapper objectMapper;

    public ErrorResponseWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void write(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), new ErrorResponse(true, defaultMessage(message)));
    }

    private String defaultMessage(String message) {
        return (message == null || message.isBlank()) ? "Unexpected error" : message;
    }
}