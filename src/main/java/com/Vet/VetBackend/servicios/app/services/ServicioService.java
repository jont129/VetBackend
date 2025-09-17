// src/main/java/com/Vet/VetBackend/servicios/app/services/ServicioService.java
package com.Vet.VetBackend.servicios.app.services;

import com.Vet.VetBackend.servicios.web.dto.ServicioReq;
import com.Vet.VetBackend.servicios.web.dto.ServicioRes;
import org.springframework.data.domain.Page;

/**
 * Caso de uso para gestión de Servicios.
 *
 * Reglas:
 * - Nombre único.
 * - Activación/desactivación lógica.
 *
 * @since 1.0
 */
public interface ServicioService {
    /**
     * Crea un servicio.
     * @param req comando con datos.
     * @return DTO creado.
     */
    ServicioRes crear(ServicioReq req);

    /**
     * Actualiza un servicio.
     * @param id  id del servicio.
     * @param req cambios.
     * @return DTO actualizado.
     */
    ServicioRes actualizar(Long id, ServicioReq req);

    /**
     * Activa o desactiva un servicio.
     * @param id id del servicio.
     * @param activo estado deseado.
     * @return DTO actualizado.
     */
    ServicioRes activar(Long id, boolean activo);

    /**
     * Obtiene un servicio por id.
     * @param id identificador.
     * @return DTO.
     */
    ServicioRes obtener(Long id);

    /**
     * Lista servicios con filtros y paginación.
     * @param q     filtro por nombre.
     * @param activo filtro por estado.
     * @param page  página base 0.
     * @param size  tamaño de página.
     * @return página de DTOs.
     */
    Page<ServicioRes> listar(String q, Boolean activo, int page, int size);
}
