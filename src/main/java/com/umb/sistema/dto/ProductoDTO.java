package com.umb.sistema.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductoDTO {
    
    // Request DTO
    public record ProductoRequest(
        @NotNull(message = "La categoría es obligatoria")
        Integer idCategoria,
        
        @NotBlank(message = "El código de barras es obligatorio")
        @Size(max = 100, message = "El código de barras no puede exceder 100 caracteres")
        String codigoBarras,
        
        @NotBlank(message = "El nombre del producto es obligatorio")
        @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
        String nombre,
        
        String descripcion,
        
        @NotNull(message = "El precio de compra es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio de compra debe ser mayor que 0")
        BigDecimal precioCompra,
        
        @NotNull(message = "El precio de venta es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio de venta debe ser mayor que 0")
        BigDecimal precioVenta,
        
        @NotNull(message = "El porcentaje de IVA es obligatorio")
        @DecimalMin(value = "0.0", message = "El porcentaje de IVA debe ser mayor o igual a 0")
        BigDecimal porcentajeIva
    ) {}
    
    // Response DTO
    public record ProductoResponse(
        Integer idProducto,
        String nombreCategoria,
        String codigoBarras,
        String nombre,
        String descripcion,
        BigDecimal precioCompra,
        BigDecimal precioVenta,
        BigDecimal porcentajeIva,
        String estado,
        LocalDateTime fechaCreacion
    ) {}
}

