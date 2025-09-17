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

/**
 * Servicio de negocio para {@link Servicio}.
 *
 * Responsabilidades:
 * - CRUD, activación y listado filtrado.
 * - Validación y normalización de nombre.
 *
 * Reglas:
 * - Nombre único.
 * - precioBase ≥ 0.
 *
 * @since 1.0
 */
@Service
@Transactional
public class ServicioServiceImpl implements ServicioService {

    private static final String ERR_NO_ENCONTRADO = "no encontrado";
    private static final String ERR_NOMBRE_EXISTE = "nombre ya existe";

    private final ServicioRepository repo;

    public ServicioServiceImpl(ServicioRepository repo) { this.repo = repo; }

    /**
     * Crea un servicio con nombre único.
     *
     * @param req comando con nombre, descripción y precio base.
     * @return DTO creado.
     * @throws IllegalArgumentException si validación falla o nombre duplicado.
     */
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

    /**
     * Actualiza datos editables. Mantiene unicidad de nombre.
     *
     * @param id  id del servicio.
     * @param req comando con cambios.
     * @return DTO actualizado.
     * @throws NoSuchElementException   si no existe.
     * @throws IllegalArgumentException si validación falla o nombre duplicado.
     */
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

    /**
     * Cambia el estado activo.
     *
     * @param id     id del servicio.
     * @param activo nuevo estado.
     * @return DTO actualizado.
     * @throws NoSuchElementException si no existe.
     */
    @Override
    public ServicioRes activar(Long id, boolean activo) {
        Servicio s = repo.findById(id).orElseThrow(() -> new NoSuchElementException(ERR_NO_ENCONTRADO));
        s.setActivo(activo);
        return map(repo.save(s));
    }

    /**
     * Obtiene un servicio por id.
     *
     * @param id identificador.
     * @return DTO del servicio.
     * @throws NoSuchElementException si no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public ServicioRes obtener(Long id) {
        return map(repo.findById(id).orElseThrow(() -> new NoSuchElementException(ERR_NO_ENCONTRADO)));
    }

    /**
     * Lista servicios con filtro por nombre y estado.
     *
     * @param q     texto a buscar en nombre (opcional).
     * @param activo filtro por estado (opcional).
     * @param page  página base 0.
     * @param size  tamaño de página.
     * @return página de DTOs.
     */
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

    /**
     * Valida reglas de entrada.
     *
     * @param r request.
     * @throws IllegalArgumentException si nombre vacío o precioBase negativo.
     */
    private void validar(ServicioReq r) {
        if (r.getNombre() == null || r.getNombre().isBlank())
            throw new IllegalArgumentException("nombre requerido");
        if (r.getPrecioBase() != null && r.getPrecioBase().compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("precio_base >= 0");
    }

    /**
     * Normaliza nombre colapsando espacios y trim.
     *
     * @param nombre texto original.
     * @return nombre normalizado.
     */
    private String normalizarNombre(String nombre) {
        return nombre.trim().replaceAll("\\s+", " ");
    }

    /**
     * Aplica campos editables a la entidad.
     *
     * @param r request.
     * @param s entidad destino.
     * @return entidad modificada.
     */
    private Servicio aplicar(ServicioReq r, Servicio s) {
        s.setDescripcion(r.getDescripcion());
        s.setPrecioBase(r.getPrecioBase());
        if (r.getActivo() != null) s.setActivo(r.getActivo());
        return s;
    }

    /**
     * Mapea entidad a DTO.
     *
     * @param s entidad servicio.
     * @return DTO.
     */
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
