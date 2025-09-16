// src/main/java/com/Vet/VetBackend/servicios/app/implementations/MotivoServiceImpl.java
package com.Vet.VetBackend.servicios.app.implementations;

import com.Vet.VetBackend.servicios.app.services.MotivoService;
import com.Vet.VetBackend.servicios.domain.Motivo;
import com.Vet.VetBackend.servicios.domain.MotivoServicio;
import com.Vet.VetBackend.servicios.domain.Servicio;
import com.Vet.VetBackend.servicios.repo.MotivoRepository;
import com.Vet.VetBackend.servicios.repo.MotivoServicioRepository;
import com.Vet.VetBackend.servicios.repo.ServicioRepository;
import com.Vet.VetBackend.servicios.web.dto.MotivoReq;
import com.Vet.VetBackend.servicios.web.dto.MotivoRes;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class MotivoServiceImpl implements MotivoService {

    private static final String ERR_NO_ENCONTRADO = "no encontrado";

    private final MotivoRepository motivoRepo;
    private final MotivoServicioRepository msRepo;
    private final ServicioRepository servicioRepo;

    public MotivoServiceImpl(MotivoRepository motivoRepo,
                             MotivoServicioRepository msRepo,
                             ServicioRepository servicioRepo) {
        this.motivoRepo = motivoRepo;
        this.msRepo = msRepo;
        this.servicioRepo = servicioRepo;
    }

    @Override
    public MotivoRes crear(MotivoReq req) {
        if (req.getNombre() == null || req.getNombre().isBlank())
            throw new IllegalArgumentException("nombre requerido");

        String nombre = req.getNombre().trim().replaceAll("\\s+", " ");
        if (motivoRepo.existsByNombreIgnoreCase(nombre))
            throw new IllegalArgumentException("nombre ya existe");

        Motivo m = Motivo.builder().nombre(nombre).build();
        try {
            return map(motivoRepo.save(m));
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("nombre ya existe");
        }
    }

    @Override
    public MotivoRes actualizar(Short id, MotivoReq req) {
        if (req.getNombre() == null || req.getNombre().isBlank())
            throw new IllegalArgumentException("nombre requerido");

        Motivo m = motivoRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException(ERR_NO_ENCONTRADO));

        String nuevo = req.getNombre().trim().replaceAll("\\s+", " ");

        if (!nuevo.equalsIgnoreCase(m.getNombre())
                && motivoRepo.existsByNombreIgnoreCaseAndIdNot(nuevo, id)) {
            throw new IllegalArgumentException("nombre ya existe");
        }

        m.setNombre(nuevo);
        try {
            return map(motivoRepo.save(m));
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("nombre ya existe");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MotivoRes obtener(Short id) {
        return map(motivoRepo.findById(id).orElseThrow(() -> new NoSuchElementException(ERR_NO_ENCONTRADO)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MotivoRes> listar() {
        return motivoRepo.findAll().stream().map(MotivoServiceImpl::map).collect(Collectors.toList());
    }

    @Override
    public void vincular(Short motivoId, Long servicioId) {
        if (!msRepo.existsByMotivoIdAndServicioId(motivoId, servicioId)) {
            Motivo m = motivoRepo.findById(motivoId).orElseThrow(() -> new NoSuchElementException("motivo no encontrado"));
            Servicio s = servicioRepo.findById(servicioId).orElseThrow(() -> new NoSuchElementException("servicio no encontrado"));
            try { msRepo.save(new MotivoServicio(null, m, s)); } catch (DataIntegrityViolationException ignored) {}
        }
    }

    @Override
    public void desvincular(Short motivoId, Long servicioId) {
        msRepo.deleteByMotivoIdAndServicioId(motivoId, servicioId);
    }

    private static MotivoRes map(Motivo m) { return new MotivoRes(m.getId(), m.getNombre()); }
}
