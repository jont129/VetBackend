// src/main/java/com/Vet/VetBackend/servicios/repo/ServicioRepository.java
package com.Vet.VetBackend.servicios.repo;

import com.Vet.VetBackend.servicios.domain.Servicio;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;

import java.util.Optional;

public interface ServicioRepository extends JpaRepository<Servicio, Long>, JpaSpecificationExecutor<Servicio> {

    Optional<Servicio> findByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);

    Page<Servicio> findByActivo(Boolean activo, Pageable pageable);

    @Query("select s from Servicio s " +
            "where (:q is null or lower(s.nombre) like lower(concat('%', :q, '%'))) " +
            "and (:activo is null or s.activo = :activo)")
    Page<Servicio> search(String q, Boolean activo, Pageable pageable);
}
