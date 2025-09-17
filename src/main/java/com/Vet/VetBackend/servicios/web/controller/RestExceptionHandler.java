// src/main/java/com/Vet/VetBackend/servicios/web/controller/RestExceptionHandler.java
package com.Vet.VetBackend.servicios.web.controller;

import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.*;

/**
 * Mapeo centralizado de excepciones a respuestas JSON.
 *
 * Formato:
 * {
 *   "code": "ERROR_CODE",
 *   "message": "detalle",
 *   "timestamp": ISO_DATE
 * }
 *
 * @since 1.0
 */
@RestControllerAdvice
public class RestExceptionHandler {

    /**
     * 404 Not Found para recursos inexistentes.
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> notFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err("NOT_FOUND", ex.getMessage()));
    }

    /**
     * 400 Bad Request para errores de validación de negocio.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> badRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err("BAD_REQUEST", ex.getMessage()));
    }

    /**
     * 400 por JSON inválido o Content-Type incorrecto.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> badJson(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(err("BAD_JSON", "cuerpo inválido o Content-Type ausente/incorrecto"));
    }

    /**
     * 400 por tipos incorrectos en path/query.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> typeMismatch(MethodArgumentTypeMismatchException ex) {
        String msg = "parámetro '" + ex.getName() + "' inválido";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err("TYPE_MISMATCH", msg));
    }

    /**
     * 400 por Bean Validation en request body.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> bodyValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> payload = err("VALIDATION_ERROR", "entrada inválida");
        payload.put("fields", ex.getBindingResult().getFieldErrors().stream()
                .map(f -> Map.of(
                        "field", f.getField(),
                        "message", Objects.toString(f.getDefaultMessage(), "inválido")
                ))
                .toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payload);
    }

    /**
     * 400 por Bean Validation en parámetros/path.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> paramValidation(ConstraintViolationException ex) {
        Map<String, Object> payload = err("VALIDATION_ERROR", "parámetros inválidos");
        payload.put("violations", ex.getConstraintViolations().stream()
                .map(v -> Map.of(
                        "param", v.getPropertyPath().toString(),
                        "message", Objects.toString(v.getMessage(), "inválido")
                ))
                .toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payload);
    }

    /**
     * 409 Conflict por unicidad o restricciones FK.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> conflict(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(err("CONFLICT", "violación de integridad"));
    }

    /**
     * 400 por errores de persistencia comunes.
     */
    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<Map<String, Object>> persistence(PersistenceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(err("PERSISTENCE_ERROR", "no se pudo persistir: revise AUTO_INCREMENT o constraints"));
    }

    /**
     * 500 para errores no manejados.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> unhandled(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err("ERROR", "error inesperado"));
    }

    /** Construye payload de error estándar. */
    private Map<String, Object> err(String code, String msg) {
        return new LinkedHashMap<>() {{
            put("code", code);
            put("message", msg);
            put("timestamp", new Date());
        }};
    }
}
