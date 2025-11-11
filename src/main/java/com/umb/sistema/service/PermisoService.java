package com.umb.sistema.service;

import com.umb.sistema.dto.PermisoDTO.*;
import com.umb.sistema.entity.Permiso;
import com.umb.sistema.exception.BusinessException;
import com.umb.sistema.exception.ResourceNotFoundException;
import com.umb.sistema.repository.PermisoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermisoService {
    
    private final PermisoRepository permisoRepository;
    
    @Transactional(rollbackFor = Exception.class)
    public PermisoResponse crearPermiso(PermisoRequest request) {
        if (permisoRepository.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe un permiso con el nombre: " + request.getNombre());
        }
        
        Permiso permiso = new Permiso();
        permiso.setNombre(request.getNombre());
        permiso.setDescripcion(request.getDescripcion());
        
        return toPermisoResponse(permisoRepository.save(permiso));
    }
    
    @Transactional(rollbackFor = Exception.class)
    public PermisoResponse actualizarPermiso(Integer idPermiso, PermisoRequest request) {
        Permiso permiso = permisoRepository.findById(idPermiso)
            .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + idPermiso));
        
        if (!permiso.getNombre().equals(request.getNombre()) &&
            permisoRepository.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe un permiso con el nombre: " + request.getNombre());
        }
        
        permiso.setNombre(request.getNombre());
        permiso.setDescripcion(request.getDescripcion());
        
        return toPermisoResponse(permisoRepository.save(permiso));
    }
    
    @Transactional(readOnly = true)
    public PermisoResponse obtenerPermiso(Integer idPermiso) {
        Permiso permiso = permisoRepository.findById(idPermiso)
            .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + idPermiso));
        return toPermisoResponse(permiso);
    }
    
    @Transactional(readOnly = true)
    public List<PermisoResponse> obtenerTodosLosPermisos() {
        return permisoRepository.findAll().stream()
            .map(this::toPermisoResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void eliminarPermiso(Integer idPermiso) {
        Permiso permiso = permisoRepository.findById(idPermiso)
            .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + idPermiso));
        
        permisoRepository.delete(permiso);
    }
    
    public PermisoResponse toPermisoResponse(Permiso permiso) {
        return new PermisoResponse(
            permiso.getIdPermiso(),
            permiso.getNombre(),
            permiso.getDescripcion()
        );
    }
}

