// src/main/java/com/Vet/VetBackend/servicios/repo/ServicioRepository.java
package com.Vet.VetBackend.servicios.repo;

import com.Vet.VetBackend.servicios.domain.Servicio;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;

import java.util.Optional;

/**
 * Repositorio JPA para {@link Servicio}.
 *
 * Incluye búsqueda por nombre, estado y especificaciones JPA.
 *
 * @since 1.0
 */
public interface ServicioRepository extends JpaRepository<Servicio, Long>, JpaSpecificationExecutor<Servicio> {

    /**
     * Busca un servicio por nombre exacto sin importar mayúsculas.
     *
     * @param nombre nombre a buscar.
     * @return Optional con el servicio si existe.
     */
    Optional<Servicio> findByNombreIgnoreCase(String nombre);

    /**
     * Verifica existencia por nombre sin importar mayúsculas.
     *
     * @param nombre nombre a verificar.
     * @return true si existe, false si no.
     */
    boolean existsByNombreIgnoreCase(String nombre);

    /**
     * Página de servicios filtrados por estado.
     *
     * @param activo   estado deseado.
     * @param pageable paginación.
     * @return página de servicios.
     */
    Page<Servicio> findByActivo(Boolean activo, Pageable pageable);

    /**
     * Búsqueda por texto y estado con paginación.
     *
     * @param q       texto a buscar en nombre (opcional).
     * @param activo  estado (opcional).
     * @param pageable paginación.
     * @return página de servicios.
     */
    @Query("select s from Servicio s " +
            "where (:q is null or lower(s.nombre) like lower(concat('%', :q, '%'))) " +
            "and (:activo is null or s.activo = :activo)")
    Page<Servicio> search(String q, Boolean activo, Pageable pageable);
}
