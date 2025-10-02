package com.umb.sistema.repository;

import com.umb.sistema.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByNumeroDocumento(String numeroDocumento);
    Optional<Cliente> findByEmail(String email);
    boolean existsByNumeroDocumento(String numeroDocumento);
    boolean existsByEmail(String email);
    
    @Query("SELECT c FROM Cliente c WHERE c.estado = 'ACTIVO'")
    List<Cliente> findAllActive();
    
    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(c.apellidos) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR c.numeroDocumento LIKE CONCAT('%', :searchTerm, '%')")
    List<Cliente> searchClientes(String searchTerm);
}

