package com.Vet.VetBackend.tratamientos.app.implementations;

import com.Vet.VetBackend.tratamientos.app.services.TratamientoAplicadoService;
import com.Vet.VetBackend.tratamientos.domain.TratamientoAplicado;
import com.Vet.VetBackend.tratamientos.repo.TratamientoAplicadoRepository;
import com.Vet.VetBackend.tratamientos.web.dto.TratamientoAplicadoReq;
import com.Vet.VetBackend.tratamientos.web.dto.TratamientoAplicadoRes;
import org.springframework.stereotype.Service;


import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TratamientoAplicadoServiceImpl implements TratamientoAplicadoService {

    private final TratamientoAplicadoRepository repo;

    public TratamientoAplicadoServiceImpl(TratamientoAplicadoRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<TratamientoAplicadoRes> listarPorCita(Long citaId) {
        return repo.findByCitaId(citaId)
                .stream().map(TratamientoAplicadoRes::fromEntity).toList();
    }

    @Override
    public List<TratamientoAplicadoRes> listarPorHistorial(Long historialId) {
        return repo.findByHistorialId(historialId)
                .stream().map(TratamientoAplicadoRes::fromEntity).toList();
    }

    @Override
    public TratamientoAplicadoRes registrar(TratamientoAplicadoReq req) {
        TratamientoAplicado entity = new TratamientoAplicado();
        entity.setCitaId(req.getCitaId());
        entity.setTratamientoId(req.getTratamientoId());
        entity.setVeterinarioId(req.getVeterinarioId());
        entity.setEstado("Pendiente");
        entity.setObservaciones(req.getObservaciones());
        return TratamientoAplicadoRes.fromEntity(repo.save(entity));
    }

    @Override
    public TratamientoAplicadoRes actualizarEstado(Long id, String estado) {
        TratamientoAplicado entity = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("TratamientoAplicado no encontrado"));
        entity.setEstado(estado);
        return TratamientoAplicadoRes.fromEntity(repo.save(entity));
    }

    @Override
    public TratamientoAplicadoRes agregarObservaciones(Long id, String observaciones) {
        TratamientoAplicado entity = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("TratamientoAplicado no encontrado"));
        entity.setObservaciones(observaciones);
        return TratamientoAplicadoRes.fromEntity(repo.save(entity));
    }
}