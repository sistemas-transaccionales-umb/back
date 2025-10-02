package com.umb.sistema.repository;

import com.umb.sistema.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByNumeroDocumento(String numeroDocumento);
    boolean existsByEmail(String email);
    boolean existsByNumeroDocumento(String numeroDocumento);
    
    List<Usuario> findByRol_IdRol(Integer idRol);
    
    @Query("SELECT u FROM Usuario u WHERE u.estado = 'ACTIVO'")
    List<Usuario> findAllActive();
    
    @Modifying
    @Query("UPDATE Usuario u SET u.fechaUltimoLogin = :fecha WHERE u.idUsuario = :idUsuario")
    void updateUltimoLogin(Integer idUsuario, LocalDateTime fecha);
}

