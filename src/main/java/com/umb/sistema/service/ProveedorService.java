package com.umb.sistema.service;

import com.umb.sistema.dto.ProveedorDTO.*;
import com.umb.sistema.entity.Proveedor;
import com.umb.sistema.exception.BusinessException;
import com.umb.sistema.exception.ResourceNotFoundException;
import com.umb.sistema.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProveedorService {
    
    private final ProveedorRepository proveedorRepository;
    
    @Transactional(rollbackFor = Exception.class)
    public ProveedorResponse crearProveedor(ProveedorRequest request) {
        if (proveedorRepository.existsByNitRuc(request.nitRuc())) {
            throw new BusinessException("Ya existe un proveedor con el NIT/RUC: " + request.nitRuc());
        }
        
        Proveedor proveedor = new Proveedor();
        proveedor.setNitRuc(request.nitRuc());
        proveedor.setNombre(request.nombre());
        proveedor.setNombreContacto(request.nombreContacto());
        proveedor.setDireccion(request.direccion());
        proveedor.setTelefono(request.telefono());
        proveedor.setEmail(request.email());
        proveedor.setObservaciones(request.observaciones());
        proveedor.setEstado(Proveedor.EstadoProveedor.ACTIVO);
        
        return toProveedorResponse(proveedorRepository.save(proveedor));
    }
    
    @Transactional(rollbackFor = Exception.class)
    public ProveedorResponse actualizarProveedor(Integer idProveedor, ProveedorRequest request) {
        Proveedor proveedor = proveedorRepository.findById(idProveedor)
            .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + idProveedor));
        
        if (!proveedor.getNitRuc().equals(request.nitRuc()) &&
            proveedorRepository.existsByNitRuc(request.nitRuc())) {
            throw new BusinessException("Ya existe un proveedor con el NIT/RUC: " + request.nitRuc());
        }
        
        proveedor.setNitRuc(request.nitRuc());
        proveedor.setNombre(request.nombre());
        proveedor.setNombreContacto(request.nombreContacto());
        proveedor.setDireccion(request.direccion());
        proveedor.setTelefono(request.telefono());
        proveedor.setEmail(request.email());
        proveedor.setObservaciones(request.observaciones());
        
        return toProveedorResponse(proveedorRepository.save(proveedor));
    }
    
    @Transactional(readOnly = true)
    public ProveedorResponse obtenerProveedor(Integer idProveedor) {
        Proveedor proveedor = proveedorRepository.findById(idProveedor)
            .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + idProveedor));
        return toProveedorResponse(proveedor);
    }
    
    @Transactional(readOnly = true)
    public List<ProveedorResponse> obtenerTodosLosProveedores() {
        return proveedorRepository.findAll().stream()
            .map(this::toProveedorResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProveedorResponse> obtenerProveedoresActivos() {
        return proveedorRepository.findAllActive().stream()
            .map(this::toProveedorResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void cambiarEstadoProveedor(Integer idProveedor, Proveedor.EstadoProveedor nuevoEstado) {
        Proveedor proveedor = proveedorRepository.findById(idProveedor)
            .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + idProveedor));
        
        proveedor.setEstado(nuevoEstado);
        proveedorRepository.save(proveedor);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void eliminarProveedor(Integer idProveedor) {
        Proveedor proveedor = proveedorRepository.findById(idProveedor)
            .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + idProveedor));
        
        // Eliminación lógica
        proveedor.setEstado(Proveedor.EstadoProveedor.INACTIVO);
        proveedorRepository.save(proveedor);
    }
    
    private ProveedorResponse toProveedorResponse(Proveedor proveedor) {
        return new ProveedorResponse(
            proveedor.getIdProveedor(),
            proveedor.getNitRuc(),
            proveedor.getNombre(),
            proveedor.getNombreContacto(),
            proveedor.getDireccion(),
            proveedor.getTelefono(),
            proveedor.getEmail(),
            proveedor.getEstado().name(),
            proveedor.getObservaciones(),
            proveedor.getFechaCreacion()
        );
    }
}

