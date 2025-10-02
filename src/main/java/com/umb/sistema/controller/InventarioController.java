package com.umb.sistema.controller;

import com.umb.sistema.dto.InventarioDTO.*;
import com.umb.sistema.service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {
    
    private final InventarioService inventarioService;
    
    @PostMapping
    public ResponseEntity<InventarioResponse> crearInventario(@Valid @RequestBody InventarioRequest request) {
        InventarioResponse response = inventarioService.crearInventario(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PostMapping("/ajustar")
    public ResponseEntity<InventarioResponse> ajustarInventario(@Valid @RequestBody AjusteInventarioRequest request) {
        InventarioResponse response = inventarioService.ajustarInventario(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<InventarioResponse> obtenerInventario(@PathVariable Integer id) {
        InventarioResponse response = inventarioService.obtenerInventario(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<InventarioResponse>> obtenerTodoElInventario() {
        List<InventarioResponse> inventarios = inventarioService.obtenerTodoElInventario();
        return ResponseEntity.ok(inventarios);
    }
    
    @GetMapping("/bodega/{idBodega}")
    public ResponseEntity<List<InventarioResponse>> obtenerInventarioPorBodega(@PathVariable Integer idBodega) {
        List<InventarioResponse> inventarios = inventarioService.obtenerInventarioPorBodega(idBodega);
        return ResponseEntity.ok(inventarios);
    }
    
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<InventarioResponse>> obtenerProductosConStockBajo() {
        List<InventarioResponse> inventarios = inventarioService.obtenerProductosConStockBajo();
        return ResponseEntity.ok(inventarios);
    }
}

