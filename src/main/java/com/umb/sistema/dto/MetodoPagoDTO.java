package com.umb.sistema.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MetodoPagoDTO {
    
    // Request DTO
    public record MetodoPagoRequest(
        @NotBlank(message = "El nombre del método de pago es obligatorio")
        @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
        String nombre,
        
        String descripcion
    ) {}
    
    // Response DTO
    public record MetodoPagoResponse(
        Integer idMetodoPago,
        String nombre,
        String descripcion
    ) {}
}

