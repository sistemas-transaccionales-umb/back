package com.umb.sistema.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bodegas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bodega {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bodega")
    private Integer idBodega;
    
    @NotBlank(message = "El nombre de la bodega es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Size(max = 255, message = "La ubicación no puede exceder 255 caracteres")
    @Column(name = "ubicacion", length = 255)
    private String ubicacion;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", columnDefinition = "ENUM('ACTIVA', 'INACTIVA') DEFAULT 'ACTIVA'")
    private EstadoBodega estado = EstadoBodega.ACTIVA;
    
    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @OneToMany(mappedBy = "bodega", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inventario> inventarios;
    
    @OneToMany(mappedBy = "bodegaOrigen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transferencia> transferenciasOrigen;
    
    @OneToMany(mappedBy = "bodegaDestino", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transferencia> transferenciasDestino;
    
    @OneToMany(mappedBy = "bodega", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MovimientoInventario> movimientos;
    
    public enum EstadoBodega {
        ACTIVA, INACTIVA
    }
}

