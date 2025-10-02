package com.umb.sistema.service;

import com.umb.sistema.dto.TransferenciaDTO.*;
import com.umb.sistema.entity.*;
import com.umb.sistema.exception.BusinessException;
import com.umb.sistema.exception.InsufficientStockException;
import com.umb.sistema.exception.ResourceNotFoundException;
import com.umb.sistema.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferenciaService {
    
    private final TransferenciaRepository transferenciaRepository;
    private final BodegaRepository bodegaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    
    /**
     * Crear transferencia - Operación transaccional ACID
     * - Crea la solicitud de transferencia
     * - NO modifica inventario hasta que se confirme la recepción
     */
    @Transactional(rollbackFor = Exception.class)
    public TransferenciaResponse crearTransferencia(TransferenciaRequest request) {
        log.info("Iniciando creación de transferencia de bodega {} a {}", 
            request.idBodegaOrigen(), request.idBodegaDestino());
        
        // Validaciones
        if (request.idBodegaOrigen().equals(request.idBodegaDestino())) {
            throw new BusinessException("La bodega de origen y destino no pueden ser la misma");
        }
        
        Bodega bodegaOrigen = bodegaRepository.findById(request.idBodegaOrigen())
            .orElseThrow(() -> new ResourceNotFoundException("Bodega origen no encontrada con ID: " + request.idBodegaOrigen()));
        
        Bodega bodegaDestino = bodegaRepository.findById(request.idBodegaDestino())
            .orElseThrow(() -> new ResourceNotFoundException("Bodega destino no encontrada con ID: " + request.idBodegaDestino()));
        
        Usuario usuario = usuarioRepository.findById(request.idUsuario())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + request.idUsuario()));
        
        // Verificar stock disponible en bodega origen
        for (DetalleTransferenciaRequest detalleReq : request.detalles()) {
            Producto producto = productoRepository.findById(detalleReq.idProducto())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + detalleReq.idProducto()));
            
            Inventario inventario = inventarioRepository.findByProducto_IdProductoAndBodega_IdBodega(
                producto.getIdProducto(), bodegaOrigen.getIdBodega())
                .orElseThrow(() -> new InsufficientStockException(
                    "No hay inventario en bodega origen para: " + producto.getNombre()));
            
            if (inventario.getCantidad() < detalleReq.cantidad()) {
                throw new InsufficientStockException(
                    "Stock insuficiente en bodega origen para: " + producto.getNombre() + 
                    ". Disponible: " + inventario.getCantidad() + ", Solicitado: " + detalleReq.cantidad());
            }
        }
        
        // Crear transferencia
        Transferencia transferencia = new Transferencia();
        transferencia.setBodegaOrigen(bodegaOrigen);
        transferencia.setBodegaDestino(bodegaDestino);
        transferencia.setUsuario(usuario);
        transferencia.setObservaciones(request.observaciones());
        transferencia.setEstado(Transferencia.EstadoTransferencia.PENDIENTE);
        
        // Crear detalles
        for (DetalleTransferenciaRequest detalleReq : request.detalles()) {
            Producto producto = productoRepository.findById(detalleReq.idProducto()).get();
            
            DetalleTransferencia detalle = new DetalleTransferencia();
            detalle.setProducto(producto);
            detalle.setCantidad(detalleReq.cantidad());
            
            transferencia.addDetalle(detalle);
        }
        
        Transferencia transferenciaGuardada = transferenciaRepository.save(transferencia);
        
        log.info("Transferencia creada exitosamente: {}", transferenciaGuardada.getIdTransferencia());
        
        return toTransferenciaResponse(transferenciaGuardada);
    }
    
    /**
     * Procesar transferencia - Operación transaccional ACID crítica
     * - Descuenta inventario de bodega origen
     * - Incrementa inventario de bodega destino
     * - Registra movimientos de inventario
     * - Rollback automático si falla cualquier operación
     */
    @Transactional(rollbackFor = Exception.class)
    public TransferenciaResponse procesarTransferencia(Integer idTransferencia) {
        log.info("Procesando transferencia {}", idTransferencia);
        
        Transferencia transferencia = transferenciaRepository.findById(idTransferencia)
            .orElseThrow(() -> new ResourceNotFoundException("Transferencia no encontrada con ID: " + idTransferencia));
        
        if (transferencia.getEstado() != Transferencia.EstadoTransferencia.PENDIENTE) {
            throw new BusinessException("Solo se pueden procesar transferencias en estado PENDIENTE");
        }
        
        // Descontar de bodega origen
        for (DetalleTransferencia detalle : transferencia.getDetalles()) {
            Inventario inventarioOrigen = inventarioRepository.findByProducto_IdProductoAndBodega_IdBodega(
                detalle.getProducto().getIdProducto(), 
                transferencia.getBodegaOrigen().getIdBodega())
                .orElseThrow(() -> new InsufficientStockException(
                    "No hay inventario en bodega origen para: " + detalle.getProducto().getNombre()));
            
            if (inventarioOrigen.getCantidad() < detalle.getCantidad()) {
                throw new InsufficientStockException(
                    "Stock insuficiente en bodega origen para: " + detalle.getProducto().getNombre());
            }
            
            int cantidadAnteriorOrigen = inventarioOrigen.getCantidad();
            inventarioOrigen.setCantidad(cantidadAnteriorOrigen - detalle.getCantidad());
            inventarioRepository.save(inventarioOrigen);
            
            // Registrar movimiento de salida
            MovimientoInventario movimientoSalida = new MovimientoInventario();
            movimientoSalida.setProducto(detalle.getProducto());
            movimientoSalida.setBodega(transferencia.getBodegaOrigen());
            movimientoSalida.setTipoMovimiento(MovimientoInventario.TipoMovimiento.TRANSFERENCIA);
            movimientoSalida.setCantidadAnterior(cantidadAnteriorOrigen);
            movimientoSalida.setCantidadNueva(inventarioOrigen.getCantidad());
            movimientoSalida.setMotivo("Transferencia salida - ID: " + transferencia.getIdTransferencia());
            movimientoSalida.setReferencia("TRANS-" + transferencia.getIdTransferencia());
            movimientoInventarioRepository.save(movimientoSalida);
        }
        
        transferencia.setEstado(Transferencia.EstadoTransferencia.EN_TRANSITO);
        Transferencia transferenciaActualizada = transferenciaRepository.save(transferencia);
        
        log.info("Transferencia {} procesada a estado EN_TRANSITO", idTransferencia);
        
        return toTransferenciaResponse(transferenciaActualizada);
    }
    
    /**
     * Recibir transferencia - Operación transaccional ACID
     * - Incrementa inventario en bodega destino
     * - Marca transferencia como recibida
     * - Registra movimientos
     */
    @Transactional(rollbackFor = Exception.class)
    public TransferenciaResponse recibirTransferencia(Integer idTransferencia) {
        log.info("Recibiendo transferencia {}", idTransferencia);
        
        Transferencia transferencia = transferenciaRepository.findById(idTransferencia)
            .orElseThrow(() -> new ResourceNotFoundException("Transferencia no encontrada con ID: " + idTransferencia));
        
        if (transferencia.getEstado() != Transferencia.EstadoTransferencia.EN_TRANSITO) {
            throw new BusinessException("Solo se pueden recibir transferencias en estado EN_TRANSITO");
        }
        
        // Incrementar en bodega destino
        for (DetalleTransferencia detalle : transferencia.getDetalles()) {
            Inventario inventarioDestino = inventarioRepository.findByProducto_IdProductoAndBodega_IdBodega(
                detalle.getProducto().getIdProducto(), 
                transferencia.getBodegaDestino().getIdBodega())
                .orElseGet(() -> {
                    // Crear inventario si no existe en bodega destino
                    Inventario nuevoInventario = new Inventario();
                    nuevoInventario.setProducto(detalle.getProducto());
                    nuevoInventario.setBodega(transferencia.getBodegaDestino());
                    nuevoInventario.setCantidad(0);
                    nuevoInventario.setStockMinimo(0);
                    return nuevoInventario;
                });
            
            int cantidadAnteriorDestino = inventarioDestino.getCantidad();
            inventarioDestino.setCantidad(cantidadAnteriorDestino + detalle.getCantidad());
            inventarioRepository.save(inventarioDestino);
            
            // Registrar movimiento de entrada
            MovimientoInventario movimientoEntrada = new MovimientoInventario();
            movimientoEntrada.setProducto(detalle.getProducto());
            movimientoEntrada.setBodega(transferencia.getBodegaDestino());
            movimientoEntrada.setTipoMovimiento(MovimientoInventario.TipoMovimiento.TRANSFERENCIA);
            movimientoEntrada.setCantidadAnterior(cantidadAnteriorDestino);
            movimientoEntrada.setCantidadNueva(inventarioDestino.getCantidad());
            movimientoEntrada.setMotivo("Transferencia entrada - ID: " + transferencia.getIdTransferencia());
            movimientoEntrada.setReferencia("TRANS-" + transferencia.getIdTransferencia());
            movimientoInventarioRepository.save(movimientoEntrada);
        }
        
        transferencia.setEstado(Transferencia.EstadoTransferencia.RECIBIDO);
        transferencia.setFechaRecibo(LocalDateTime.now());
        Transferencia transferenciaActualizada = transferenciaRepository.save(transferencia);
        
        log.info("Transferencia {} recibida exitosamente", idTransferencia);
        
        return toTransferenciaResponse(transferenciaActualizada);
    }
    
    @Transactional(readOnly = true)
    public TransferenciaResponse obtenerTransferencia(Integer idTransferencia) {
        Transferencia transferencia = transferenciaRepository.findById(idTransferencia)
            .orElseThrow(() -> new ResourceNotFoundException("Transferencia no encontrada con ID: " + idTransferencia));
        return toTransferenciaResponse(transferencia);
    }
    
    @Transactional(readOnly = true)
    public List<TransferenciaResponse> obtenerTodasLasTransferencias() {
        return transferenciaRepository.findAll().stream()
            .map(this::toTransferenciaResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TransferenciaResponse> obtenerTransferenciasPendientes() {
        return transferenciaRepository.findTransferenciasPendientes().stream()
            .map(this::toTransferenciaResponse)
            .collect(Collectors.toList());
    }
    
    // Métodos de mapeo
    private TransferenciaResponse toTransferenciaResponse(Transferencia transferencia) {
        List<DetalleTransferenciaResponse> detalles = transferencia.getDetalles().stream()
            .map(this::toDetalleTransferenciaResponse)
            .collect(Collectors.toList());
        
        return new TransferenciaResponse(
            transferencia.getIdTransferencia(),
            transferencia.getBodegaOrigen().getNombre(),
            transferencia.getBodegaDestino().getNombre(),
            transferencia.getUsuario().getNombres() + " " + transferencia.getUsuario().getApellidos(),
            transferencia.getFechaSolicitud(),
            transferencia.getFechaRecibo(),
            transferencia.getEstado().name(),
            transferencia.getObservaciones(),
            detalles
        );
    }
    
    private DetalleTransferenciaResponse toDetalleTransferenciaResponse(DetalleTransferencia detalle) {
        return new DetalleTransferenciaResponse(
            detalle.getIdDetalleTransferencia(),
            detalle.getProducto().getNombre(),
            detalle.getProducto().getCodigoBarras(),
            detalle.getCantidad()
        );
    }
}

