package com.umb.sistema.service;

import com.umb.sistema.dto.VentaDTO.*;
import com.umb.sistema.entity.*;
import com.umb.sistema.exception.BusinessException;
import com.umb.sistema.exception.InsufficientStockException;
import com.umb.sistema.exception.ResourceNotFoundException;
import com.umb.sistema.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VentaService {
    
    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    
    /**
     * Crear una venta - Operación transaccional ACID crítica
     * - Crea la venta y sus detalles
     * - Descuenta el inventario
     * - Registra movimientos de inventario
     * - Rollback automático si falla cualquier operación
     */
    @Transactional(rollbackFor = Exception.class)
    public VentaResponse crearVenta(VentaRequest request) {
        log.info("Iniciando creación de venta {}", request.numeroFactura());
        
        // Validar cliente
        Cliente cliente = clienteRepository.findById(request.idCliente())
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + request.idCliente()));
        
        if (cliente.getEstado() != Cliente.EstadoCliente.ACTIVO) {
            throw new BusinessException("El cliente no está activo");
        }
        
        // Validar usuario
        Usuario usuario = usuarioRepository.findById(request.idUsuario())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + request.idUsuario()));
        
        // Validar número de factura único
        if (ventaRepository.existsByNumeroFactura(request.numeroFactura())) {
            throw new BusinessException("Ya existe una venta con el número de factura: " + request.numeroFactura());
        }
        
        // Crear entidad venta
        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setUsuario(usuario);
        venta.setNumeroFactura(request.numeroFactura());
        venta.setTotalDescuento(request.totalDescuento() != null ? request.totalDescuento() : BigDecimal.ZERO);
        venta.setObservaciones(request.observaciones());
        venta.setOlaCode(request.olaCode());
        venta.setEstadoPago(Venta.EstadoPago.PENDIENTE);
        
        // Calcular total y crear detalles
        BigDecimal totalVenta = BigDecimal.ZERO;
        
        for (DetalleVentaRequest detalleReq : request.detalles()) {
            Producto producto = productoRepository.findById(detalleReq.idProducto())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + detalleReq.idProducto()));
            
            if (producto.getEstado() != Producto.EstadoProducto.ACTIVO) {
                throw new BusinessException("El producto " + producto.getNombre() + " no está activo");
            }
            
            // Verificar inventario disponible (asumimos bodega principal con ID 1)
            Inventario inventario = inventarioRepository.findByProducto_IdProductoAndBodega_IdBodega(
                producto.getIdProducto(), 1)
                .orElseThrow(() -> new InsufficientStockException(
                    "No hay inventario disponible para el producto: " + producto.getNombre()));
            
            if (inventario.getCantidad() < detalleReq.cantidad()) {
                throw new InsufficientStockException(
                    "Stock insuficiente para el producto: " + producto.getNombre() + 
                    ". Disponible: " + inventario.getCantidad() + ", Solicitado: " + detalleReq.cantidad());
            }
            
            // Crear detalle de venta
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProducto(producto);
            detalle.setCantidad(detalleReq.cantidad());
            detalle.setPrecioUnitario(detalleReq.precioUnitario());
            
            // Los totales se calculan automáticamente con @PrePersist
            detalle.calcularTotales();
            
            venta.addDetalle(detalle);
            totalVenta = totalVenta.add(detalle.getTotalLinea());
            
            // Descontar del inventario
            int cantidadAnterior = inventario.getCantidad();
            inventario.setCantidad(cantidadAnterior - detalleReq.cantidad());
            inventarioRepository.save(inventario);
            
            // Registrar movimiento de inventario
            MovimientoInventario movimiento = new MovimientoInventario();
            movimiento.setProducto(producto);
            movimiento.setBodega(inventario.getBodega());
            movimiento.setTipoMovimiento(MovimientoInventario.TipoMovimiento.SALIDA);
            movimiento.setCantidadAnterior(cantidadAnterior);
            movimiento.setCantidadNueva(inventario.getCantidad());
            movimiento.setMotivo("Venta - Factura: " + request.numeroFactura());
            movimiento.setReferencia(request.numeroFactura());
            movimientoInventarioRepository.save(movimiento);
        }
        
        // Aplicar descuento
        venta.setTotalVenta(totalVenta.subtract(venta.getTotalDescuento()));
        
        // Guardar venta
        Venta ventaGuardada = ventaRepository.save(venta);
        
        log.info("Venta creada exitosamente: {}", ventaGuardada.getIdVenta());
        
        return toVentaResponse(ventaGuardada);
    }
    
    @Transactional(readOnly = true)
    public VentaResponse obtenerVenta(Integer idVenta) {
        Venta venta = ventaRepository.findById(idVenta)
            .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con ID: " + idVenta));
        return toVentaResponse(venta);
    }
    
    @Transactional(readOnly = true)
    public List<VentaResponse> obtenerTodasLasVentas() {
        return ventaRepository.findAll().stream()
            .map(this::toVentaResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(rollbackFor = Exception.class)
    public VentaResponse actualizarEstadoPago(Integer idVenta, Venta.EstadoPago nuevoEstado) {
        Venta venta = ventaRepository.findById(idVenta)
            .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con ID: " + idVenta));
        
        venta.setEstadoPago(nuevoEstado);
        Venta ventaActualizada = ventaRepository.save(venta);
        
        log.info("Estado de pago actualizado para venta {} a {}", idVenta, nuevoEstado);
        
        return toVentaResponse(ventaActualizada);
    }
    
    @Transactional(readOnly = true)
    public List<VentaResponse> obtenerVentasPorCliente(Integer idCliente) {
        return ventaRepository.findByCliente_IdCliente(idCliente).stream()
            .map(this::toVentaResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<VentaResponse> obtenerVentasPorPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ventaRepository.findByFechaVentaBetween(fechaInicio, fechaFin).stream()
            .map(this::toVentaResponse)
            .collect(Collectors.toList());
    }
    
    // Métodos de mapeo
    private VentaResponse toVentaResponse(Venta venta) {
        List<DetalleVentaResponse> detalles = venta.getDetalles().stream()
            .map(this::toDetalleVentaResponse)
            .collect(Collectors.toList());
        
        return new VentaResponse(
            venta.getIdVenta(),
            venta.getCliente().getNombre() + " " + venta.getCliente().getApellidos(),
            venta.getUsuario().getNombres() + " " + venta.getUsuario().getApellidos(),
            venta.getNumeroFactura(),
            venta.getFechaVenta(),
            venta.getTotalDescuento(),
            venta.getTotalVenta(),
            venta.getObservaciones(),
            venta.getOlaCode(),
            venta.getEstadoPago().name(),
            detalles
        );
    }
    
    private DetalleVentaResponse toDetalleVentaResponse(DetalleVenta detalle) {
        return new DetalleVentaResponse(
            detalle.getIdDetalleVenta(),
            detalle.getProducto().getNombre(),
            detalle.getProducto().getCodigoBarras(),
            detalle.getCantidad(),
            detalle.getPrecioUnitario(),
            detalle.getSubtotalLinea(),
            detalle.getTotalIvaLinea(),
            detalle.getTotalLinea()
        );
    }
}

