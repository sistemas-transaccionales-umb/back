package com.umb.sistema.controller;

import com.umb.sistema.dto.UsuarioDTO.*;
import com.umb.sistema.entity.Usuario;
import com.umb.sistema.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    
    @PostMapping
    public ResponseEntity<UsuarioResponse> crearUsuario(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse response = usuarioService.crearUsuario(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizarUsuario(
            @PathVariable Integer id,
            @Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse response = usuarioService.actualizarUsuario(id, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerUsuario(@PathVariable Integer id) {
        UsuarioResponse response = usuarioService.obtenerUsuario(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> obtenerTodosLosUsuarios() {
        List<UsuarioResponse> usuarios = usuarioService.obtenerTodosLosUsuarios();
        return ResponseEntity.ok(usuarios);
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioResponse>> obtenerUsuariosActivos() {
        List<UsuarioResponse> usuarios = usuarioService.obtenerUsuariosActivos();
        return ResponseEntity.ok(usuarios);
    }
    
    @GetMapping("/rol/{idRol}")
    public ResponseEntity<List<UsuarioResponse>> obtenerUsuariosPorRol(@PathVariable Integer idRol) {
        List<UsuarioResponse> usuarios = usuarioService.obtenerUsuariosPorRol(idRol);
        return ResponseEntity.ok(usuarios);
    }
    
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstadoUsuario(
            @PathVariable Integer id,
            @RequestParam Usuario.EstadoUsuario estado) {
        usuarioService.cambiarEstadoUsuario(id, estado);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Integer id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}

