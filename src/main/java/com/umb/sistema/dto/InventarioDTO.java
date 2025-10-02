package com.umb.sistema.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class InventarioDTO {
    
    // Request DTO
    public record InventarioRequest(
        @NotNull(message = "El producto es obligatorio")
        Integer idProducto,
        
        @NotNull(message = "La bodega es obligatoria")
        Integer idBodega,
        
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 0, message = "La cantidad no puede ser negativa")
        Integer cantidad,
        
        @NotNull(message = "El stock mínimo es obligatorio")
        @Min(value = 0, message = "El stock mínimo no puede ser negativo")
        Integer stockMinimo
    ) {}
    
    // Response DTO
    public record InventarioResponse(
        Integer idInventario,
        String nombreProducto,
        String codigoBarras,
        String nombreBodega,
        Integer cantidad,
        Integer stockMinimo,
        LocalDateTime fechaActualizacion,
        boolean stockBajo
    ) {
        public InventarioResponse(Integer idInventario, String nombreProducto, String codigoBarras, 
                                 String nombreBodega, Integer cantidad, Integer stockMinimo, 
                                 LocalDateTime fechaActualizacion) {
            this(idInventario, nombreProducto, codigoBarras, nombreBodega, cantidad, stockMinimo, 
                 fechaActualizacion, cantidad <= stockMinimo);
        }
    }
    
    // DTO para ajuste de inventario
    public record AjusteInventarioRequest(
        @NotNull(message = "El producto es obligatorio")
        Integer idProducto,
        
        @NotNull(message = "La bodega es obligatoria")
        Integer idBodega,
        
        @NotNull(message = "La cantidad es obligatoria")
        Integer cantidad,
        
        @NotBlank(message = "El motivo es obligatorio")
        String motivo
    ) {}
}

