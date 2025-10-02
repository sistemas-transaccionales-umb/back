package com.umb.sistema.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class TransferenciaDTO {
    
    // Request DTO para crear transferencia
    public record TransferenciaRequest(
        @NotNull(message = "La bodega de origen es obligatoria")
        Integer idBodegaOrigen,
        
        @NotNull(message = "La bodega de destino es obligatoria")
        Integer idBodegaDestino,
        
        @NotNull(message = "El usuario es obligatorio")
        Integer idUsuario,
        
        String observaciones,
        
        @NotEmpty(message = "Debe incluir al menos un detalle de transferencia")
        @Valid
        List<DetalleTransferenciaRequest> detalles
    ) {}
    
    // Request DTO para detalle de transferencia
    public record DetalleTransferenciaRequest(
        @NotNull(message = "El producto es obligatorio")
        Integer idProducto,
        
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        Integer cantidad
    ) {}
    
    // Response DTO
    public record TransferenciaResponse(
        Integer idTransferencia,
        String bodegaOrigen,
        String bodegaDestino,
        String nombreUsuario,
        LocalDateTime fechaSolicitud,
        LocalDateTime fechaRecibo,
        String estado,
        String observaciones,
        List<DetalleTransferenciaResponse> detalles
    ) {}
    
    // Response DTO para detalle de transferencia
    public record DetalleTransferenciaResponse(
        Long idDetalleTransferencia,
        String nombreProducto,
        String codigoBarras,
        Integer cantidad
    ) {}
}

