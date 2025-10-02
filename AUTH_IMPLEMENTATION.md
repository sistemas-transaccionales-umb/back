# ✅ AUTENTICACIÓN IMPLEMENTADA

## 📋 Resumen

Se ha agregado un **sistema completo de autenticación** al proyecto con:

✅ **Registro de usuarios** con validaciones  
✅ **Login seguro** con encriptación BCrypt  
✅ **Cambio de contraseña**  
✅ **Verificación de email disponible**  
✅ **Manejo de errores** HTTP 401 (Unauthorized)  
✅ **CORS habilitado** para frontend  
✅ **Transacciones ACID** en registro  
✅ **Auditoría** de último login

---

## 🗂️ Archivos Creados

### 1. DTOs
- **`AuthDTO.java`** - 3 records:
  - `LoginRequest` (email, password)
  - `RegisterRequest` (datos completos del usuario)
  - `AuthResponse` (respuesta con datos del usuario autenticado)

### 2. Servicio
- **`AuthService.java`** - Lógica de autenticación:
  - `login()` - Valida credenciales y retorna usuario
  - `register()` - Crea nuevo usuario con contraseña encriptada
  - `existsByEmail()` - Verifica disponibilidad de email
  - `getUserByEmail()` - Obtiene usuario por email
  - `changePassword()` - Cambia contraseña validando la actual

### 3. Controlador
- **`AuthController.java`** - 5 endpoints REST:
  - `POST /api/auth/login`
  - `POST /api/auth/register`
  - `GET /api/auth/check-email`
  - `GET /api/auth/user`
  - `POST /api/auth/change-password`

### 4. Configuración
- **`SecurityConfig.java`** - Bean de PasswordEncoder (BCrypt)

### 5. Excepciones
- **`AuthenticationException.java`** - Excepción personalizada para errores de autenticación

### 6. Manejo de Excepciones
- **`GlobalExceptionHandler.java`** - Agregado handler para AuthenticationException (HTTP 401)

---

## 🔐 Seguridad Implementada

### Encriptación BCrypt

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

**Características:**
- ✅ Algoritmo de hashing seguro
- ✅ Salt aleatorio único por contraseña
- ✅ Factor de trabajo: 10 (2^10 = 1024 iteraciones)
- ✅ No reversible (one-way hash)
- ✅ Resistente a ataques de fuerza bruta

### Validaciones

**En Registro:**
- Email único
- Documento único
- Contraseña mínimo 6 caracteres
- Rol debe existir
- Estado inicial: ACTIVO

**En Login:**
- Usuario debe existir
- Contraseña debe coincidir
- Usuario debe estar ACTIVO
- Se registra último login

---

## 📡 Endpoints

### 1. Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@sistema.com",
  "password": "password123"
}
```

**Response 200:**
```json
{
  "idUsuario": 1,
  "nombres": "Juan",
  "apellidos": "Pérez",
  "email": "admin@sistema.com",
  "tipoDocumento": "CC",
  "numeroDocumento": "1234567890",
  "telefono": "3001234567",
  "idRol": 1,
  "nombreRol": "ADMIN",
  "estado": "ACTIVO",
  "message": "Login exitoso"
}
```

**Error 401:**
```json
{
  "timestamp": "2025-10-02T10:30:00",
  "status": 401,
  "error": "Authentication Failed",
  "message": "Credenciales inválidas"
}
```

---

### 2. Registro

```http
POST /api/auth/register
Content-Type: application/json

{
  "idRol": 2,
  "tipoDocumento": "CC",
  "numeroDocumento": "9876543210",
  "nombres": "María",
  "apellidos": "González",
  "email": "maria@ejemplo.com",
  "password": "password123",
  "telefono": "3109876543"
}
```

**Response 201:**
```json
{
  "idUsuario": 2,
  "nombres": "María",
  "apellidos": "González",
  "email": "maria@ejemplo.com",
  "tipoDocumento": "CC",
  "numeroDocumento": "9876543210",
  "telefono": "3109876543",
  "idRol": 2,
  "nombreRol": "VENDEDOR",
  "estado": "ACTIVO",
  "message": "Usuario registrado exitosamente"
}
```

---

## 🧪 Pruebas

### Con cURL

```bash
# Registrar usuario
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "idRol": 1,
    "tipoDocumento": "CC",
    "numeroDocumento": "1234567890",
    "nombres": "Admin",
    "apellidos": "Sistema",
    "email": "admin@test.com",
    "password": "admin123",
    "telefono": "3001234567"
  }'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@test.com",
    "password": "admin123"
  }'
```

### Usuarios de Ejemplo (en data-example.sql)

| Email | Password | Rol |
|-------|----------|-----|
| admin@sistema.com | password123 | ADMIN |
| vendedor@sistema.com | password123 | VENDEDOR |
| bodega@sistema.com | password123 | BODEGUERO |

---

## 🔄 Integración con Frontend

### React/TypeScript

```typescript
// authService.ts
import axios from 'axios';

const API_URL = 'http://localhost:8080/api/auth';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  idRol: number;
  tipoDocumento: string;
  numeroDocumento: string;
  nombres: string;
  apellidos: string;
  email: string;
  password: string;
  telefono?: string;
}

export interface AuthResponse {
  idUsuario: number;
  nombres: string;
  apellidos: string;
  email: string;
  tipoDocumento: string;
  numeroDocumento: string;
  telefono: string;
  idRol: number;
  nombreRol: string;
  estado: string;
  message: string;
}

export const authService = {
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const response = await axios.post<AuthResponse>(
      `${API_URL}/login`, 
      credentials
    );
    return response.data;
  },

  async register(data: RegisterRequest): Promise<AuthResponse> {
    const response = await axios.post<AuthResponse>(
      `${API_URL}/register`, 
      data
    );
    return response.data;
  }
};

