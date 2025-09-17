// src/main/java/com/Vet/VetBackend/servicios/repo/MotivoServicioRepository.java
package com.Vet.VetBackend.servicios.repo;

import com.Vet.VetBackend.servicios.domain.Motivo;
import com.Vet.VetBackend.servicios.domain.MotivoServicio;
import com.Vet.VetBackend.servicios.domain.Servicio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositorio JPA para la relación {@link MotivoServicio}.
 *
 * Proporciona utilidades para:
 * - Existencia y eliminación de vínculos motivo-servicio.
 * - Consultas de servicios por motivo.
 * - Consultas de motivos por servicio.
 *
 * @since 1.0
 */
public interface MotivoServicioRepository extends JpaRepository<MotivoServicio, Long> {

    /**
     * Verifica si existe el vínculo motivo-servicio.
     *
     * @param motivoId   id del motivo.
     * @param servicioId id del servicio.
     * @return true si existe, false si no.
     */
    boolean existsByMotivoIdAndServicioId(Short motivoId, Long servicioId);

    /**
     * Elimina el vínculo motivo-servicio.
     *
     * @param motivoId   id del motivo.
     * @param servicioId id del servicio.
     * @return número de filas eliminadas.
     */
    long deleteByMotivoIdAndServicioId(Short motivoId, Long servicioId);

    // ---- Servicios por motivo ----

    /**
     * Lista servicios vinculados a un motivo.
     *
     * @param motivoId id del motivo.
     * @return lista de servicios.
     */
    @SuppressWarnings("unused")
    @Query("select ms.servicio from MotivoServicio ms where ms.motivo.id = :motivoId")
    List<Servicio> findServiciosByMotivoId(@Param("motivoId") Short motivoId);

    /**
     * Lista servicios activos vinculados a un motivo.
     *
     * @param motivoId id del motivo.
     * @return lista de servicios activos.
     */
    @SuppressWarnings("unused")
    @Query("select ms.servicio from MotivoServicio ms " +
            "where ms.motivo.id = :motivoId and ms.servicio.activo = true")
    List<Servicio> findServiciosActivosByMotivoId(@Param("motivoId") Short motivoId);

    /**
     * Página de servicios por motivo con filtro opcional por nombre.
     *
     * @param motivoId id del motivo.
     * @param q        texto a buscar en nombre (opcional).
     * @param pageable paginación.
     * @return página de servicios.
     */
    @SuppressWarnings("unused")
    @Query("select ms.servicio from MotivoServicio ms " +
            "where ms.motivo.id = :motivoId and (:q is null or lower(ms.servicio.nombre) like lower(concat('%', :q, '%')))")
    Page<Servicio> findServiciosByMotivoId(@Param("motivoId") Short motivoId,
                                           @Param("q") String q,
                                           Pageable pageable);

    // ---- Motivos por servicio ----

    /**
     * Lista motivos vinculados a un servicio.
     *
     * @param servicioId id del servicio.
     * @return lista de motivos.
     */
    @SuppressWarnings("unused")
    @Query("select ms.motivo from MotivoServicio ms where ms.servicio.id = :servicioId")
    List<Motivo> findMotivosByServicioId(@Param("servicioId") Long servicioId);

    /**
     * Página de motivos por servicio con filtro opcional por nombre.
     *
     * @param servicioId id del servicio.
     * @param q          texto a buscar en nombre (opcional).
     * @param pageable   paginación.
     * @return página de motivos.
     */
    @SuppressWarnings("unused")
    @Query("select ms.motivo from MotivoServicio ms " +
            "where ms.servicio.id = :servicioId and (:q is null or lower(ms.motivo.nombre) like lower(concat('%', :q, '%')))")
    Page<Motivo> findMotivosByServicioId(@Param("servicioId") Long servicioId,
                                         @Param("q") String q,
                                         Pageable pageable);
}
