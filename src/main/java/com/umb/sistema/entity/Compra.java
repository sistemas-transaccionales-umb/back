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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "compras")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Compra {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra")
    private Long idCompra;
    
    @NotNull(message = "El proveedor es obligatorio")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_proveedor", nullable = false)
    private Proveedor proveedor;
    
    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
    
    @NotBlank(message = "El número de compra es obligatorio")
    @Size(max = 50, message = "El número de compra no puede exceder 50 caracteres")
    @Column(name = "numero_compra", nullable = false, length = 50, unique = true)
    private String numeroCompra;
    
    @NotNull(message = "La fecha de compra es obligatoria")
    @Column(name = "fecha_compra", nullable = false)
    private LocalDate fechaCompra;
    
    @NotNull(message = "El subtotal es obligatorio")
    @DecimalMin(value = "0.0", message = "El subtotal no puede ser negativo")
    @Column(name = "subtotal", nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal;
    
    @NotNull(message = "El total de IVA es obligatorio")
    @DecimalMin(value = "0.0", message = "El total de IVA no puede ser negativo")
    @Column(name = "total_iva", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalIva;
    
    @NotNull(message = "El total de la compra es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El total debe ser mayor que 0")
    @Column(name = "total_compra", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalCompra;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", columnDefinition = "ENUM('PENDIENTE', 'RECIBIDA', 'CANCELADA') DEFAULT 'PENDIENTE'")
    private EstadoCompra estado = EstadoCompra.PENDIENTE;
    
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
    
    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DetalleCompra> detalles = new ArrayList<>();
    
    public enum EstadoCompra {
        PENDIENTE,   // Compra registrada pero no recibida
        RECIBIDA,    // Mercancía recibida e ingresada al inventario
        CANCELADA    // Compra cancelada
    }
    
    // Métodos helper para gestionar detalles
    public void agregarDetalle(DetalleCompra detalle) {
        detalles.add(detalle);
        detalle.setCompra(this);
    }
    
    public void removerDetalle(DetalleCompra detalle) {
        detalles.remove(detalle);
        detalle.setCompra(null);
    }
}

