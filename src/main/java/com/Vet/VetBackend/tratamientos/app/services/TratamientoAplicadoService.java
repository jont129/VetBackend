package com.Vet.VetBackend.tratamientos.app.services;

import com.Vet.VetBackend.tratamientos.web.dto.TratamientoAplicadoReq;
import com.Vet.VetBackend.tratamientos.web.dto.TratamientoAplicadoRes;


import java.util.List;

public interface TratamientoAplicadoService {

    List<TratamientoAplicadoRes> listarPorCita(Long citaId);

    List<TratamientoAplicadoRes> listarPorHistorial(Long historialId);

    TratamientoAplicadoRes registrar(TratamientoAplicadoReq req);

    TratamientoAplicadoRes actualizarEstado(Long id, String estado);

    TratamientoAplicadoRes agregarObservaciones(Long id, String observaciones);
}



