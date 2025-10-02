package com.umb.sistema.repository;

import com.umb.sistema.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {
    Optional<Venta> findByNumeroFactura(String numeroFactura);
    boolean existsByNumeroFactura(String numeroFactura);
    
    List<Venta> findByCliente_IdCliente(Integer idCliente);
    List<Venta> findByUsuario_IdUsuario(Integer idUsuario);
    
    @Query("SELECT v FROM Venta v WHERE v.fechaVenta BETWEEN :fechaInicio AND :fechaFin")
    List<Venta> findByFechaVentaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    @Query("SELECT v FROM Venta v WHERE v.estadoPago = 'PENDIENTE'")
    List<Venta> findVentasPendientes();
    
    @Query("SELECT SUM(v.totalVenta) FROM Venta v WHERE v.fechaVenta BETWEEN :fechaInicio AND :fechaFin AND v.estadoPago = 'PAGADO'")
    Double calcularTotalVentasPorPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}

