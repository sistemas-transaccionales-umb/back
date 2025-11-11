package com.umb.sistema.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class RolDTO {
    
    // Request DTO
    public record RolRequest(
        @NotBlank(message = "El nombre del rol es obligatorio")
        @Size(max = 50, message = "El nombre del rol no puede exceder 50 caracteres")
        String nombreRol,
        
        String descripcion,
        
        List<Integer> idsPermisos
    ) {}
    
    // Response DTO
    public record RolResponse(
        Integer idRol,
        String nombreRol,
        String descripcion,
        List<PermisoDTO.PermisoResponse> permisos
    ) {}
}

