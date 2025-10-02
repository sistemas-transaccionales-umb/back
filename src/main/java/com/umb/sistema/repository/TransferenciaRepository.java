package com.umb.sistema.repository;

import com.umb.sistema.entity.Transferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransferenciaRepository extends JpaRepository<Transferencia, Integer> {
    List<Transferencia> findByBodegaOrigen_IdBodega(Integer idBodega);
    List<Transferencia> findByBodegaDestino_IdBodega(Integer idBodega);
    List<Transferencia> findByUsuario_IdUsuario(Integer idUsuario);
    
    @Query("SELECT t FROM Transferencia t WHERE t.estado = 'PENDIENTE'")
    List<Transferencia> findTransferenciasPendientes();
    
    @Query("SELECT t FROM Transferencia t WHERE t.estado = 'EN_TRANSITO'")
    List<Transferencia> findTransferenciasEnTransito();
    
    @Query("SELECT t FROM Transferencia t WHERE t.fechaSolicitud BETWEEN :fechaInicio AND :fechaFin")
    List<Transferencia> findByFechaSolicitudBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}

