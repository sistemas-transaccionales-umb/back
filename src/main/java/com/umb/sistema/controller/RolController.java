package com.umb.sistema.controller;

import com.umb.sistema.dto.RolDTO.*;
import com.umb.sistema.service.RolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RolController {
    
    private final RolService rolService;
    
    @PostMapping
    public ResponseEntity<RolResponse> crearRol(@Valid @RequestBody RolRequest request) {
        RolResponse response = rolService.crearRol(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<RolResponse> actualizarRol(
            @PathVariable Integer id,
            @Valid @RequestBody RolRequest request) {
        RolResponse response = rolService.actualizarRol(id, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RolResponse> obtenerRol(@PathVariable Integer id) {
        RolResponse response = rolService.obtenerRol(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<RolResponse>> obtenerTodosLosRoles() {
        List<RolResponse> roles = rolService.obtenerTodosLosRoles();
        return ResponseEntity.ok(roles);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRol(@PathVariable Integer id) {
        rolService.eliminarRol(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{idRol}/permisos")
    public ResponseEntity<RolResponse> agregarPermisosARol(
            @PathVariable Integer idRol,
            @RequestBody List<Integer> idsPermisos) {
        RolResponse response = rolService.agregarPermisosARol(idRol, idsPermisos);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{idRol}/permisos")
    public ResponseEntity<RolResponse> removerPermisosDeRol(
            @PathVariable Integer idRol,
            @RequestBody List<Integer> idsPermisos) {
        RolResponse response = rolService.removerPermisosDeRol(idRol, idsPermisos);
        return ResponseEntity.ok(response);
    }
}

