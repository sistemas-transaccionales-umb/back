package com.umb.sistema.controller;

import com.umb.sistema.dto.BodegaDTO.*;
import com.umb.sistema.service.BodegaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bodegas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BodegaController {
    
    private final BodegaService bodegaService;
    
    @PostMapping
    public ResponseEntity<BodegaResponse> crearBodega(@Valid @RequestBody BodegaRequest request) {
        BodegaResponse response = bodegaService.crearBodega(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<BodegaResponse> actualizarBodega(
            @PathVariable Integer id,
            @Valid @RequestBody BodegaRequest request) {
        BodegaResponse response = bodegaService.actualizarBodega(id, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BodegaResponse> obtenerBodega(@PathVariable Integer id) {
        BodegaResponse response = bodegaService.obtenerBodega(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<BodegaResponse>> obtenerTodasLasBodegas() {
        List<BodegaResponse> bodegas = bodegaService.obtenerTodasLasBodegas();
        return ResponseEntity.ok(bodegas);
    }
    
    @GetMapping("/activas")
    public ResponseEntity<List<BodegaResponse>> obtenerBodegasActivas() {
        List<BodegaResponse> bodegas = bodegaService.obtenerBodegasActivas();
        return ResponseEntity.ok(bodegas);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarBodega(@PathVariable Integer id) {
        bodegaService.eliminarBodega(id);
        return ResponseEntity.noContent().build();
    }
}

