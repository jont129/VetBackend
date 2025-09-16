// src/main/java/com/Vet/VetBackend/servicios/app/implementations/ServicioServiceImpl.java
package com.Vet.VetBackend.servicios.app.implementations;

import com.Vet.VetBackend.servicios.app.services.ServicioService;
import com.Vet.VetBackend.servicios.domain.Servicio;
import com.Vet.VetBackend.servicios.repo.ServicioRepository;
import com.Vet.VetBackend.servicios.web.dto.ServicioReq;
import com.Vet.VetBackend.servicios.web.dto.ServicioRes;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@Transactional
public class ServicioServiceImpl implements ServicioService {

    private static final String ERR_NO_ENCONTRADO = "no encontrado";
    private static final String ERR_NOMBRE_EXISTE = "nombre ya existe";

    private final ServicioRepository repo;

    public ServicioServiceImpl(ServicioRepository repo) { this.repo = repo; }

    @Override
    public ServicioRes crear(ServicioReq req) {
        validar(req);
        String nombre = normalizarNombre(req.getNombre());
        repo.findByNombreIgnoreCase(nombre).ifPresent(s -> { throw new IllegalArgumentException(ERR_NOMBRE_EXISTE); });
        Servicio s = aplicar(req, new Servicio());
        s.setNombre(nombre);
        try {
            return map(repo.save(s));
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(ERR_NOMBRE_EXISTE);
        }
    }

    @Override
    public ServicioRes actualizar(Long id, ServicioReq req) {
        validar(req);
        Servicio s = repo.findById(id).orElseThrow(() -> new NoSuchElementException(ERR_NO_ENCONTRADO));

        String nuevoNombre = normalizarNombre(req.getNombre());
        if (!Objects.equals(s.getNombre().toLowerCase(), nuevoNombre.toLowerCase())
                && repo.findByNombreIgnoreCase(nuevoNombre).isPresent()) {
            throw new IllegalArgumentException(ERR_NOMBRE_EXISTE);
        }

        s = aplicar(req, s);
        s.setNombre(nuevoNombre);
        try {
            return map(repo.save(s));
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(ERR_NOMBRE_EXISTE);
        }
    }

    @Override
    public ServicioRes activar(Long id, boolean activo) {
        Servicio s = repo.findById(id).orElseThrow(() -> new NoSuchElementException(ERR_NO_ENCONTRADO));
        s.setActivo(activo);
        return map(repo.save(s));
    }

    @Override
    @Transactional(readOnly = true)
    public ServicioRes obtener(Long id) {
        return map(repo.findById(id).orElseThrow(() -> new NoSuchElementException(ERR_NO_ENCONTRADO)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServicioRes> listar(String q, Boolean activo, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
        Specification<Servicio> spec = (root, query, cb) -> cb.conjunction();

        if (q != null && !q.isBlank()) {
            String like = "%" + q.toLowerCase().trim() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("nombre")), like));
        }
        if (activo != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("activo"), activo));
        }
        return repo.findAll(spec, pageable).map(this::map);
    }

    private void validar(ServicioReq r) {
        if (r.getNombre() == null || r.getNombre().isBlank())
            throw new IllegalArgumentException("nombre requerido");
        if (r.getPrecioBase() != null && r.getPrecioBase().compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("precio_base >= 0");
    }

    private String normalizarNombre(String nombre) {
        return nombre.trim().replaceAll("\\s+", " ");
    }

    private Servicio aplicar(ServicioReq r, Servicio s) {
        s.setDescripcion(r.getDescripcion());
        s.setPrecioBase(r.getPrecioBase());
        if (r.getActivo() != null) s.setActivo(r.getActivo());
        return s;
    }

    private ServicioRes map(Servicio s) {
        return ServicioRes.builder()
                .id(s.getId())
                .nombre(s.getNombre())
                .descripcion(s.getDescripcion())
                .precioBase(s.getPrecioBase())
                .activo(s.getActivo())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}
