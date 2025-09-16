package com.Vet.VetBackend.servicios.repo;

import com.Vet.VetBackend.servicios.domain.Motivo;
import com.Vet.VetBackend.servicios.domain.MotivoServicio;
import com.Vet.VetBackend.servicios.domain.Servicio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MotivoServicioRepository extends JpaRepository<MotivoServicio, Long> {

    boolean existsByMotivoIdAndServicioId(Short motivoId, Long servicioId);

    /** Devuelve número de filas eliminadas. */
    long deleteByMotivoIdAndServicioId(Short motivoId, Long servicioId);

    // ---- Servicios por motivo ----
    @SuppressWarnings("unused")
    @Query("select ms.servicio from MotivoServicio ms where ms.motivo.id = :motivoId")
    List<Servicio> findServiciosByMotivoId(@Param("motivoId") Short motivoId);

    @SuppressWarnings("unused")
    @Query("select ms.servicio from MotivoServicio ms " +
            "where ms.motivo.id = :motivoId and ms.servicio.activo = true")
    List<Servicio> findServiciosActivosByMotivoId(@Param("motivoId") Short motivoId);

    @SuppressWarnings("unused")
    @Query("select ms.servicio from MotivoServicio ms " +
            "where ms.motivo.id = :motivoId and (:q is null or lower(ms.servicio.nombre) like lower(concat('%', :q, '%')))")
    Page<Servicio> findServiciosByMotivoId(@Param("motivoId") Short motivoId,
                                           @Param("q") String q,
                                           Pageable pageable);

    // ---- Motivos por servicio ----
    @SuppressWarnings("unused")
    @Query("select ms.motivo from MotivoServicio ms where ms.servicio.id = :servicioId")
    List<Motivo> findMotivosByServicioId(@Param("servicioId") Long servicioId);

    @SuppressWarnings("unused")
    @Query("select ms.motivo from MotivoServicio ms " +
            "where ms.servicio.id = :servicioId and (:q is null or lower(ms.motivo.nombre) like lower(concat('%', :q, '%')))")
    Page<Motivo> findMotivosByServicioId(@Param("servicioId") Long servicioId,
                                         @Param("q") String q,
                                         Pageable pageable);
}
