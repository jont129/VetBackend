// src/main/java/com/Vet/VetBackend/servicios/web/dto/MotivoRes.java
package com.Vet.VetBackend.servicios.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "nombre" })
public class MotivoRes {
    private Short id;
    private String nombre;
}
