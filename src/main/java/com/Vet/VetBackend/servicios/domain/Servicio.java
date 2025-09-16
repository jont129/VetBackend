package com.Vet.VetBackend.servicios.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "servicio",
        uniqueConstraints = @UniqueConstraint(name = "uk_servicio_nombre", columnNames = "nombre"),
        indexes = {
                @Index(name = "idx_servicio_nombre", columnList = "nombre"),
                @Index(name = "idx_servicio_activo_nombre", columnList = "activo,nombre")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 250)
    private String descripcion;

    @Column(name = "precio_base", precision = 12, scale = 2)
    private BigDecimal precioBase;

    @Column(nullable = false)
    private Boolean activo;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    private void prePersist() {
        if (activo == null) activo = Boolean.TRUE;
        if (nombre != null) nombre = nombre.trim().replaceAll("\\s+", " ");
    }

    @PreUpdate
    private void preUpdate() {
        if (nombre != null) nombre = nombre.trim().replaceAll("\\s+", " ");
    }
}
