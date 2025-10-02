package com.umb.sistema.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Integer idVenta;
    
    @NotNull(message = "El cliente es obligatorio")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;
    
    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
    
    @NotBlank(message = "El número de factura es obligatorio")
    @Size(max = 20, message = "El número de factura no puede exceder 20 caracteres")
    @Column(name = "numero_factura", nullable = false, length = 20, unique = true)
    private String numeroFactura;
    
    @CreationTimestamp
    @Column(name = "fecha_venta", nullable = false, updatable = false)
    private LocalDateTime fechaVenta;
    
    @NotNull(message = "El total de descuento es obligatorio")
    @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
    @Column(name = "total_descuento", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalDescuento = BigDecimal.ZERO;
    
    @NotNull(message = "El total de venta es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El total de venta debe ser mayor que 0")
    @Column(name = "total_venta", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalVenta;
    
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
    
    @Size(max = 255, message = "El código OLA no puede exceder 255 caracteres")
    @Column(name = "ola_code", length = 255)
    private String olaCode;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago", columnDefinition = "ENUM('PENDIENTE', 'PAGADO', 'CANCELADO') DEFAULT 'PENDIENTE'")
    private EstadoPago estadoPago = EstadoPago.PENDIENTE;
    
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();
    
    public enum EstadoPago {
        PENDIENTE, PAGADO, CANCELADO
    }
    
    // Helper methods para mantener consistencia bidireccional
    public void addDetalle(DetalleVenta detalle) {
        detalles.add(detalle);
        detalle.setVenta(this);
    }
    
    public void removeDetalle(DetalleVenta detalle) {
        detalles.remove(detalle);
        detalle.setVenta(null);
    }
}

