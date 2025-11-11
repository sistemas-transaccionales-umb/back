package com.umb.sistema.service;

import com.umb.sistema.dto.UsuarioDTO.*;
import com.umb.sistema.entity.Rol;
import com.umb.sistema.entity.Usuario;
import com.umb.sistema.exception.BusinessException;
import com.umb.sistema.exception.ResourceNotFoundException;
import com.umb.sistema.repository.RolRepository;
import com.umb.sistema.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final RolService rolService;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional(rollbackFor = Exception.class)
    public UsuarioResponse crearUsuario(UsuarioRequest request) {
        // Validar que el email no exista
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new BusinessException("Ya existe un usuario con el email: " + request.email());
        }
        
        // Validar que el número de documento no exista
        if (usuarioRepository.existsByNumeroDocumento(request.numeroDocumento())) {
            throw new BusinessException("Ya existe un usuario con el número de documento: " + request.numeroDocumento());
        }
        
        // Validar que el rol exista
        Rol rol = rolRepository.findById(request.idRol())
            .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + request.idRol()));
        
        Usuario usuario = new Usuario();
        usuario.setRol(rol);
        usuario.setTipoDocumento(request.tipoDocumento());
        usuario.setNumeroDocumento(request.numeroDocumento());
        usuario.setNombres(request.nombres());
        usuario.setApellidos(request.apellidos());
        usuario.setEmail(request.email());
        usuario.setContrasenaHash(passwordEncoder.encode(request.contrasena()));
        usuario.setTelefono(request.telefono());
        usuario.setEstado(Usuario.EstadoUsuario.ACTIVO);
        
        return toUsuarioResponse(usuarioRepository.save(usuario));
    }
    
    @Transactional(rollbackFor = Exception.class)
    public UsuarioResponse actualizarUsuario(Integer idUsuario, UsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuario));
        
        // Validar email si cambió
        if (!usuario.getEmail().equals(request.email()) &&
            usuarioRepository.existsByEmail(request.email())) {
            throw new BusinessException("Ya existe un usuario con el email: " + request.email());
        }
        
        // Validar documento si cambió
        if (!usuario.getNumeroDocumento().equals(request.numeroDocumento()) &&
            usuarioRepository.existsByNumeroDocumento(request.numeroDocumento())) {
            throw new BusinessException("Ya existe un usuario con el número de documento: " + request.numeroDocumento());
        }
        
        // Validar que el rol exista
        Rol rol = rolRepository.findById(request.idRol())
            .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + request.idRol()));
        
        usuario.setRol(rol);
        usuario.setTipoDocumento(request.tipoDocumento());
        usuario.setNumeroDocumento(request.numeroDocumento());
        usuario.setNombres(request.nombres());
        usuario.setApellidos(request.apellidos());
        usuario.setEmail(request.email());
        
        // Solo actualizar contraseña si se proporciona
        if (request.contrasena() != null && !request.contrasena().isEmpty()) {
            usuario.setContrasenaHash(passwordEncoder.encode(request.contrasena()));
        }
        
        usuario.setTelefono(request.telefono());
        
        return toUsuarioResponse(usuarioRepository.save(usuario));
    }
    
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerUsuario(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuario));
        return toUsuarioResponse(usuario);
    }
    
    @Transactional(readOnly = true)
    public List<UsuarioResponse> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll().stream()
            .map(this::toUsuarioResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<UsuarioResponse> obtenerUsuariosActivos() {
        return usuarioRepository.findAllActive().stream()
            .map(this::toUsuarioResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<UsuarioResponse> obtenerUsuariosPorRol(Integer idRol) {
        return usuarioRepository.findByRol_IdRol(idRol).stream()
            .map(this::toUsuarioResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void cambiarEstadoUsuario(Integer idUsuario, Usuario.EstadoUsuario nuevoEstado) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuario));
        
        usuario.setEstado(nuevoEstado);
        usuarioRepository.save(usuario);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void eliminarUsuario(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuario));
        
        // Eliminación lógica
        usuario.setEstado(Usuario.EstadoUsuario.INACTIVO);
        usuarioRepository.save(usuario);
    }
    
    private UsuarioResponse toUsuarioResponse(Usuario usuario) {
        return new UsuarioResponse(
            usuario.getIdUsuario(),
            rolService.toRolResponse(usuario.getRol()),
            usuario.getTipoDocumento(),
            usuario.getNumeroDocumento(),
            usuario.getNombres(),
            usuario.getApellidos(),
            usuario.getEmail(),
            usuario.getTelefono(),
            usuario.getEstado().name(),
            usuario.getFechaCreacion(),
            usuario.getFechaUltimoLogin()
        );
    }
}

