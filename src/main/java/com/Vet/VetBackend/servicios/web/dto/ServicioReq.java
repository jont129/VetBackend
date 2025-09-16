// src/main/java/com/Vet/VetBackend/servicios/web/dto/ServicioReq.java
package com.Vet.VetBackend.servicios.web.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServicioReq {
    @NotBlank @Size(max = 120)
    private String nombre;

    @Size(max = 250)
    private String descripcion;

    @PositiveOrZero(message = "precio_base >= 0")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal precioBase;

    /** Opcional. Si viene null, el servicio se crea activo por defecto. */
    private Boolean activo;
}
