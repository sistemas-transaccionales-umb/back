package com.umb.sistema.service;

import com.umb.sistema.dto.AuthDTO.*;
import com.umb.sistema.entity.Rol;
import com.umb.sistema.entity.Usuario;
import com.umb.sistema.exception.AuthenticationException;
import com.umb.sistema.exception.BusinessException;
import com.umb.sistema.exception.ResourceNotFoundException;
import com.umb.sistema.repository.RolRepository;
import com.umb.sistema.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Login de usuario - Valida credenciales y retorna datos del usuario
     * 
     * @param request Credenciales de login (email y password)
     * @return AuthResponse con datos del usuario autenticado
     * @throws AuthenticationException si las credenciales son inválidas
     */
    @Transactional(rollbackFor = Exception.class)
    public AuthResponse login(LoginRequest request) {
        log.info("Intento de login para email: {}", request.email());
        
        // Buscar usuario por email
        Usuario usuario = usuarioRepository.findByEmail(request.email())
            .orElseThrow(() -> new AuthenticationException("Credenciales inválidas"));
        
        // Verificar que el usuario esté activo
        if (usuario.getEstado() != Usuario.EstadoUsuario.ACTIVO) {
            throw new AuthenticationException("El usuario no está activo");
        }
        
        // Verificar contraseña
        if (!passwordEncoder.matches(request.password(), usuario.getContrasenaHash())) {
            log.warn("Intento de login fallido para email: {}", request.email());
            throw new AuthenticationException("Credenciales inválidas");
        }
        
        // Actualizar último login
        usuarioRepository.updateUltimoLogin(usuario.getIdUsuario(), LocalDateTime.now());
        
        log.info("Login exitoso para usuario: {} - {}", usuario.getIdUsuario(), usuario.getEmail());
        
        return toAuthResponse(usuario, "Login exitoso");
    }
    
    /**
     * Registro de nuevo usuario - Operación transaccional ACID
     * 
     * - Valida unicidad de email y documento
     * - Encripta la contraseña con BCrypt
     * - Crea el usuario en estado ACTIVO
     * 
     * @param request Datos del usuario a registrar
     * @return AuthResponse con datos del usuario creado
     * @throws BusinessException si el email o documento ya existen
     */
    @Transactional(rollbackFor = Exception.class)
    public AuthResponse register(RegisterRequest request) {
        log.info("Intento de registro para email: {}", request.email());
        
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
        
        // Crear nuevo usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setRol(rol);
        nuevoUsuario.setTipoDocumento(request.tipoDocumento());
        nuevoUsuario.setNumeroDocumento(request.numeroDocumento());
        nuevoUsuario.setNombres(request.nombres());
        nuevoUsuario.setApellidos(request.apellidos());
        nuevoUsuario.setEmail(request.email());
        
        // Encriptar contraseña con BCrypt
        String passwordEncriptada = passwordEncoder.encode(request.password());
        nuevoUsuario.setContrasenaHash(passwordEncriptada);
        
        nuevoUsuario.setTelefono(request.telefono());
        nuevoUsuario.setEstado(Usuario.EstadoUsuario.ACTIVO);
        
        // Guardar usuario
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
        
        log.info("Usuario registrado exitosamente: {} - {}", usuarioGuardado.getIdUsuario(), usuarioGuardado.getEmail());
        
        return toAuthResponse(usuarioGuardado, "Usuario registrado exitosamente");
    }
    
    /**
     * Verificar si un usuario existe por email
     * 
     * @param email Email del usuario
     * @return true si existe, false si no
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
    
    /**
     * Obtener usuario por email
     * 
     * @param email Email del usuario
     * @return AuthResponse con datos del usuario
     * @throws ResourceNotFoundException si el usuario no existe
     */
    @Transactional(readOnly = true)
    public AuthResponse getUserByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
        
        return toAuthResponse(usuario, "Usuario encontrado");
    }
    
    /**
     * Cambiar contraseña de usuario
     * 
     * @param email Email del usuario
     * @param oldPassword Contraseña actual
     * @param newPassword Nueva contraseña
     * @throws AuthenticationException si la contraseña actual es incorrecta
     */
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(String email, String oldPassword, String newPassword) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
        
        // Verificar contraseña actual
        if (!passwordEncoder.matches(oldPassword, usuario.getContrasenaHash())) {
            throw new AuthenticationException("La contraseña actual es incorrecta");
        }
        
        // Encriptar y guardar nueva contraseña
        String nuevaPasswordEncriptada = passwordEncoder.encode(newPassword);
        usuario.setContrasenaHash(nuevaPasswordEncriptada);
        usuarioRepository.save(usuario);
        
        log.info("Contraseña actualizada para usuario: {}", email);
    }
    
    /**
     * Mapear Usuario a AuthResponse
     */
    private AuthResponse toAuthResponse(Usuario usuario, String message) {
        return new AuthResponse(
            usuario.getIdUsuario(),
            usuario.getNombres(),
            usuario.getApellidos(),
            usuario.getEmail(),
            usuario.getTipoDocumento(),
            usuario.getNumeroDocumento(),
            usuario.getTelefono(),
            usuario.getRol().getIdRol(),
            usuario.getRol().getNombreRol(),
            usuario.getEstado().name(),
            message
        );
    }
}