// Uso en componente
const handleLogin = async (email: string, password: string) => {
  try {
    const user = await authService.login({ email, password });
    localStorage.setItem('user', JSON.stringify(user));
    console.log('Login exitoso:', user);
    // Redirigir al dashboard
  } catch (error) {
    console.error('Error:', error);
    alert('Credenciales inválidas');
  }
};
```

---

## 📊 Flujo de Login

```
1. Usuario envía email y password
   ↓
2. AuthController recibe request
   ↓
3. AuthService.login()
   ↓
4. Buscar usuario por email
   ↓
5. ¿Usuario existe?
   NO → AuthenticationException (401)
   SÍ ↓
6. ¿Usuario ACTIVO?
   NO → AuthenticationException (401)
   SÍ ↓
7. Verificar password con BCrypt
   ↓
8. ¿Password coincide?
   NO → AuthenticationException (401)
   SÍ ↓
9. Actualizar fecha_ultimo_login
   ↓
10. Retornar AuthResponse (200)
```

---

## 📊 Flujo de Registro

```
1. Usuario envía datos de registro
   ↓
2. AuthController valida datos
   ↓
3. AuthService.register()
   ↓
4. ¿Email ya existe?
   SÍ → BusinessException (400)
   NO ↓
5. ¿Documento ya existe?
   SÍ → BusinessException (400)
   NO ↓
6. ¿Rol existe?
   NO → ResourceNotFoundException (404)
   SÍ ↓
7. Encriptar password con BCrypt
   ↓
8. Crear usuario (estado: ACTIVO)
   ↓
9. @Transactional - guardar en BD
   ↓
10. Retornar AuthResponse (201)
```

---

## 🎯 Características Destacadas

### 1. Transaccional ACID

```java
@Transactional(rollbackFor = Exception.class)
public AuthResponse register(RegisterRequest request) {
    // Todas las validaciones y guardado son atómicos
    // Si falla cualquier paso, rollback completo
}
```

### 2. Logging de Auditoría

```java
log.info("Intento de login para email: {}", request.email());
log.warn("Intento de login fallido para email: {}", request.email());
log.info("Login exitoso para usuario: {} - {}", usuario.getIdUsuario(), usuario.getEmail());
log.info("Usuario registrado exitosamente: {} - {}", usuarioGuardado.getIdUsuario(), usuarioGuardado.getEmail());
```

### 3. Actualización de Último Login

```java
// En cada login exitoso
usuarioRepository.updateUltimoLogin(usuario.getIdUsuario(), LocalDateTime.now());
```

### 4. CORS Habilitado

```java
@CrossOrigin(origins = "*")
public class AuthController {
    // Permite peticiones desde cualquier origen (frontend)
}
```

---

## 🔄 Próximos Pasos Recomendados

### 1. JWT (JSON Web Tokens)

```java
// Generar token en login
String token = jwtService.generateToken(usuario);

// AuthResponse incluiría:
public record AuthResponse(
    // ... campos existentes
    String token,
    long expiresIn
) {}
```

### 2. Middleware de Autenticación

```java
@RestController
public class ProductoController {
    
    @GetMapping("/api/productos")
    @PreAuthorize("isAuthenticated()")
    public List<ProductoResponse> getProductos() {
        // Solo usuarios autenticados
    }
    
    @PostMapping("/api/productos")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductoResponse crearProducto() {
        // Solo usuarios ADMIN
    }
}
```

### 3. Refresh Tokens

```java
public record TokenResponse(
    String accessToken,
    String refreshToken,
    long expiresIn
) {}
```

### 4. Recuperación de Contraseña

```java
@PostMapping("/forgot-password")
public void forgotPassword(String email) {
    // Generar token temporal
    // Enviar email con link de reset
}

@PostMapping("/reset-password")
public void resetPassword(String token, String newPassword) {
    // Validar token
    // Cambiar contraseña
}
```

---

## 📦 Dependencias Agregadas

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
</dependency>
```

**Nota:** Solo se usa BCrypt, no Spring Security completo (sin configuración de seguridad).

---

## ✅ Checklist de Implementación

- [x] DTOs (LoginRequest, RegisterRequest, AuthResponse)
- [x] AuthService con lógica de negocio
- [x] AuthController con endpoints REST
- [x] PasswordEncoder con BCrypt
- [x] Validaciones Jakarta Validation
- [x] Manejo de excepciones (401)
- [x] Transacciones ACID
- [x] Logging de auditoría
- [x] CORS habilitado
- [x] Actualización de último login
- [x] Documentación completa
- [x] Ejemplos de integración frontend

---

## 📚 Documentación

- **`AUTH_API_DOCS.md`** - Documentación completa de la API
- **`API-EXAMPLES.md`** - Ejemplos actualizados con autenticación
- **`README_API.md`** - README actualizado
- **`INICIO_RAPIDO.md`** - Guía de inicio actualizada
- **`data-example.sql`** - Usuarios de ejemplo actualizados

---

## 🎉 ¡Sistema de Autenticación Listo!

Tu API ahora incluye:

✅ **Login seguro** con BCrypt  
✅ **Registro de usuarios** con validaciones  
✅ **Cambio de contraseña**  
✅ **Verificación de email**  
✅ **HTTP 401** para errores de autenticación  
✅ **CORS** para frontend  
✅ **Documentación completa**  
✅ **Ejemplos de integración**  
✅ **Usuarios de prueba** en SQL

---

**Desarrollado por:** Harold Camargo  
**Proyecto:** Sistemas Transaccionales - UMB  
**Fecha:** Octubre 2025

