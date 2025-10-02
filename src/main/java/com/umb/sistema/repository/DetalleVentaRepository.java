package com.umb.sistema.repository;

import com.umb.sistema.entity.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    List<DetalleVenta> findByVenta_IdVenta(Integer idVenta);
    List<DetalleVenta> findByProducto_IdProducto(Integer idProducto);
    
    @Query("SELECT d FROM DetalleVenta d WHERE d.venta.idVenta = :idVenta")
    List<DetalleVenta> findDetallesByVentaId(Integer idVenta);
}

