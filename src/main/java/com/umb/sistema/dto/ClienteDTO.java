package com.umb.sistema.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class ClienteDTO {
    
    // Request DTO
    public record ClienteRequest(
        @NotBlank(message = "El tipo de documento es obligatorio")
        @Size(max = 3, message = "El tipo de documento no puede exceder 3 caracteres")
        String tipoDocumento,
        
        @NotBlank(message = "El número de documento es obligatorio")
        @Size(max = 20, message = "El número de documento no puede exceder 20 caracteres")
        String numeroDocumento,
        
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        String nombre,
        
        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 100, message = "Los apellidos no pueden exceder 100 caracteres")
        String apellidos,
        
        @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
        String direccion,
        
        @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
        String telefono,
        
        @Email(message = "El email debe ser válido")
        @Size(max = 255, message = "El email no puede exceder 255 caracteres")
        String email,
        
        String contrasenaHash
    ) {}
    
    // Response DTO
    public record ClienteResponse(
        Integer idCliente,
        String tipoDocumento,
        String numeroDocumento,
        String nombre,
        String apellidos,
        String direccion,
        String telefono,
        String email,
        String estado,
        LocalDateTime fechaCreacion
    ) {}
}

