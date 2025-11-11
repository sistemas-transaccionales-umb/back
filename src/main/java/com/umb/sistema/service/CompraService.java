package com.umb.sistema.service;

import com.umb.sistema.dto.CompraDTO.*;
import com.umb.sistema.dto.ProveedorDTO;
import com.umb.sistema.entity.*;
import com.umb.sistema.exception.BusinessException;
import com.umb.sistema.exception.ResourceNotFoundException;
import com.umb.sistema.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompraService {
    
    private final CompraRepository compraRepository;
    private final DetalleCompraRepository detalleCompraRepository;
    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final BodegaRepository bodegaRepository;
    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final ProveedorService proveedorService;
    
    @Transactional(rollbackFor = Exception.class)
    public CompraResponse crearCompra(CompraRequest request) {
        // Validar que el número de compra no exista
        if (compraRepository.existsByNumeroCompra(request.numeroCompra())) {
            throw new BusinessException("Ya existe una compra con el número: " + request.numeroCompra());
        }
        
        // Validar proveedor
        Proveedor proveedor = proveedorRepository.findById(request.idProveedor())
            .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + request.idProveedor()));
        
        if (proveedor.getEstado() != Proveedor.EstadoProveedor.ACTIVO) {
            throw new BusinessException("El proveedor no está activo");
        }
        
        // Validar usuario
        Usuario usuario = usuarioRepository.findById(request.idUsuario())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + request.idUsuario()));
        
        // Crear compra
        Compra compra = new Compra();
        compra.setProveedor(proveedor);
        compra.setUsuario(usuario);
        compra.setNumeroCompra(request.numeroCompra());
        compra.setFechaCompra(request.fechaCompra());
        compra.setObservaciones(request.observaciones());
        compra.setEstado(Compra.EstadoCompra.PENDIENTE);
        
        // Procesar detalles
        BigDecimal subtotalTotal = BigDecimal.ZERO;
        BigDecimal ivaTotal = BigDecimal.ZERO;
        
        for (DetalleCompraRequest detalleReq : request.detalles()) {
            // Validar producto
            Producto producto = productoRepository.findById(detalleReq.idProducto())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + detalleReq.idProducto()));
            
            // Validar bodega
            Bodega bodega = bodegaRepository.findById(detalleReq.idBodega())
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con ID: " + detalleReq.idBodega()));
            
            if (bodega.getEstado() != Bodega.EstadoBodega.ACTIVA) {
                throw new BusinessException("La bodega " + bodega.getNombre() + " no está activa");
            }
            
            // Crear detalle
            DetalleCompra detalle = new DetalleCompra();
            detalle.setCompra(compra);
            detalle.setProducto(producto);
            detalle.setBodega(bodega);
            detalle.setCantidad(detalleReq.cantidad());
            detalle.setPrecioUnitarioCompra(detalleReq.precioUnitarioCompra());
            
            // Calcular totales del detalle (se calcula automáticamente en @PrePersist)
            detalle.calcularTotales();
            
            compra.agregarDetalle(detalle);
            
            // Acumular totales
            subtotalTotal = subtotalTotal.add(detalle.getSubtotalLinea());
            ivaTotal = ivaTotal.add(detalle.getTotalIvaLinea());
        }
        
        compra.setSubtotal(subtotalTotal);
        compra.setTotalIva(ivaTotal);
        compra.setTotalCompra(subtotalTotal.add(ivaTotal));
        
        Compra compraSaved = compraRepository.save(compra);
        
        return toCompraResponse(compraSaved);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public CompraResponse recibirCompra(Long idCompra) {
        Compra compra = compraRepository.findById(idCompra)
            .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con ID: " + idCompra));
        
        if (compra.getEstado() != Compra.EstadoCompra.PENDIENTE) {
            throw new BusinessException("Solo se pueden recibir compras en estado PENDIENTE");
        }
        
        // Actualizar inventario por cada detalle
        for (DetalleCompra detalle : compra.getDetalles()) {
            actualizarInventario(detalle);
        }
        
        compra.setEstado(Compra.EstadoCompra.RECIBIDA);
        Compra compraActualizada = compraRepository.save(compra);
        
        return toCompraResponse(compraActualizada);
    }
    
    private void actualizarInventario(DetalleCompra detalle) {
        Producto producto = detalle.getProducto();
        Bodega bodega = detalle.getBodega();
        Integer cantidadCompra = detalle.getCantidad();
        
        // Buscar o crear inventario
        Inventario inventario = inventarioRepository
            .findByProducto_IdProductoAndBodega_IdBodega(producto.getIdProducto(), bodega.getIdBodega())
            .orElseGet(() -> {
                Inventario nuevoInv = new Inventario();
                nuevoInv.setProducto(producto);
                nuevoInv.setBodega(bodega);
                nuevoInv.setCantidad(0);
                nuevoInv.setStockMinimo(0);
                return nuevoInv;
            });
        
        // Guardar cantidad anterior para el movimiento
        Integer cantidadAnterior = inventario.getCantidad();
        
        // Actualizar cantidad disponible
        inventario.setCantidad(inventario.getCantidad() + cantidadCompra);
        inventarioRepository.save(inventario);
        
        // Registrar movimiento de inventario
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setBodega(bodega);
        movimiento.setTipoMovimiento(MovimientoInventario.TipoMovimiento.ENTRADA);
        movimiento.setCantidadAnterior(cantidadAnterior);
        movimiento.setCantidadNueva(inventario.getCantidad());
        movimiento.setMotivo("Compra recibida - Número: " + detalle.getCompra().getNumeroCompra());
        movimiento.setReferencia(detalle.getCompra().getNumeroCompra());
        
        movimientoInventarioRepository.save(movimiento);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public CompraResponse cancelarCompra(Long idCompra, String motivo) {
        Compra compra = compraRepository.findById(idCompra)
            .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con ID: " + idCompra));
        
        if (compra.getEstado() == Compra.EstadoCompra.RECIBIDA) {
            throw new BusinessException("No se puede cancelar una compra ya recibida");
        }
        
        compra.setEstado(Compra.EstadoCompra.CANCELADA);
        
        if (motivo != null && !motivo.isEmpty()) {
            String observacionesActuales = compra.getObservaciones() != null ? compra.getObservaciones() : "";
            compra.setObservaciones(observacionesActuales + "\nCANCELADA: " + motivo);
        }
        
        Compra compraActualizada = compraRepository.save(compra);
        
        return toCompraResponse(compraActualizada);
    }
    
    @Transactional(readOnly = true)
    public CompraResponse obtenerCompra(Long idCompra) {
        Compra compra = compraRepository.findById(idCompra)
            .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con ID: " + idCompra));
        return toCompraResponse(compra);
    }
    
    @Transactional(readOnly = true)
    public List<CompraResponse> obtenerTodasLasCompras() {
        return compraRepository.findAll().stream()
            .map(this::toCompraResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<CompraResponse> obtenerComprasPorProveedor(Integer idProveedor) {
        return compraRepository.findByProveedor_IdProveedor(idProveedor).stream()
            .map(this::toCompraResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<CompraResponse> obtenerComprasPorEstado(Compra.EstadoCompra estado) {
        return compraRepository.findByEstado(estado).stream()
            .map(this::toCompraResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<CompraResponse> obtenerComprasPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return compraRepository.findByFechaCompraBetween(fechaInicio, fechaFin).stream()
            .map(this::toCompraResponse)
            .collect(Collectors.toList());
    }
    
    private CompraResponse toCompraResponse(Compra compra) {
        ProveedorDTO.ProveedorResponse proveedorResp = proveedorService.obtenerProveedor(compra.getProveedor().getIdProveedor());
        
        UsuarioBasicResponse usuarioResp = new UsuarioBasicResponse(
            compra.getUsuario().getIdUsuario(),
            compra.getUsuario().getNombres(),
            compra.getUsuario().getApellidos(),
            compra.getUsuario().getEmail()
        );
        
        List<DetalleCompraResponse> detallesResp = compra.getDetalles().stream()
            .map(this::toDetalleCompraResponse)
            .collect(Collectors.toList());
        
        return new CompraResponse(
            compra.getIdCompra(),
            proveedorResp,
            usuarioResp,
            compra.getNumeroCompra(),
            compra.getFechaCompra(),
            compra.getSubtotal(),
            compra.getTotalIva(),
            compra.getTotalCompra(),
            compra.getEstado().name(),
            compra.getObservaciones(),
            compra.getFechaCreacion(),
            detallesResp
        );
    }
    
    private DetalleCompraResponse toDetalleCompraResponse(DetalleCompra detalle) {
        ProductoBasicResponse productoResp = new ProductoBasicResponse(
            detalle.getProducto().getIdProducto(),
            detalle.getProducto().getCodigoBarras(),
            detalle.getProducto().getNombre(),
            detalle.getProducto().getPorcentajeIva()
        );
        
        BodegaBasicResponse bodegaResp = new BodegaBasicResponse(
            detalle.getBodega().getIdBodega(),
            detalle.getBodega().getNombre()
        );
        
        return new DetalleCompraResponse(
            detalle.getIdDetalleCompra(),
            productoResp,
            bodegaResp,
            detalle.getCantidad(),
            detalle.getPrecioUnitarioCompra(),
            detalle.getSubtotalLinea(),
            detalle.getTotalIvaLinea(),
            detalle.getTotalLinea()
        );
    }
}

