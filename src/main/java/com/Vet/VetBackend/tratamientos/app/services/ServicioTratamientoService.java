package com.Vet.VetBackend.tratamientos.app.services;

import com.Vet.VetBackend.tratamientos.web.dto.ServicioTratamientoReq;
import com.Vet.VetBackend.tratamientos.web.dto.ServicioTratamientoRes;

import java.util.List;

public interface ServicioTratamientoService {
    List<ServicioTratamientoRes> listarPorServicio(Long servicioId);
    // NUEVO en la interfaz
    List<ServicioTratamientoRes> listarTodos();
    ServicioTratamientoRes asociar(ServicioTratamientoReq req);
    void eliminarAsociacion(Long id); // id de la relación
}


