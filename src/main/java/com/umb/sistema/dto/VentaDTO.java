package com.umb.sistema.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class VentaDTO {
    
    // Request DTO para crear venta
    public record VentaRequest(
        @NotNull(message = "El cliente es obligatorio")
        Integer idCliente,
        
        @NotNull(message = "El usuario es obligatorio")
        Integer idUsuario,
        
        @NotBlank(message = "El número de factura es obligatorio")
        @Size(max = 20, message = "El número de factura no puede exceder 20 caracteres")
        String numeroFactura,
        
        @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
        BigDecimal totalDescuento,
        
        String observaciones,
        
        String olaCode,
        
        @NotEmpty(message = "Debe incluir al menos un detalle de venta")
        @Valid
        List<DetalleVentaRequest> detalles
    ) {}
    
    // Request DTO para detalle de venta
    public record DetalleVentaRequest(
        @NotNull(message = "El producto es obligatorio")
        Integer idProducto,
        
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        Integer cantidad,
        
        @NotNull(message = "El precio unitario es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio unitario debe ser mayor que 0")
        BigDecimal precioUnitario
    ) {}
    
    // Response DTO
    public record VentaResponse(
        Integer idVenta,
        String nombreCliente,
        String nombreUsuario,
        String numeroFactura,
        LocalDateTime fechaVenta,
        BigDecimal totalDescuento,
        BigDecimal totalVenta,
        String observaciones,
        String olaCode,
        String estadoPago,
        List<DetalleVentaResponse> detalles
    ) {}
    
    // Response DTO para detalle de venta
    public record DetalleVentaResponse(
        Long idDetalleVenta,
        String nombreProducto,
        String codigoBarras,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotalLinea,
        BigDecimal totalIvaLinea,
        BigDecimal totalLinea
    ) {}
}

