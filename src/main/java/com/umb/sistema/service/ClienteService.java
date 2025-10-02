package com.umb.sistema.service;

import com.umb.sistema.dto.ClienteDTO.*;
import com.umb.sistema.entity.Cliente;
import com.umb.sistema.exception.BusinessException;
import com.umb.sistema.exception.ResourceNotFoundException;
import com.umb.sistema.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {
    
    private final ClienteRepository clienteRepository;
    
    @Transactional(rollbackFor = Exception.class)
    public ClienteResponse crearCliente(ClienteRequest request) {
        if (clienteRepository.existsByNumeroDocumento(request.numeroDocumento())) {
            throw new BusinessException("Ya existe un cliente con el número de documento: " + request.numeroDocumento());
        }
        
        if (request.email() != null && clienteRepository.existsByEmail(request.email())) {
            throw new BusinessException("Ya existe un cliente con el email: " + request.email());
        }
        
        Cliente cliente = new Cliente();
        cliente.setTipoDocumento(request.tipoDocumento());
        cliente.setNumeroDocumento(request.numeroDocumento());
        cliente.setNombre(request.nombre());
        cliente.setApellidos(request.apellidos());
        cliente.setDireccion(request.direccion());
        cliente.setTelefono(request.telefono());
        cliente.setEmail(request.email());
        cliente.setContrasenaHash(request.contrasenaHash());
        cliente.setEstado(Cliente.EstadoCliente.ACTIVO);
        
        return toClienteResponse(clienteRepository.save(cliente));
    }
    
    @Transactional(rollbackFor = Exception.class)
    public ClienteResponse actualizarCliente(Integer idCliente, ClienteRequest request) {
        Cliente cliente = clienteRepository.findById(idCliente)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + idCliente));
        
        if (!cliente.getNumeroDocumento().equals(request.numeroDocumento()) &&
            clienteRepository.existsByNumeroDocumento(request.numeroDocumento())) {
            throw new BusinessException("Ya existe un cliente con el número de documento: " + request.numeroDocumento());
        }
        
        if (request.email() != null && !request.email().equals(cliente.getEmail()) &&
            clienteRepository.existsByEmail(request.email())) {
            throw new BusinessException("Ya existe un cliente con el email: " + request.email());
        }
        
        cliente.setTipoDocumento(request.tipoDocumento());
        cliente.setNumeroDocumento(request.numeroDocumento());
        cliente.setNombre(request.nombre());
        cliente.setApellidos(request.apellidos());
        cliente.setDireccion(request.direccion());
        cliente.setTelefono(request.telefono());
        cliente.setEmail(request.email());
        if (request.contrasenaHash() != null) {
            cliente.setContrasenaHash(request.contrasenaHash());
        }
        
        return toClienteResponse(clienteRepository.save(cliente));
    }
    
    @Transactional(readOnly = true)
    public ClienteResponse obtenerCliente(Integer idCliente) {
        Cliente cliente = clienteRepository.findById(idCliente)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + idCliente));
        return toClienteResponse(cliente);
    }
    
    @Transactional(readOnly = true)
    public List<ClienteResponse> obtenerTodosLosClientes() {
        return clienteRepository.findAll().stream()
            .map(this::toClienteResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ClienteResponse> obtenerClientesActivos() {
        return clienteRepository.findAllActive().stream()
            .map(this::toClienteResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void eliminarCliente(Integer idCliente) {
        Cliente cliente = clienteRepository.findById(idCliente)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + idCliente));
        
        cliente.setEstado(Cliente.EstadoCliente.INACTIVO);
        clienteRepository.save(cliente);
    }
    
    private ClienteResponse toClienteResponse(Cliente cliente) {
        return new ClienteResponse(
            cliente.getIdCliente(),
            cliente.getTipoDocumento(),
            cliente.getNumeroDocumento(),
            cliente.getNombre(),
            cliente.getApellidos(),
            cliente.getDireccion(),
            cliente.getTelefono(),
            cliente.getEmail(),
            cliente.getEstado().name(),
            cliente.getFechaCreacion()
        );
    }
}

