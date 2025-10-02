package com.umb.sistema.service;

import com.umb.sistema.dto.CategoriaDTO.*;
import com.umb.sistema.entity.Categoria;
import com.umb.sistema.exception.BusinessException;
import com.umb.sistema.exception.ResourceNotFoundException;
import com.umb.sistema.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {
    
    private final CategoriaRepository categoriaRepository;
    
    @Transactional(rollbackFor = Exception.class)
    public CategoriaResponse crearCategoria(CategoriaRequest request) {
        if (categoriaRepository.existsByNombreCategoria(request.nombreCategoria())) {
            throw new BusinessException("Ya existe una categoría con el nombre: " + request.nombreCategoria());
        }
        
        Categoria categoria = new Categoria();
        categoria.setNombreCategoria(request.nombreCategoria());
        categoria.setDescripcion(request.descripcion());
        
        return toCategoriaResponse(categoriaRepository.save(categoria));
    }
    
    @Transactional(rollbackFor = Exception.class)
    public CategoriaResponse actualizarCategoria(Integer idCategoria, CategoriaRequest request) {
        Categoria categoria = categoriaRepository.findById(idCategoria)
            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + idCategoria));
        
        if (!categoria.getNombreCategoria().equals(request.nombreCategoria()) &&
            categoriaRepository.existsByNombreCategoria(request.nombreCategoria())) {
            throw new BusinessException("Ya existe una categoría con el nombre: " + request.nombreCategoria());
        }
        
        categoria.setNombreCategoria(request.nombreCategoria());
        categoria.setDescripcion(request.descripcion());
        
        return toCategoriaResponse(categoriaRepository.save(categoria));
    }
    
    @Transactional(readOnly = true)
    public CategoriaResponse obtenerCategoria(Integer idCategoria) {
        Categoria categoria = categoriaRepository.findById(idCategoria)
            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + idCategoria));
        return toCategoriaResponse(categoria);
    }
    
    @Transactional(readOnly = true)
    public List<CategoriaResponse> obtenerTodasLasCategorias() {
        return categoriaRepository.findAll().stream()
            .map(this::toCategoriaResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void eliminarCategoria(Integer idCategoria) {
        Categoria categoria = categoriaRepository.findById(idCategoria)
            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + idCategoria));
        
        categoriaRepository.delete(categoria);
    }
    
    private CategoriaResponse toCategoriaResponse(Categoria categoria) {
        return new CategoriaResponse(
            categoria.getIdCategoria(),
            categoria.getNombreCategoria(),
            categoria.getDescripcion()
        );
    }
}

