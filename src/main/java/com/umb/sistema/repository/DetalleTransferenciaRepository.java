package com.umb.sistema.repository;

import com.umb.sistema.entity.DetalleTransferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleTransferenciaRepository extends JpaRepository<DetalleTransferencia, Long> {
    List<DetalleTransferencia> findByTransferencia_IdTransferencia(Integer idTransferencia);
    List<DetalleTransferencia> findByProducto_IdProducto(Integer idProducto);
}

