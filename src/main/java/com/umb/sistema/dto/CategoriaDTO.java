package com.umb.sistema.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoriaDTO {
    
    // Request DTO
    public record CategoriaRequest(
        @NotBlank(message = "El nombre de la categoría es obligatorio")
        @Size(max = 100, message = "El nombre de la categoría no puede exceder 100 caracteres")
        String nombreCategoria,
        
        String descripcion
    ) {}
    
    // Response DTO
    public record CategoriaResponse(
        Integer idCategoria,
        String nombreCategoria,
        String descripcion
    ) {}
}

