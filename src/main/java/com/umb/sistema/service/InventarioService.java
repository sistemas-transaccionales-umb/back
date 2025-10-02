package com.umb.sistema.service;

import com.umb.sistema.dto.InventarioDTO.*;
import com.umb.sistema.entity.*;
import com.umb.sistema.exception.BusinessException;
import com.umb.sistema.exception.ResourceNotFoundException;
import com.umb.sistema.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventarioService {
    
    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;
    private final BodegaRepository bodegaRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    
    @Transactional(rollbackFor = Exception.class)
    public InventarioResponse crearInventario(InventarioRequest request) {
        Producto producto = productoRepository.findById(request.idProducto())
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + request.idProducto()));
        
        Bodega bodega = bodegaRepository.findById(request.idBodega())
            .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con ID: " + request.idBodega()));
        
        // Verificar si ya existe inventario para este producto en esta bodega
        if (inventarioRepository.findByProducto_IdProductoAndBodega_IdBodega(
            request.idProducto(), request.idBodega()).isPresent()) {
            throw new BusinessException("Ya existe inventario para este producto en la bodega especificada");
        }
        
        Inventario inventario = new Inventario();
        inventario.setProducto(producto);
        inventario.setBodega(bodega);
        inventario.setCantidad(request.cantidad());
        inventario.setStockMinimo(request.stockMinimo());
        
        Inventario inventarioGuardado = inventarioRepository.save(inventario);
        
        // Registrar movimiento de inventario inicial
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setBodega(bodega);
        movimiento.setTipoMovimiento(MovimientoInventario.TipoMovimiento.ENTRADA);
        movimiento.setCantidadAnterior(0);
        movimiento.setCantidadNueva(request.cantidad());
        movimiento.setMotivo("Inventario inicial");
        movimiento.setReferencia("INV-INICIAL-" + inventarioGuardado.getIdInventario());
        movimientoInventarioRepository.save(movimiento);
        
        return toInventarioResponse(inventarioGuardado);
    }
    
    /**
     * Ajustar inventario - Operación transaccional ACID
     * - Actualiza la cantidad de inventario
     * - Registra el movimiento
     */
    @Transactional(rollbackFor = Exception.class)
    public InventarioResponse ajustarInventario(AjusteInventarioRequest request) {
        log.info("Ajustando inventario para producto {} en bodega {}", 
            request.idProducto(), request.idBodega());
        
        Inventario inventario = inventarioRepository.findByProducto_IdProductoAndBodega_IdBodega(
            request.idProducto(), request.idBodega())
            .orElseThrow(() -> new ResourceNotFoundException(
                "No existe inventario para el producto y bodega especificados"));
        
        int cantidadAnterior = inventario.getCantidad();
        int cantidadNueva = cantidadAnterior + request.cantidad();
        
        if (cantidadNueva < 0) {
            throw new BusinessException("El ajuste resultaría en una cantidad negativa de inventario");
        }
        
        inventario.setCantidad(cantidadNueva);
        Inventario inventarioActualizado = inventarioRepository.save(inventario);
        
        // Registrar movimiento
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(inventario.getProducto());
        movimiento.setBodega(inventario.getBodega());
        movimiento.setTipoMovimiento(MovimientoInventario.TipoMovimiento.AJUSTE);
        movimiento.setCantidadAnterior(cantidadAnterior);
        movimiento.setCantidadNueva(cantidadNueva);
        movimiento.setMotivo(request.motivo());
        movimiento.setReferencia("AJUSTE-" + inventario.getIdInventario());
        movimientoInventarioRepository.save(movimiento);
        
        log.info("Inventario ajustado. Cantidad anterior: {}, Cantidad nueva: {}", 
            cantidadAnterior, cantidadNueva);
        
        return toInventarioResponse(inventarioActualizado);
    }
    
    @Transactional(readOnly = true)
    public InventarioResponse obtenerInventario(Integer idInventario) {
        Inventario inventario = inventarioRepository.findById(idInventario)
            .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado con ID: " + idInventario));
        return toInventarioResponse(inventario);
    }
    
    @Transactional(readOnly = true)
    public List<InventarioResponse> obtenerTodoElInventario() {
        return inventarioRepository.findAll().stream()
            .map(this::toInventarioResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<InventarioResponse> obtenerInventarioPorBodega(Integer idBodega) {
        return inventarioRepository.findByBodega_IdBodega(idBodega).stream()
            .map(this::toInventarioResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<InventarioResponse> obtenerProductosConStockBajo() {
        return inventarioRepository.findProductosConStockBajo().stream()
            .map(this::toInventarioResponse)
            .collect(Collectors.toList());
    }
    
    private InventarioResponse toInventarioResponse(Inventario inventario) {
        return new InventarioResponse(
            inventario.getIdInventario(),
            inventario.getProducto().getNombre(),
            inventario.getProducto().getCodigoBarras(),
            inventario.getBodega().getNombre(),
            inventario.getCantidad(),
            inventario.getStockMinimo(),
            inventario.getFechaActualizacion()
        );
    }
}

