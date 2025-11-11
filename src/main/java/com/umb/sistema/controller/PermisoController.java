package com.umb.sistema.controller;

import com.umb.sistema.dto.PermisoDTO.*;
import com.umb.sistema.service.PermisoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permisos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PermisoController {
    
    private final PermisoService permisoService;
    
    @PostMapping
    public ResponseEntity<PermisoResponse> crearPermiso(@Valid @RequestBody PermisoRequest request) {
        PermisoResponse response = permisoService.crearPermiso(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PermisoResponse> actualizarPermiso(
            @PathVariable Integer id,
            @Valid @RequestBody PermisoRequest request) {
        PermisoResponse response = permisoService.actualizarPermiso(id, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PermisoResponse> obtenerPermiso(@PathVariable Integer id) {
        PermisoResponse response = permisoService.obtenerPermiso(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<PermisoResponse>> obtenerTodosLosPermisos() {
        List<PermisoResponse> permisos = permisoService.obtenerTodosLosPermisos();
        return ResponseEntity.ok(permisos);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPermiso(@PathVariable Integer id) {
        permisoService.eliminarPermiso(id);
        return ResponseEntity.noContent().build();
    }
}

