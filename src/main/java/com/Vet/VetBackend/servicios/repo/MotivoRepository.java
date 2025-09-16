// src/main/java/com/Vet/VetBackend/servicios/repo/MotivoRepository.java
package com.Vet.VetBackend.servicios.repo;

import com.Vet.VetBackend.servicios.domain.Motivo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MotivoRepository extends JpaRepository<Motivo, Short> {
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Short id);
}
