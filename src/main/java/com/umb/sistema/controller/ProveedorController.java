package com.umb.sistema.controller;

import com.umb.sistema.dto.ProveedorDTO.*;
import com.umb.sistema.entity.Proveedor;
import com.umb.sistema.service.ProveedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProveedorController {
    
    private final ProveedorService proveedorService;
    
    @PostMapping
    public ResponseEntity<ProveedorResponse> crearProveedor(@Valid @RequestBody ProveedorRequest request) {
        ProveedorResponse response = proveedorService.crearProveedor(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProveedorResponse> actualizarProveedor(
            @PathVariable Integer id,
            @Valid @RequestBody ProveedorRequest request) {
        ProveedorResponse response = proveedorService.actualizarProveedor(id, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProveedorResponse> obtenerProveedor(@PathVariable Integer id) {
        ProveedorResponse response = proveedorService.obtenerProveedor(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<ProveedorResponse>> obtenerTodosLosProveedores() {
        List<ProveedorResponse> proveedores = proveedorService.obtenerTodosLosProveedores();
        return ResponseEntity.ok(proveedores);
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<ProveedorResponse>> obtenerProveedoresActivos() {
        List<ProveedorResponse> proveedores = proveedorService.obtenerProveedoresActivos();
        return ResponseEntity.ok(proveedores);
    }
    
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstadoProveedor(
            @PathVariable Integer id,
            @RequestParam Proveedor.EstadoProveedor estado) {
        proveedorService.cambiarEstadoProveedor(id, estado);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProveedor(@PathVariable Integer id) {
        proveedorService.eliminarProveedor(id);
        return ResponseEntity.noContent().build();
    }
}

