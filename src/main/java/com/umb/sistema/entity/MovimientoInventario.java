package com.umb.sistema.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_inventario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoInventario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Long idMovimiento;
    
    @NotNull(message = "El producto es obligatorio")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;
    
    @NotNull(message = "La bodega es obligatoria")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_bodega", nullable = false)
    private Bodega bodega;
    
    @NotNull(message = "El tipo de movimiento es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, columnDefinition = "ENUM('ENTRADA', 'SALIDA', 'AJUSTE', 'TRANSFERENCIA')")
    private TipoMovimiento tipoMovimiento;
    
    @NotNull(message = "La cantidad anterior es obligatoria")
    @Min(value = 0, message = "La cantidad anterior no puede ser negativa")
    @Column(name = "cantidad_anterior", nullable = false)
    private Integer cantidadAnterior;
    
    @NotNull(message = "La cantidad nueva es obligatoria")
    @Min(value = 0, message = "La cantidad nueva no puede ser negativa")
    @Column(name = "cantidad_nueva", nullable = false)
    private Integer cantidadNueva;
    
    @NotBlank(message = "El motivo es obligatorio")
    @Column(name = "motivo", nullable = false, columnDefinition = "TEXT")
    private String motivo;
    
    @Size(max = 100, message = "La referencia no puede exceder 100 caracteres")
    @Column(name = "referencia", length = 100)
    private String referencia;
    
    @CreationTimestamp
    @Column(name = "fecha_movimiento", nullable = false, updatable = false)
    private LocalDateTime fechaMovimiento;
    
    public enum TipoMovimiento {
        ENTRADA, SALIDA, AJUSTE, TRANSFERENCIA
    }
}

