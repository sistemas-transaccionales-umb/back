package com.umb.sistema.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_compras")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleCompra {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_compra")
    private Long idDetalleCompra;
    
    @NotNull(message = "La compra es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compra", nullable = false)
    private Compra compra;
    
    @NotNull(message = "El producto es obligatorio")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;
    
    @NotNull(message = "La bodega es obligatoria")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_bodega", nullable = false)
    private Bodega bodega;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;
    
    @NotNull(message = "El precio unitario de compra es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio unitario debe ser mayor que 0")
    @Column(name = "precio_unitario_compra", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitarioCompra;
    
    @NotNull(message = "El subtotal de línea es obligatorio")
    @DecimalMin(value = "0.0", message = "El subtotal de línea no puede ser negativo")
    @Column(name = "subtotal_linea", nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotalLinea;
    
    @NotNull(message = "El total de IVA es obligatorio")
    @DecimalMin(value = "0.0", message = "El total de IVA no puede ser negativo")
    @Column(name = "total_iva_linea", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalIvaLinea;
    
    @NotNull(message = "El total de línea es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El total de línea debe ser mayor que 0")
    @Column(name = "total_linea", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalLinea;
    
    // Método para calcular automáticamente los totales
    @PrePersist
    @PreUpdate
    public void calcularTotales() {
        if (cantidad != null && precioUnitarioCompra != null && producto != null) {
            this.subtotalLinea = precioUnitarioCompra.multiply(BigDecimal.valueOf(cantidad));
            this.totalIvaLinea = subtotalLinea.multiply(producto.getPorcentajeIva()).divide(BigDecimal.valueOf(100));
            this.totalLinea = subtotalLinea.add(totalIvaLinea);
        }
    }
}

