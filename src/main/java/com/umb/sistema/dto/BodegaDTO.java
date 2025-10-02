package com.umb.sistema.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class BodegaDTO {
    
    // Request DTO
    public record BodegaRequest(
        @NotBlank(message = "El nombre de la bodega es obligatorio")
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        String nombre,
        
        @Size(max = 255, message = "La ubicación no puede exceder 255 caracteres")
        String ubicacion
    ) {}
    
    // Response DTO
    public record BodegaResponse(
        Integer idBodega,
        String nombre,
        String ubicacion,
        String estado,
        LocalDateTime fechaCreacion
    ) {}
}

