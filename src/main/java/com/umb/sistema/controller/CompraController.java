package com.umb.sistema.controller;

import com.umb.sistema.dto.CompraDTO.*;
import com.umb.sistema.entity.Compra;
import com.umb.sistema.service.CompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CompraController {
    
    private final CompraService compraService;
    
    @PostMapping
    public ResponseEntity<CompraResponse> crearCompra(@Valid @RequestBody CompraRequest request) {
        CompraResponse response = compraService.crearCompra(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PostMapping("/{id}/recibir")
    public ResponseEntity<CompraResponse> recibirCompra(@PathVariable Long id) {
        CompraResponse response = compraService.recibirCompra(id);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<CompraResponse> cancelarCompra(
            @PathVariable Long id,
            @RequestParam(required = false) String motivo) {
        CompraResponse response = compraService.cancelarCompra(id, motivo);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CompraResponse> obtenerCompra(@PathVariable Long id) {
        CompraResponse response = compraService.obtenerCompra(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<CompraResponse>> obtenerTodasLasCompras() {
        List<CompraResponse> compras = compraService.obtenerTodasLasCompras();
        return ResponseEntity.ok(compras);
    }
    
    @GetMapping("/proveedor/{idProveedor}")
    public ResponseEntity<List<CompraResponse>> obtenerComprasPorProveedor(@PathVariable Integer idProveedor) {
        List<CompraResponse> compras = compraService.obtenerComprasPorProveedor(idProveedor);
        return ResponseEntity.ok(compras);
    }
    
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<CompraResponse>> obtenerComprasPorEstado(@PathVariable Compra.EstadoCompra estado) {
        List<CompraResponse> compras = compraService.obtenerComprasPorEstado(estado);
        return ResponseEntity.ok(compras);
    }
    
    @GetMapping("/rango-fechas")
    public ResponseEntity<List<CompraResponse>> obtenerComprasPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<CompraResponse> compras = compraService.obtenerComprasPorRangoFechas(fechaInicio, fechaFin);
        return ResponseEntity.ok(compras);
    }
}

