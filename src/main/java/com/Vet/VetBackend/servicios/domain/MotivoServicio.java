package com.Vet.VetBackend.servicios.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "motivo_servicio",
        uniqueConstraints = @UniqueConstraint(name = "uq_motivo_servicio",
                columnNames = {"motivo_id", "servicio_id"}),
        indexes = {
                @Index(name = "idx_ms_motivo", columnList = "motivo_id"),
                @Index(name = "idx_ms_servicio", columnList = "servicio_id")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MotivoServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "motivo_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_ms_motivo"))
    private Motivo motivo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "servicio_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_ms_servicio"))
    private Servicio servicio;
}
