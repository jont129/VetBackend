// src/main/java/com/Vet/VetBackend/servicios/web/controller/ServicioController.java
package com.Vet.VetBackend.servicios.web.controller;

import com.Vet.VetBackend.servicios.app.services.ServicioService;
import com.Vet.VetBackend.servicios.web.dto.ServicioReq;
import com.Vet.VetBackend.servicios.web.dto.ServicioRes;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/servicios", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class ServicioController {

    private final ServicioService svc;
    public ServicioController(ServicioService svc) { this.svc = svc; }

    /**
     * POST /api/servicios
     * Crea un servicio.
     * Body: { nombre, descripcion?, precioBase?, activo? }
     * Respuestas: 201 Created (Location: /api/servicios/{id}), 400, 409
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServicioRes> crear(@RequestBody @Validated ServicioReq req) {
        ServicioRes res = svc.crear(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(res.getId()).toUri();
        return ResponseEntity.created(location).body(res);
    }

    /**
     * PUT /api/servicios/{id}
     * Actualiza un servicio por id.
     * Body: { nombre, descripcion?, precioBase?, activo? }
     * Respuestas: 200 OK, 400, 404, 409
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServicioRes> actualizar(@PathVariable Long id, @RequestBody @Validated ServicioReq req) {
        return ResponseEntity.ok(svc.actualizar(id, req));
    }

    /**
     * PATCH /api/servicios/{id}/estado?activo=true|false
     * Activa/Desactiva (eliminación lógica).
     * Respuestas: 200 OK, 404
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<ServicioRes> estado(@PathVariable Long id, @RequestParam boolean activo) {
        return ResponseEntity.ok(svc.activar(id, activo));
    }

    /**
     * GET /api/servicios/{id}
     * Obtiene un servicio por id.
     * Respuestas: 200 OK, 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServicioRes> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(svc.obtener(id));
    }

    /**
     * GET /api/servicios?q=texto&activo=true|false&page=0&size=10
     * Lista paginada con filtros por nombre (like) y activo.
     * Respuestas: 200 OK
     */
    @GetMapping
    public ResponseEntity<Page<ServicioRes>> listar(@RequestParam(required = false) String q,
                                                    @RequestParam(required = false) Boolean activo,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(svc.listar(q, activo, page, size));
    }
}
