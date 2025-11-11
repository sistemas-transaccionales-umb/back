package com.umb.sistema.repository;

import com.umb.sistema.entity.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    
    Optional<Compra> findByNumeroCompra(String numeroCompra);
    
    boolean existsByNumeroCompra(String numeroCompra);
    
    List<Compra> findByProveedor_IdProveedor(Integer idProveedor);
    
    List<Compra> findByUsuario_IdUsuario(Integer idUsuario);
    
    List<Compra> findByEstado(Compra.EstadoCompra estado);
    
    @Query("SELECT c FROM Compra c WHERE c.fechaCompra BETWEEN :fechaInicio AND :fechaFin")
    List<Compra> findByFechaCompraBetween(@Param("fechaInicio") LocalDate fechaInicio, 
                                           @Param("fechaFin") LocalDate fechaFin);
    
    @Query("SELECT c FROM Compra c WHERE c.proveedor.idProveedor = :idProveedor AND c.estado = :estado")
    List<Compra> findByProveedorAndEstado(@Param("idProveedor") Integer idProveedor, 
                                           @Param("estado") Compra.EstadoCompra estado);
}

