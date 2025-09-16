// src/main/java/com/Vet/VetBackend/servicios/web/dto/MotivoServicioReq.java
package com.Vet.VetBackend.servicios.web.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MotivoServicioReq {
    @NotNull @Min(1)
    private Short motivoId;

    @NotNull @Min(1)
    private Long servicioId;
}
