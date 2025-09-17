// src/main/java/com/Vet/VetBackend/servicios/app/services/MotivoService.java
package com.Vet.VetBackend.servicios.app.services;

import com.Vet.VetBackend.servicios.web.dto.MotivoReq;
import com.Vet.VetBackend.servicios.web.dto.MotivoRes;

import java.util.List;

/**
 * Caso de uso para gestión de Motivos.
 *
 * Operaciones:
 * - CRUD de lectura básica.
 * - Vinculación con Servicios.
 *
 * Contratos:
 * - Unicidad por nombre.
 * - Excepciones: IllegalArgumentException para validación; NoSuchElementException para inexistencia.
 *
 * @since 1.0
 */
public interface MotivoService {
    /**
     * Crea un motivo.
     * @param req comando con nombre.
     * @return DTO creado.
     */
    MotivoRes crear(MotivoReq req);

    /**
     * Actualiza un motivo existente.
     * @param id  id del motivo.
     * @param req comando con cambios.
     * @return DTO actualizado.
     */
    MotivoRes actualizar(Short id, MotivoReq req);

    /**
     * Obtiene un motivo por id.
     * @param id id del motivo.
     * @return DTO del motivo.
     */
    MotivoRes obtener(Short id);

    /**
     * Lista motivos.
     * @return colección de DTOs.
     */
    List<MotivoRes> listar();

    /**
     * Vincula motivo con servicio.
     * @param motivoId id del motivo.
     * @param servicioId id del servicio.
     */
    void vincular(Short motivoId, Long servicioId);

    /**
     * Quita la relación motivo-servicio.
     * @param motivoId id del motivo.
     * @param servicioId id del servicio.
     */
    void desvincular(Short motivoId, Long servicioId);
}
