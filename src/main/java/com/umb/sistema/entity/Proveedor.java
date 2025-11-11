package com.umb.sistema.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "proveedores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proveedor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Integer idProveedor;
    
    @NotBlank(message = "El NIT/RUC es obligatorio")
    @Size(max = 20, message = "El NIT/RUC no puede exceder 20 caracteres")
    @Column(name = "nit_ruc", nullable = false, length = 20, unique = true)
    private String nitRuc;
    
    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;
    
    @Size(max = 100, message = "El nombre de contacto no puede exceder 100 caracteres")
    @Column(name = "nombre_contacto", length = 100)
    private String nombreContacto;
    
    @Column(name = "direccion", columnDefinition = "TEXT")
    private String direccion;
    
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    @Column(name = "telefono", length = 20)
    private String telefono;
    
    @Email(message = "El email debe ser válido")
    @Size(max = 255, message = "El email no puede exceder 255 caracteres")
    @Column(name = "email", length = 255)
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", columnDefinition = "ENUM('ACTIVO', 'INACTIVO') DEFAULT 'ACTIVO'")
    private EstadoProveedor estado = EstadoProveedor.ACTIVO;
    
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
    
    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Compra> compras;
    
    public enum EstadoProveedor {
        ACTIVO, INACTIVO
    }
}

