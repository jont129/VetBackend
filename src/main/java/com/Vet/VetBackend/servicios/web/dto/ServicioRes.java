// src/main/java/com/Vet/VetBackend/servicios/web/dto/ServicioRes.java
package com.Vet.VetBackend.servicios.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id","nombre","descripcion","precioBase","activo","createdAt","updatedAt" })
public class ServicioRes {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precioBase;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
