// src/main/java/com/Vet/VetBackend/servicios/app/services/ServicioService.java
package com.Vet.VetBackend.servicios.app.services;

import com.Vet.VetBackend.servicios.web.dto.ServicioReq;
import com.Vet.VetBackend.servicios.web.dto.ServicioRes;
import org.springframework.data.domain.Page;

public interface ServicioService {
    ServicioRes crear(ServicioReq req);
    ServicioRes actualizar(Long id, ServicioReq req);
    ServicioRes activar(Long id, boolean activo);
    ServicioRes obtener(Long id);
    Page<ServicioRes> listar(String q, Boolean activo, int page, int size);
}
