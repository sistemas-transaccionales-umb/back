package com.umb.sistema.service;

import com.umb.sistema.dto.ProductoDTO.*;
import com.umb.sistema.entity.Categoria;
import com.umb.sistema.entity.Producto;
import com.umb.sistema.exception.BusinessException;
import com.umb.sistema.exception.ResourceNotFoundException;
import com.umb.sistema.repository.CategoriaRepository;
import com.umb.sistema.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {
    
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    
    @Transactional(rollbackFor = Exception.class)
    public ProductoResponse crearProducto(ProductoRequest request) {
        if (productoRepository.existsByCodigoBarras(request.codigoBarras())) {
            throw new BusinessException("Ya existe un producto con el código de barras: " + request.codigoBarras());
        }
        
        Categoria categoria = categoriaRepository.findById(request.idCategoria())
            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + request.idCategoria()));
        
        Producto producto = new Producto();
        producto.setCategoria(categoria);
        producto.setCodigoBarras(request.codigoBarras());
        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecioCompra(request.precioCompra());
        producto.setPrecioVenta(request.precioVenta());
        producto.setPorcentajeIva(request.porcentajeIva());
        producto.setEstado(Producto.EstadoProducto.ACTIVO);
        
        return toProductoResponse(productoRepository.save(producto));
    }
    
    @Transactional(rollbackFor = Exception.class)
    public ProductoResponse actualizarProducto(Integer idProducto, ProductoRequest request) {
        Producto producto = productoRepository.findById(idProducto)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + idProducto));
        
        if (!producto.getCodigoBarras().equals(request.codigoBarras()) &&
            productoRepository.existsByCodigoBarras(request.codigoBarras())) {
            throw new BusinessException("Ya existe un producto con el código de barras: " + request.codigoBarras());
        }
        
        Categoria categoria = categoriaRepository.findById(request.idCategoria())
            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + request.idCategoria()));
        
        producto.setCategoria(categoria);
        producto.setCodigoBarras(request.codigoBarras());
        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecioCompra(request.precioCompra());
        producto.setPrecioVenta(request.precioVenta());
        producto.setPorcentajeIva(request.porcentajeIva());
        
        return toProductoResponse(productoRepository.save(producto));
    }
    
    @Transactional(readOnly = true)
    public ProductoResponse obtenerProducto(Integer idProducto) {
        Producto producto = productoRepository.findById(idProducto)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + idProducto));
        return toProductoResponse(producto);
    }
    
    @Transactional(readOnly = true)
    public List<ProductoResponse> obtenerTodosLosProductos() {
        return productoRepository.findAll().stream()
            .map(this::toProductoResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductoResponse> obtenerProductosActivos() {
        return productoRepository.findAllActive().stream()
            .map(this::toProductoResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void eliminarProducto(Integer idProducto) {
        Producto producto = productoRepository.findById(idProducto)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + idProducto));
        
        producto.setEstado(Producto.EstadoProducto.INACTIVO);
        productoRepository.save(producto);
    }
    
    private ProductoResponse toProductoResponse(Producto producto) {
        return new ProductoResponse(
            producto.getIdProducto(),
            producto.getCategoria().getNombreCategoria(),
            producto.getCodigoBarras(),
            producto.getNombre(),
            producto.getDescripcion(),
            producto.getPrecioCompra(),
            producto.getPrecioVenta(),
            producto.getPorcentajeIva(),
            producto.getEstado().name(),
            producto.getFechaCreacion()
        );
    }
}

