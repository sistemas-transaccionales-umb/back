package com.umb.sistema.repository;

import com.umb.sistema.entity.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {
    List<MovimientoInventario> findByProducto_IdProducto(Integer idProducto);
    List<MovimientoInventario> findByBodega_IdBodega(Integer idBodega);
    
    @Query("SELECT m FROM MovimientoInventario m WHERE m.producto.idProducto = :idProducto AND m.bodega.idBodega = :idBodega ORDER BY m.fechaMovimiento DESC")
    List<MovimientoInventario> findByProductoAndBodegaOrderByFechaDesc(Integer idProducto, Integer idBodega);
    
    @Query("SELECT m FROM MovimientoInventario m WHERE m.fechaMovimiento BETWEEN :fechaInicio AND :fechaFin")
    List<MovimientoInventario> findByFechaMovimientoBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    @Query("SELECT m FROM MovimientoInventario m WHERE m.tipoMovimiento = :tipoMovimiento")
    List<MovimientoInventario> findByTipoMovimiento(MovimientoInventario.TipoMovimiento tipoMovimiento);
}

