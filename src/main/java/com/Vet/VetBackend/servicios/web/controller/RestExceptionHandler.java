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

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> notFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> badRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err("BAD_REQUEST", ex.getMessage()));
    }

    /** Body JSON inválido o Content-Type incorrecto */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> badJson(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(err("BAD_JSON", "cuerpo inválido o Content-Type ausente/incorrecto"));
    }

    /** Path/query param con tipo incorrecto (p.ej., id no numérico) */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> typeMismatch(MethodArgumentTypeMismatchException ex) {
        String msg = "parámetro '" + ex.getName() + "' inválido";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err("TYPE_MISMATCH", msg));
    }

    /** Bean Validation en body */
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

    /** Bean Validation en params/path */
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

    /** Unicidad / FK */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> conflict(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(err("CONFLICT", "violación de integridad"));
    }

    /** Problemas de persistencia típicos (p.ej., falta AUTO_INCREMENT) */
    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<Map<String, Object>> persistence(PersistenceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(err("PERSISTENCE_ERROR", "no se pudo persistir: revise AUTO_INCREMENT o constraints"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> unhandled(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err("ERROR", "error inesperado"));
    }

    private Map<String, Object> err(String code, String msg) {
        return new LinkedHashMap<>() {{
            put("code", code);
            put("message", msg);
            put("timestamp", new Date());
        }};
    }
}
