package com.umb.sistema.repository;

import com.umb.sistema.entity.DetalleCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleCompraRepository extends JpaRepository<DetalleCompra, Long> {
    
    List<DetalleCompra> findByCompra_IdCompra(Long idCompra);
    
    List<DetalleCompra> findByProducto_IdProducto(Integer idProducto);
    
    List<DetalleCompra> findByBodega_IdBodega(Integer idBodega);
    
    @Query("SELECT d FROM DetalleCompra d WHERE d.compra.idCompra = :idCompra AND d.producto.idProducto = :idProducto")
    List<DetalleCompra> findByCompraAndProducto(@Param("idCompra") Long idCompra, 
                                                 @Param("idProducto") Integer idProducto);
}

