package com.umb.sistema.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public class UsuarioDTO {
    
    // Request DTO
    public record UsuarioRequest(
        @NotNull(message = "El rol es obligatorio")
        Integer idRol,
        
        @NotBlank(message = "El tipo de documento es obligatorio")
        @Size(max = 3, message = "El tipo de documento no puede exceder 3 caracteres")
        String tipoDocumento,
        
        @NotBlank(message = "El número de documento es obligatorio")
        @Size(max = 20, message = "El número de documento no puede exceder 20 caracteres")
        String numeroDocumento,
        
        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 100, message = "Los nombres no pueden exceder 100 caracteres")
        String nombres,
        
        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 100, message = "Los apellidos no pueden exceder 100 caracteres")
        String apellidos,
        
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe ser válido")
        @Size(max = 255, message = "El email no puede exceder 255 caracteres")
        String email,
        
        @NotBlank(message = "La contraseña es obligatoria")
        String contrasena,
        
        @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
        String telefono
    ) {}
    
    // Response DTO
    public record UsuarioResponse(
        Integer idUsuario,
        RolDTO.RolResponse rol,
        String tipoDocumento,
        String numeroDocumento,
        String nombres,
        String apellidos,
        String email,
        String telefono,
        String estado,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaUltimoLogin
    ) {}
}

