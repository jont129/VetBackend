package com.Vet.VetBackend.tratamientos.app.implementations;

import com.Vet.VetBackend.tratamientos.app.services.ServicioTratamientoService;
import com.Vet.VetBackend.tratamientos.domain.ServicioTratamiento;
import com.Vet.VetBackend.tratamientos.repo.ServicioTratamientoRepository;
import com.Vet.VetBackend.tratamientos.web.dto.ServicioTratamientoReq;
import com.Vet.VetBackend.tratamientos.web.dto.ServicioTratamientoRes;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class ServicioTratamientoServiceImpl implements ServicioTratamientoService {

    private final ServicioTratamientoRepository repository;

    public ServicioTratamientoServiceImpl(ServicioTratamientoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ServicioTratamientoRes> listarPorServicio(Long servicioId) {
        return repository.findByServicioId(servicioId).stream()
                .map(ServicioTratamientoRes::fromEntity).toList();
    }

    @Override
    public ServicioTratamientoRes asociar(ServicioTratamientoReq req) {
        ServicioTratamiento st = new ServicioTratamiento();
        st.setServicioId(req.getServicioId());
        st.setTratamientoId(req.getTratamientoId());
        return ServicioTratamientoRes.fromEntity(repository.save(st));
    }

    @Override
    public void eliminarAsociacion(Long id) {
        repository.deleteById(id);
    }
    @Override
    public List<ServicioTratamientoRes> listarTodos() {
        return repository.findAll()
                .stream()
                .map(ServicioTratamientoRes::fromEntity)
                .toList();
    }

}
