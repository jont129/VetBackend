// src/main/java/com/Vet/VetBackend/servicios/domain/Motivo.java
package com.Vet.VetBackend.servicios.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "motivo",
        uniqueConstraints = @UniqueConstraint(name = "uk_motivo_nombre", columnNames = "nombre"),
        indexes = { @Index(name = "idx_motivo_nombre", columnList = "nombre") })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Motivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(columnDefinition = "TINYINT")
    private Short id;

    @Column(nullable = false, length = 60)
    private String nombre;

    @PrePersist
    private void prePersist() {
        if (nombre != null) nombre = nombre.trim().replaceAll("\\s+", " ");
    }

    @PreUpdate
    private void preUpdate() {
        if (nombre != null) nombre = nombre.trim().replaceAll("\\s+", " ");
    }
}
