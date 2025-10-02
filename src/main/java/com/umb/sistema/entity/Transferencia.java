package com.umb.sistema.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "transferencias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transferencia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transferencia")
    private Integer idTransferencia;
    
    @NotNull(message = "La bodega de origen es obligatoria")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_bodega_origen", nullable = false)
    private Bodega bodegaOrigen;
    
    @NotNull(message = "La bodega de destino es obligatoria")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_bodega_destino", nullable = false)
    private Bodega bodegaDestino;
    
    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
    
    @CreationTimestamp
    @Column(name = "fecha_solicitud", nullable = false, updatable = false)
    private LocalDateTime fechaSolicitud;
    
    @Column(name = "fecha_recibo")
    private LocalDateTime fechaRecibo;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", columnDefinition = "ENUM('PENDIENTE', 'EN_TRANSITO', 'RECIBIDO', 'CANCELADO') DEFAULT 'PENDIENTE'")
    private EstadoTransferencia estado = EstadoTransferencia.PENDIENTE;
    
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
    
    @OneToMany(mappedBy = "transferencia", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<DetalleTransferencia> detalles = new ArrayList<>();
    
    public enum EstadoTransferencia {
        PENDIENTE, EN_TRANSITO, RECIBIDO, CANCELADO
    }
    
    // Helper methods para mantener consistencia bidireccional
    public void addDetalle(DetalleTransferencia detalle) {
        detalles.add(detalle);
        detalle.setTransferencia(this);
    }
    
    public void removeDetalle(DetalleTransferencia detalle) {
        detalles.remove(detalle);
        detalle.setTransferencia(null);
    }
}

