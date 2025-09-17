package com.Vet.VetBackend.facturacion.web.controller;

import com.Vet.VetBackend.facturacion.app.PagoService;
import com.Vet.VetBackend.facturacion.web.dto.PagoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    /* ================== CRUD ================== */

    @PostMapping
    public ResponseEntity<PagoDTO> registrarPago(@RequestBody PagoRequest request) {
        try {
            PagoDTO nuevoPago = pagoService.registrarPago(
                    request.getFacturaId(),
                    request.getMetodo(),
                    request.getMonto(),
                    request.getFechaPago()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPago);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoDTO> obtenerPago(@PathVariable("id") Long id) {
        try {
            Optional<PagoDTO> pago = pagoService.obtenerPagoPorId(id);
            return pago.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/factura/{facturaId}")
    public ResponseEntity<List<PagoDTO>> obtenerPagosPorFactura(@PathVariable("facturaId") Long facturaId) {
        try {
            return ResponseEntity.ok(pagoService.obtenerPagosPorFactura(facturaId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/metodo/{metodo}")
    public ResponseEntity<List<PagoDTO>> obtenerPagosPorMetodo(@PathVariable("metodo") String metodo) {
        try {
            return ResponseEntity.ok(pagoService.obtenerPagosPorMetodo(metodo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<PagoDTO>> obtenerPagosEnPeriodo(
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam("fechaFin")    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            return ResponseEntity.ok(pagoService.obtenerPagosEnPeriodo(fechaInicio, fechaFin));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PagoDTO>> obtenerPagosPorCliente(@PathVariable("clienteId") Long clienteId) {
        try {
            return ResponseEntity.ok(pagoService.obtenerPagosPorCliente(clienteId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse> anularPago(@PathVariable("id") Long id,
                                                      @RequestBody(required = false) AnulacionRequest request) {
        try {
            String motivo = request != null ? request.getMotivo() : "Sin motivo especificado";
            pagoService.anularPago(id, motivo);
            return ResponseEntity.ok(new SuccessResponse("Pago anulado correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /* ============ CONSULTAS / VALIDACIONES ============ */

    @GetMapping("/factura/{facturaId}/total")
    public ResponseEntity<TotalResponse> obtenerTotalPagado(@PathVariable("facturaId") Long facturaId) {
        try {
            BigDecimal totalPagado = pagoService.obtenerTotalPagado(facturaId);
            return ResponseEntity.ok(new TotalResponse(totalPagado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/validar")
    public ResponseEntity<ValidacionResponse> validarPago(@RequestParam("facturaId") Long facturaId,
                                                          @RequestParam("monto") BigDecimal monto) {
        try {
            boolean puedeRegistrar = pagoService.puedeRegistrarPago(facturaId, monto);
            BigDecimal saldoDespues = pagoService.calcularSaldoDespuesPago(facturaId, monto);
            return ResponseEntity.ok(new ValidacionResponse(puedeRegistrar, saldoDespues));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/factura/{facturaId}/ultimo")
    public ResponseEntity<PagoDTO> obtenerUltimoPago(@PathVariable("facturaId") Long facturaId) {
        try {
            Optional<PagoDTO> ultimoPago = pagoService.obtenerUltimoPago(facturaId);
            return ultimoPago.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /* ============ ESTADÍSTICAS (con DTOs propios) ============ */

    @GetMapping("/estadisticas/metodos")
    public ResponseEntity<List<RecaudacionPorMetodoRes>> obtenerRecaudacionPorMetodo() {
        var data = pagoService.obtenerRecaudacionPorMetodo()
                .stream()
                .map(r -> new RecaudacionPorMetodoRes(
                        r.getMetodo(),
                        r.getTotalRecaudado()          // 👈 antes getTotal()
                ))
                .toList();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/estadisticas/diaria")
    public ResponseEntity<List<RecaudacionDiariaRes>> obtenerRecaudacionDiaria(
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam("fechaFin")    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        var data = pagoService.obtenerRecaudacionDiaria(fechaInicio, fechaFin)
                .stream()
                .map(r -> new RecaudacionDiariaRes(
                        r.getFecha().toLocalDate(),     // 👈 service devuelve java.sql.Date
                        r.getTotalRecaudado()           // 👈 antes getTotal()
                ))
                .toList();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/estadisticas/total")
    public ResponseEntity<TotalResponse> obtenerTotalRecaudado(
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam("fechaFin")    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            BigDecimal totalRecaudado = pagoService.obtenerTotalRecaudado(fechaInicio, fechaFin);
            return ResponseEntity.ok(new TotalResponse(totalRecaudado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/estadisticas/hoy")
    public ResponseEntity<EstadisticasPagosHoyRes> obtenerEstadisticasHoy() {
        var s = pagoService.obtenerEstadisticasHoy();
        return ResponseEntity.ok(new EstadisticasPagosHoyRes(
                s.getCantidadPagos(),               // 👈 antes getCantidad()
                s.getTotalRecaudado()              // 👈 antes getTotal()
        ));
    }

    /* ========== DTOs request/response (públicos) ========== */

    public static class PagoRequest {
        private Long facturaId;
        private String metodo;
        private BigDecimal monto;
        private LocalDateTime fechaPago;
        public PagoRequest() {}
        public Long getFacturaId() { return facturaId; }
        public void setFacturaId(Long facturaId) { this.facturaId = facturaId; }
        public String getMetodo() { return metodo; }
        public void setMetodo(String metodo) { this.metodo = metodo; }
        public BigDecimal getMonto() { return monto; }
        public void setMonto(BigDecimal monto) { this.monto = monto; }
        public LocalDateTime getFechaPago() { return fechaPago; }
        public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }
    }

    public static class AnulacionRequest {
        private String motivo;
        public AnulacionRequest() {}
        public String getMotivo() { return motivo; }
        public void setMotivo(String motivo) { this.motivo = motivo; }
    }

    public static class TotalResponse {
        private BigDecimal total;
        public TotalResponse(BigDecimal total) { this.total = total; }
        public BigDecimal getTotal() { return total; }
    }

    public static class ValidacionResponse {
        private boolean puedeRegistrar;
        private BigDecimal saldoDespuesPago;
        public ValidacionResponse(boolean puedeRegistrar, BigDecimal saldoDespuesPago) {
            this.puedeRegistrar = puedeRegistrar;
            this.saldoDespuesPago = saldoDespuesPago;
        }
        public boolean isPuedeRegistrar() { return puedeRegistrar; }
        public BigDecimal getSaldoDespuesPago() { return saldoDespuesPago; }
    }

    public static class SuccessResponse {
        private String mensaje;
        private LocalDateTime timestamp;
        public SuccessResponse(String mensaje) {
            this.mensaje = mensaje;
            this.timestamp = LocalDateTime.now();
        }
        public String getMensaje() { return mensaje; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }

    /* ===== DTOs para estadísticas (públicos y simples) ===== */

    public static class RecaudacionPorMetodoRes {
        private String metodo;
        private BigDecimal total;
        public RecaudacionPorMetodoRes(String metodo, BigDecimal total) {
            this.metodo = metodo; this.total = total;
        }
        public String getMetodo() { return metodo; }
        public BigDecimal getTotal() { return total; }
    }

    public static class RecaudacionDiariaRes {
        private LocalDate fecha;
        private BigDecimal total;
        public RecaudacionDiariaRes(LocalDate fecha, BigDecimal total) {
            this.fecha = fecha; this.total = total;
        }
        public LocalDate getFecha() { return fecha; }
        public BigDecimal getTotal() { return total; }
    }

    public static class EstadisticasPagosHoyRes {
        private Integer cantidad;
        private BigDecimal total;
        public EstadisticasPagosHoyRes(Integer cantidad, BigDecimal total) {
            this.cantidad = cantidad; this.total = total;
        }
        public Integer getCantidad() { return cantidad; }
        public BigDecimal getTotal() { return total; }
    }
}
