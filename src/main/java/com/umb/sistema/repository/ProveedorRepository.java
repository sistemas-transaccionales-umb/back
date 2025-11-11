package com.umb.sistema.repository;

import com.umb.sistema.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {
    
    Optional<Proveedor> findByNitRuc(String nitRuc);
    
    boolean existsByNitRuc(String nitRuc);
    
    @Query("SELECT p FROM Proveedor p WHERE p.estado = 'ACTIVO'")
    List<Proveedor> findAllActive();
}

