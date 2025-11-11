package com.umb.sistema.service;

import com.umb.sistema.dto.RolDTO.*;
import com.umb.sistema.entity.Permiso;
import com.umb.sistema.entity.Rol;
import com.umb.sistema.exception.BusinessException;
import com.umb.sistema.exception.ResourceNotFoundException;
import com.umb.sistema.repository.PermisoRepository;
import com.umb.sistema.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolService {
    
    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final PermisoService permisoService;
    
    @Transactional(rollbackFor = Exception.class)
    public RolResponse crearRol(RolRequest request) {
        if (rolRepository.existsByNombreRol(request.nombreRol())) {
            throw new BusinessException("Ya existe un rol con el nombre: " + request.nombreRol());
        }
        
        Rol rol = new Rol();
        rol.setNombreRol(request.nombreRol());
        rol.setDescripcion(request.descripcion());
        
        // Asignar permisos si se proporcionaron
        if (request.idsPermisos() != null && !request.idsPermisos().isEmpty()) {
            List<Permiso> permisos = obtenerPermisosPorIds(request.idsPermisos());
            rol.setPermisos(permisos);
        }
        
        return toRolResponse(rolRepository.save(rol));
    }
    
    @Transactional(rollbackFor = Exception.class)
    public RolResponse actualizarRol(Integer idRol, RolRequest request) {
        Rol rol = rolRepository.findById(idRol)
            .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + idRol));
        
        if (!rol.getNombreRol().equals(request.nombreRol()) &&
            rolRepository.existsByNombreRol(request.nombreRol())) {
            throw new BusinessException("Ya existe un rol con el nombre: " + request.nombreRol());
        }
        
        rol.setNombreRol(request.nombreRol());
        rol.setDescripcion(request.descripcion());
        
        // Actualizar permisos
        if (request.idsPermisos() != null) {
            List<Permiso> permisos = obtenerPermisosPorIds(request.idsPermisos());
            rol.setPermisos(permisos);
        }
        
        return toRolResponse(rolRepository.save(rol));
    }
    
    @Transactional(readOnly = true)
    public RolResponse obtenerRol(Integer idRol) {
        Rol rol = rolRepository.findById(idRol)
            .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + idRol));
        return toRolResponse(rol);
    }
    
    @Transactional(readOnly = true)
    public List<RolResponse> obtenerTodosLosRoles() {
        return rolRepository.findAll().stream()
            .map(this::toRolResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void eliminarRol(Integer idRol) {
        Rol rol = rolRepository.findById(idRol)
            .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + idRol));
        
        rolRepository.delete(rol);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public RolResponse agregarPermisosARol(Integer idRol, List<Integer> idsPermisos) {
        Rol rol = rolRepository.findById(idRol)
            .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + idRol));
        
        List<Permiso> permisosNuevos = obtenerPermisosPorIds(idsPermisos);
        
        // Evitar duplicados
        for (Permiso permiso : permisosNuevos) {
            if (!rol.getPermisos().contains(permiso)) {
                rol.getPermisos().add(permiso);
            }
        }
        
        return toRolResponse(rolRepository.save(rol));
    }
    
    @Transactional(rollbackFor = Exception.class)
    public RolResponse removerPermisosDeRol(Integer idRol, List<Integer> idsPermisos) {
        Rol rol = rolRepository.findById(idRol)
            .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + idRol));
        
        List<Permiso> permisosARemover = obtenerPermisosPorIds(idsPermisos);
        rol.getPermisos().removeAll(permisosARemover);
        
        return toRolResponse(rolRepository.save(rol));
    }
    
    private List<Permiso> obtenerPermisosPorIds(List<Integer> idsPermisos) {
        List<Permiso> permisos = new ArrayList<>();
        for (Integer idPermiso : idsPermisos) {
            Permiso permiso = permisoRepository.findById(idPermiso)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + idPermiso));
            permisos.add(permiso);
        }
        return permisos;
    }
    
    public RolResponse toRolResponse(Rol rol) {
        return new RolResponse(
            rol.getIdRol(),
            rol.getNombreRol(),
            rol.getDescripcion(),
            rol.getPermisos().stream()
                .map(permisoService::toPermisoResponse)
                .collect(Collectors.toList())
        );
    }
}

