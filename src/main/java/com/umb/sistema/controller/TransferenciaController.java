package com.umb.sistema.controller;

import com.umb.sistema.dto.TransferenciaDTO.*;
import com.umb.sistema.service.TransferenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transferencias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransferenciaController {
    
    private final TransferenciaService transferenciaService;
    
    @PostMapping
    public ResponseEntity<TransferenciaResponse> crearTransferencia(@Valid @RequestBody TransferenciaRequest request) {
        TransferenciaResponse response = transferenciaService.crearTransferencia(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TransferenciaResponse> obtenerTransferencia(@PathVariable Integer id) {
        TransferenciaResponse response = transferenciaService.obtenerTransferencia(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<TransferenciaResponse>> obtenerTodasLasTransferencias() {
        List<TransferenciaResponse> transferencias = transferenciaService.obtenerTodasLasTransferencias();
        return ResponseEntity.ok(transferencias);
    }
    
    @GetMapping("/pendientes")
    public ResponseEntity<List<TransferenciaResponse>> obtenerTransferenciasPendientes() {
        List<TransferenciaResponse> transferencias = transferenciaService.obtenerTransferenciasPendientes();
        return ResponseEntity.ok(transferencias);
    }
    
    @PostMapping("/{id}/procesar")
    public ResponseEntity<TransferenciaResponse> procesarTransferencia(@PathVariable Integer id) {
        TransferenciaResponse response = transferenciaService.procesarTransferencia(id);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/recibir")
    public ResponseEntity<TransferenciaResponse> recibirTransferencia(@PathVariable Integer id) {
        TransferenciaResponse response = transferenciaService.recibirTransferencia(id);
        return ResponseEntity.ok(response);
    }
}

