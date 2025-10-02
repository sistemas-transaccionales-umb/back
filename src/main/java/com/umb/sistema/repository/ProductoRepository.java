package com.umb.sistema.repository;

import com.umb.sistema.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    Optional<Producto> findByCodigoBarras(String codigoBarras);
    boolean existsByCodigoBarras(String codigoBarras);
    
    List<Producto> findByCategoria_IdCategoria(Integer idCategoria);
    
    @Query("SELECT p FROM Producto p WHERE p.estado = 'ACTIVO'")
    List<Producto> findAllActive();
    
    @Query("SELECT p FROM Producto p WHERE p.estado = 'ACTIVO' AND " +
           "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR p.codigoBarras LIKE CONCAT('%', :searchTerm, '%'))")
    List<Producto> searchActiveProducts(String searchTerm);
}

