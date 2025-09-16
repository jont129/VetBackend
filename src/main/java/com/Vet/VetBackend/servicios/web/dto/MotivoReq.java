// src/main/java/com/Vet/VetBackend/servicios/web/dto/MotivoReq.java
package com.Vet.VetBackend.servicios.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MotivoReq {
    @NotBlank @Size(max = 60)
    private String nombre;
}
