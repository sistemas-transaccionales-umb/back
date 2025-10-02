package com.umb.sistema.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AuthDTO {
    
    /**
     * Request para login de usuario
     */
    public record LoginRequest(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe ser válido")
        String email,
        
        @NotBlank(message = "La contraseña es obligatoria")
        String password
    ) {}
    
    /**
     * Request para registro de nuevo usuario
     */
    public record RegisterRequest(
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
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String password,
        
        @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
        String telefono
    ) {}
    
    /**
     * Response con datos del usuario autenticado
     */
    public record AuthResponse(
        Integer idUsuario,
        String nombres,
        String apellidos,
        String email,
        String tipoDocumento,
        String numeroDocumento,
        String telefono,
        Integer idRol,
        String nombreRol,
        String estado,
        String message
    ) {}
}

