package com.umb.sistema.service;

import com.umb.sistema.dto.BodegaDTO.*;
import com.umb.sistema.entity.Bodega;
import com.umb.sistema.exception.BusinessException;
import com.umb.sistema.exception.ResourceNotFoundException;
import com.umb.sistema.repository.BodegaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BodegaService {
    
    private final BodegaRepository bodegaRepository;
    
    @Transactional(rollbackFor = Exception.class)
    public BodegaResponse crearBodega(BodegaRequest request) {
        if (bodegaRepository.existsByNombre(request.nombre())) {
            throw new BusinessException("Ya existe una bodega con el nombre: " + request.nombre());
        }
        
        Bodega bodega = new Bodega();
        bodega.setNombre(request.nombre());
        bodega.setUbicacion(request.ubicacion());
        bodega.setEstado(Bodega.EstadoBodega.ACTIVA);
        
        return toBodegaResponse(bodegaRepository.save(bodega));
    }
    
    @Transactional(rollbackFor = Exception.class)
    public BodegaResponse actualizarBodega(Integer idBodega, BodegaRequest request) {
        Bodega bodega = bodegaRepository.findById(idBodega)
            .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con ID: " + idBodega));
        
        if (!bodega.getNombre().equals(request.nombre()) &&
            bodegaRepository.existsByNombre(request.nombre())) {
            throw new BusinessException("Ya existe una bodega con el nombre: " + request.nombre());
        }
        
        bodega.setNombre(request.nombre());
        bodega.setUbicacion(request.ubicacion());
        
        return toBodegaResponse(bodegaRepository.save(bodega));
    }
    
    @Transactional(readOnly = true)
    public BodegaResponse obtenerBodega(Integer idBodega) {
        Bodega bodega = bodegaRepository.findById(idBodega)
            .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con ID: " + idBodega));
        return toBodegaResponse(bodega);
    }
    
    @Transactional(readOnly = true)
    public List<BodegaResponse> obtenerTodasLasBodegas() {
        return bodegaRepository.findAll().stream()
            .map(this::toBodegaResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<BodegaResponse> obtenerBodegasActivas() {
        return bodegaRepository.findAllActive().stream()
            .map(this::toBodegaResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void eliminarBodega(Integer idBodega) {
        Bodega bodega = bodegaRepository.findById(idBodega)
            .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con ID: " + idBodega));
        
        bodega.setEstado(Bodega.EstadoBodega.INACTIVA);
        bodegaRepository.save(bodega);
    }
    
    private BodegaResponse toBodegaResponse(Bodega bodega) {
        return new BodegaResponse(
            bodega.getIdBodega(),
            bodega.getNombre(),
            bodega.getUbicacion(),
            bodega.getEstado().name(),
            bodega.getFechaCreacion()
        );
    }
}

