package com.umb.sistema.dto;

import java.time.LocalDateTime;

public class MovimientoInventarioDTO {
    
    // Response DTO
    public record MovimientoInventarioResponse(
        Long idMovimiento,
        String nombreProducto,
        String codigoBarras,
        String nombreBodega,
        String tipoMovimiento,
        Integer cantidadAnterior,
        Integer cantidadNueva,
        String motivo,
        String referencia,
        LocalDateTime fechaMovimiento
    ) {}
}

