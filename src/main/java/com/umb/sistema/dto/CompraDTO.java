package com.umb.sistema.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CompraDTO {
    
    // Request para crear compra
    public record CompraRequest(
        @NotNull(message = "El ID del proveedor es obligatorio")
        Integer idProveedor,
        
        @NotNull(message = "El ID del usuario es obligatorio")
        Integer idUsuario,
        
        @NotBlank(message = "El número de compra es obligatorio")
        @Size(max = 50, message = "El número de compra no puede exceder 50 caracteres")
        String numeroCompra,
        
        @NotNull(message = "La fecha de compra es obligatoria")
        LocalDate fechaCompra,
        
        String observaciones,
        
        @NotNull(message = "Los detalles de la compra son obligatorios")
        @Size(min = 1, message = "Debe incluir al menos un detalle de compra")
        @Valid
        List<DetalleCompraRequest> detalles
    ) {}
    
    // Request para detalle de compra
    public record DetalleCompraRequest(
        @NotNull(message = "El ID del producto es obligatorio")
        Integer idProducto,
        
        @NotNull(message = "El ID de la bodega es obligatorio")
        Integer idBodega,
        
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        Integer cantidad,
        
        @NotNull(message = "El precio unitario de compra es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio unitario debe ser mayor que 0")
        BigDecimal precioUnitarioCompra
    ) {}
    
    // Response completo de compra
    public record CompraResponse(
        Long idCompra,
        ProveedorDTO.ProveedorResponse proveedor,
        UsuarioBasicResponse usuario,
        String numeroCompra,
        LocalDate fechaCompra,
        BigDecimal subtotal,
        BigDecimal totalIva,
        BigDecimal totalCompra,
        String estado,
        String observaciones,
        LocalDateTime fechaCreacion,
        List<DetalleCompraResponse> detalles
    ) {}
    
    // Response de detalle de compra
    public record DetalleCompraResponse(
        Long idDetalleCompra,
        ProductoBasicResponse producto,
        BodegaBasicResponse bodega,
        Integer cantidad,
        BigDecimal precioUnitarioCompra,
        BigDecimal subtotalLinea,
        BigDecimal totalIvaLinea,
        BigDecimal totalLinea
    ) {}
    
    // DTOs simplificados para referencias
    public record UsuarioBasicResponse(
        Integer idUsuario,
        String nombres,
        String apellidos,
        String email
    ) {}
    
    public record ProductoBasicResponse(
        Integer idProducto,
        String codigoBarras,
        String nombre,
        BigDecimal porcentajeIva
    ) {}
    
    public record BodegaBasicResponse(
        Integer idBodega,
        String nombreBodega
    ) {}
}

