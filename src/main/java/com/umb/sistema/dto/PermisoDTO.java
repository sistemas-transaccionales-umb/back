package com.umb.sistema.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PermisoDTO {
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermisoRequest {
        @NotBlank(message = "El nombre del permiso es obligatorio")
        @Size(max = 100, message = "El nombre del permiso no puede exceder 100 caracteres")
        private String nombre;
        
        private String descripcion;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermisoResponse {
        private Integer idPermiso;
        private String nombre;
        private String descripcion;
    }
}

