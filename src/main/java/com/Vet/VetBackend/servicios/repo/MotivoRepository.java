// src/main/java/com/Vet/VetBackend/servicios/repo/MotivoRepository.java
package com.Vet.VetBackend.servicios.repo;

import com.Vet.VetBackend.servicios.domain.Motivo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para {@link Motivo}.
 *
 * Responsabilidad: acceso CRUD y verificaciones de unicidad por nombre.
 *
 * @since 1.0
 */
public interface MotivoRepository extends JpaRepository<Motivo, Short> {

    /**
     * Verifica si existe un motivo con el nombre dado (case-insensitive).
     *
     * @param nombre nombre a verificar.
     * @return true si existe, false en caso contrario.
     */
    boolean existsByNombreIgnoreCase(String nombre);

    /**
     * Verifica unicidad por nombre excluyendo un id específico.
     *
     * @param nombre nombre a verificar.
     * @param id     id a excluir.
     * @return true si existe otro registro con ese nombre, false si no.
     */
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Short id);
}
