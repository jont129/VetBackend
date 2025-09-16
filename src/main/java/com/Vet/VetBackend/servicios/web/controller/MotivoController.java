// src/main/java/com/Vet/VetBackend/servicios/web/controller/MotivoController.java
package com.Vet.VetBackend.servicios.web.controller;

import com.Vet.VetBackend.servicios.app.services.MotivoService;
import com.Vet.VetBackend.servicios.web.dto.MotivoReq;
import com.Vet.VetBackend.servicios.web.dto.MotivoRes;
import com.Vet.VetBackend.servicios.web.dto.MotivoServicioReq;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/motivos", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class MotivoController {

    private final MotivoService svc;
    public MotivoController(MotivoService svc) { this.svc = svc; }

    /** POST /api/motivos  Body:{nombre} → 201 Created */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MotivoRes> crear(@RequestBody @Validated MotivoReq req) {
        MotivoRes res = svc.crear(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(res.getId()).toUri();
        return ResponseEntity.created(location).body(res);
    }

    /** PUT /api/motivos/{id}  Body:{nombre} → 200 OK */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MotivoRes> actualizar(@PathVariable("id") Short id,
                                                @RequestBody @Validated MotivoReq req) {
        return ResponseEntity.ok(svc.actualizar(id, req));
    }

    /** GET /api/motivos/{id} → 200 OK */
    @GetMapping("/{id}")
    public ResponseEntity<MotivoRes> obtener(@PathVariable("id") Short id) {
        return ResponseEntity.ok(svc.obtener(id));
    }

    /** GET /api/motivos → 200 OK */
    @GetMapping
    public ResponseEntity<List<MotivoRes>> listar() {
        return ResponseEntity.ok(svc.listar());
    }

    /** POST /api/motivos/vincular  Body:{motivoId,servicioId} → 204 */
    @PostMapping(value = "/vincular", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void vincular(@RequestBody @Validated MotivoServicioReq req) {
        svc.vincular(req.getMotivoId(), req.getServicioId());
    }

    /** DELETE /api/motivos/desvincular  Body:{motivoId,servicioId} → 204 */
    @DeleteMapping(value = "/desvincular", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desvincular(@RequestBody @Validated MotivoServicioReq req) {
        svc.desvincular(req.getMotivoId(), req.getServicioId());
    }
}
