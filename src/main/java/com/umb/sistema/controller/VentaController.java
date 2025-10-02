package com.umb.sistema.controller;

import com.umb.sistema.dto.VentaDTO.*;
import com.umb.sistema.entity.Venta;
import com.umb.sistema.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {
    
    private final VentaService ventaService;
    
    @PostMapping
    public ResponseEntity<VentaResponse> crearVenta(@Valid @RequestBody VentaRequest request) {
        VentaResponse response = ventaService.crearVenta(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<VentaResponse> obtenerVenta(@PathVariable Integer id) {
        VentaResponse response = ventaService.obtenerVenta(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<VentaResponse>> obtenerTodasLasVentas() {
        List<VentaResponse> ventas = ventaService.obtenerTodasLasVentas();
        return ResponseEntity.ok(ventas);
    }
    
    @PutMapping("/{id}/estado-pago")
    public ResponseEntity<VentaResponse> actualizarEstadoPago(
            @PathVariable Integer id,
            @RequestParam Venta.EstadoPago estadoPago) {
        VentaResponse response = ventaService.actualizarEstadoPago(id, estadoPago);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<VentaResponse>> obtenerVentasPorCliente(@PathVariable Integer idCliente) {
        List<VentaResponse> ventas = ventaService.obtenerVentasPorCliente(idCliente);
        return ResponseEntity.ok(ventas);
    }
    
    @GetMapping("/periodo")
    public ResponseEntity<List<VentaResponse>> obtenerVentasPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        List<VentaResponse> ventas = ventaService.obtenerVentasPorPeriodo(fechaInicio, fechaFin);
        return ResponseEntity.ok(ventas);
    }
}

