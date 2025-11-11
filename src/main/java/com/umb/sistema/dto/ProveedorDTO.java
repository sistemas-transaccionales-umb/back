package com.umb.sistema.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class ProveedorDTO {
    
    // Request DTO
    public record ProveedorRequest(
        @NotBlank(message = "El NIT/RUC es obligatorio")
        @Size(max = 20, message = "El NIT/RUC no puede exceder 20 caracteres")
        String nitRuc,
        
        @NotBlank(message = "El nombre del proveedor es obligatorio")
        @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
        String nombre,
        
        @Size(max = 100, message = "El nombre de contacto no puede exceder 100 caracteres")
        String nombreContacto,
        
        String direccion,
        
        @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
        String telefono,
        
        @Email(message = "El email debe ser válido")
        @Size(max = 255, message = "El email no puede exceder 255 caracteres")
        String email,
        
        String observaciones
    ) {}
    
    // Response DTO
    public record ProveedorResponse(
        Integer idProveedor,
        String nitRuc,
        String nombre,
        String nombreContacto,
        String direccion,
        String telefono,
        String email,
        String estado,
        String observaciones,
        LocalDateTime fechaCreacion
    ) {}
}

