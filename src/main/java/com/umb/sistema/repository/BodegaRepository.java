package com.umb.sistema.repository;

import com.umb.sistema.entity.Bodega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BodegaRepository extends JpaRepository<Bodega, Integer> {
    Optional<Bodega> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
    
    @Query("SELECT b FROM Bodega b WHERE b.estado = 'ACTIVA'")
    List<Bodega> findAllActive();
}

