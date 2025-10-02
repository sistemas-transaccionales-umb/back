package com.umb.sistema.repository;

import com.umb.sistema.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Integer> {
    Optional<Inventario> findByProducto_IdProductoAndBodega_IdBodega(Integer idProducto, Integer idBodega);
    
    List<Inventario> findByBodega_IdBodega(Integer idBodega);
    List<Inventario> findByProducto_IdProducto(Integer idProducto);
    
    @Query("SELECT i FROM Inventario i WHERE i.cantidad <= i.stockMinimo")
    List<Inventario> findProductosConStockBajo();
    
    @Query("SELECT i FROM Inventario i WHERE i.bodega.idBodega = :idBodega AND i.cantidad <= i.stockMinimo")
    List<Inventario> findProductosConStockBajoPorBodega(Integer idBodega);
    
    @Modifying
    @Query("UPDATE Inventario i SET i.cantidad = i.cantidad + :cantidad WHERE i.idInventario = :idInventario")
    void incrementarStock(Integer idInventario, Integer cantidad);
    
    @Modifying
    @Query("UPDATE Inventario i SET i.cantidad = i.cantidad - :cantidad WHERE i.idInventario = :idInventario AND i.cantidad >= :cantidad")
    int decrementarStock(Integer idInventario, Integer cantidad);
}

