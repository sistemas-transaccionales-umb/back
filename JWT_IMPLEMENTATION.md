# 🔐 JWT (JSON Web Tokens) - Implementación

## ✅ ¿Qué se ha implementado?

Se ha agregado **JWT (JSON Web Tokens)** al sistema de autenticación para:

- ✅ **Generar tokens** al hacer login o registro
- ✅ **Retornar el token** junto con la información del usuario
- ✅ **Validar tokens** en peticiones (preparado para usar)
- ✅ **Expiración automática** de tokens (24 horas por defecto)

---

## 📦 Componentes Agregados

### 1. **JwtService.java** - Servicio de JWT

Funcionalidades:
- `generateToken(Usuario)` - Genera token JWT con información del usuario
- `extractEmail(token)` - Extrae el email del token
- `isTokenValid(token, email)` - Valida si un token es válido
- `extractClaim(token, resolver)` - Extrae claims específicos
- `getExpirationTime()` - Obtiene tiempo de expiración

### 2. **AuthDTO.java** - Actualizado

`AuthResponse` ahora incluye:
```java
public record AuthResponse(
    Integer idUsuario,
    String nombres,
    String apellidos,
    String email,
    String tipoDocumento,
    String numeroDocumento,
    String telefono,
    Integer idRol,
    String nombreRol,
    String estado,
    String token,           // ← NUEVO: Token JWT
    long expiresIn,         // ← NUEVO: Tiempo de expiración (ms)
    String message
) {}
```

### 3. **AuthService.java** - Actualizado

- Inyecta `JwtService`
- Genera token en `toAuthResponse()`
- Retorna token en login y registro

### 4. **application.properties** - Configuración JWT

```properties
# JWT Configuration
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration=86400000  # 24 horas en milisegundos
```

---

## 🚀 Uso del Sistema

### 1. Login - Ahora Retorna Token

**Request:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@sistema.com",
    "password": "password123"
  }'
```

**Response (200 OK):**
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
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJpZFVzdWFyaW8iOjEsImVtYWlsIjoiYWRtaW5Ac2lzdGVtYS5jb20iLCJub21icmVzIjoiSnVhbiIsImFwZWxsaWRvcyI6IlDDqXJleiIsImlkUm9sIjoxLCJub21icmVSb2wiOiJBRE1JTiIsImVzdGFkbyI6IkFDVElWTyIsInN1YiI6ImFkbWluQHNpc3RlbWEuY29tIiwiaWF0IjoxNzI4MTIzNDU2LCJleHAiOjE3MjgyMDk4NTZ9.xyz123...",
  "expiresIn": 86400000,
  "message": "Login exitoso"
}
```

### 2. Registro - También Retorna Token

**Request:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "idRol": 2,
    "tipoDocumento": "CC",
    "numeroDocumento": "9876543210",
    "nombres": "María",
    "apellidos": "González",
    "email": "maria@ejemplo.com",
    "password": "password123",
    "telefono": "3109876543"
  }'
```

**Response (201 Created):**
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
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 86400000,
  "message": "Usuario registrado exitosamente"
}
```

---

## 🔄 Integración con Frontend

### React/TypeScript

```typescript
// types.ts
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
  token: string;          // ← Token JWT
  expiresIn: number;      // ← Tiempo de expiración en ms
  message: string;
}

// authService.ts
import axios from 'axios';

const API_URL = 'http://localhost:8080/api/auth';

export const authService = {
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const response = await axios.post<AuthResponse>(
      `${API_URL}/login`, 
      credentials
    );
    
    // Guardar token en localStorage
    localStorage.setItem('token', response.data.token);
    localStorage.setItem('user', JSON.stringify(response.data));
    
    return response.data;
  },

  async register(data: RegisterRequest): Promise<AuthResponse> {
    const response = await axios.post<AuthResponse>(
      `${API_URL}/register`, 
      data
    );
    
    // Guardar token en localStorage
    localStorage.setItem('token', response.data.token);
    localStorage.setItem('user', JSON.stringify(response.data));
    
    return response.data;
  },

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },

  getToken(): string | null {
    return localStorage.getItem('token');
  },

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
};

// Configurar Axios para incluir el token en todas las peticiones
axios.interceptors.request.use(
  (config) => {
    const token = authService.getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Componente de Login
const LoginComponent = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    
    try {
      const response = await authService.login({ email, password });
      
      console.log('Login exitoso!');
      console.log('Token:', response.token);
      console.log('Usuario:', response);
      console.log('Expira en:', response.expiresIn / 1000 / 60 / 60, 'horas');
      
      // Redirigir al dashboard
      window.location.href = '/dashboard';
      
    } catch (error) {
      console.error('Error en login:', error);
      alert('Credenciales inválidas');
    }
  };

  return (
    <form onSubmit={handleLogin}>
      <input 
        type="email" 
        value={email} 
        onChange={(e) => setEmail(e.target.value)}
        placeholder="Email"
        required 
      />
      <input 
        type="password" 
        value={password} 
        onChange={(e) => setPassword(e.target.value)}
        placeholder="Contraseña"
        required 
      />
      <button type="submit">Login</button>
    </form>
  );
};
```

---

## 🔒 Información del Token JWT

### Estructura del Token

Un token JWT tiene 3 partes separadas por puntos:

```
eyJhbGciOiJIUzI1NiJ9.eyJpZFVzdWFyaW8iOjEsImVtYWlsIjoiYWRtaW4iLCJub21icmVzIjoiSnVhbiIsImFwZWxsaWRvcyI6IlDDqXJleiIsImlkUm9sIjoxLCJub21icmVSb2wiOiJBRE1JTiIsImVzdGFkbyI6IkFDVElWTyIsInN1YiI6ImFkbWluQHNpc3RlbWEuY29tIiwiaWF0IjoxNzI4MTIzNDU2LCJleHAiOjE3MjgyMDk4NTZ9.xyz123
    ^                              ^                                                                                                                                                                                                                                      ^
    Header                          Payload                                                                                                                                                                                                                            Signature
```

### Claims Incluidos en el Token

```json
{
  "idUsuario": 1,
  "email": "admin@sistema.com",
  "nombres": "Juan",
  "apellidos": "Pérez",
  "idRol": 1,
  "nombreRol": "ADMIN",
  "estado": "ACTIVO",
  "sub": "admin@sistema.com",
  "iat": 1728123456,  // Fecha de emisión
  "exp": 1728209856   // Fecha de expiración
}
```

### Tiempo de Expiración

- **Por defecto**: 24 horas (86400000 ms)
- **Configurable** en `application.properties`

Para cambiar la expiración:
```properties
jwt.expiration=3600000  # 1 hora
jwt.expiration=604800000  # 7 días
```

---

## 🛡️ Validación de Token (Próximo Paso)

Aunque el token ya se genera, aún **no se valida** en los endpoints protegidos. Para proteger endpoints:

### 1. Crear Filtro JWT

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String email = jwtService.extractEmail(token);
            
            if (email != null && jwtService.isTokenValid(token, email)) {
                // Token válido - permitir acceso
                filterChain.doFilter(request, response);
            } else {
                // Token inválido - denegar acceso
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
```

### 2. Proteger Endpoints

```java
@RestController
@RequestMapping("/api/ventas")
public class VentaController {
    
    @PostMapping
    public ResponseEntity<VentaResponse> crearVenta(
        @RequestHeader("Authorization") String authHeader,
        @Valid @RequestBody VentaRequest request
    ) {
        // Validar token antes de procesar
        String token = authHeader.substring(7); // Remover "Bearer "
        
        if (!jwtService.isTokenValid(token, extractedEmail)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // Procesar venta...
    }
}
```

---

## 📊 Flujo Completo con JWT

```
1. Usuario hace login/registro
   ↓
2. Backend valida credenciales
   ↓
3. JwtService genera token con datos del usuario
   ↓
4. Se retorna AuthResponse con token
   ↓
5. Frontend guarda token en localStorage
   ↓
6. Frontend incluye token en cada petición:
   Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
   ↓
7. Backend valida token (próximo paso)
   ↓
8. Si válido → procesa petición
   Si inválido → HTTP 401
```

---

## 🎯 Ventajas de JWT

✅ **Sin estado (Stateless)** - No necesitas guardar sesiones en servidor  
✅ **Escalable** - Funciona en múltiples servidores  
✅ **Seguro** - Firmado con clave secreta  
✅ **Auto-contenido** - Incluye toda la información del usuario  
✅ **Estándar** - Compatible con cualquier frontend/móvil  
✅ **Expiración automática** - Tokens caducan después de 24h  

---

## ⚙️ Configuración

### En `application.properties`:

```properties
# JWT Secret (cambiar en producción por una clave más segura)
jwt.secret=TU_CLAVE_SECRETA_MUY_LARGA_Y_SEGURA_AQUI

# Expiración (en milisegundos)
jwt.expiration=86400000  # 24 horas
```

### Generar nueva clave secreta:

```bash
# En terminal
openssl rand -base64 32
```

---

## 🔐 Seguridad

### ⚠️ IMPORTANTE para Producción:

1. **Cambiar `jwt.secret`** a una clave única y segura
2. **Usar variable de entorno** en lugar de hardcodear en properties
3. **HTTPS obligatorio** - JWT sin HTTPS es vulnerable
4. **Implementar refresh tokens** para renovar tokens
5. **Blacklist de tokens** para logout efectivo

---

## ✅ Checklist

- [x] Dependencias JWT agregadas (jjwt)
- [x] JwtService implementado
- [x] AuthResponse incluye token
- [x] Login genera y retorna token
- [x] Registro genera y retorna token
- [x] Configuración JWT en properties
- [x] Claims personalizados incluidos
- [x] Validación de tokens preparada
- [x] Documentación completa

---

## 📚 Documentación de Referencia

- **JJWT Library**: https://github.com/jwtk/jjwt
- **JWT.io**: https://jwt.io (para decodificar tokens)
- **RFC 7519**: https://tools.ietf.org/html/rfc7519

---

**Desarrollado por:** Harold Camargo  
**Proyecto:** Sistemas Transaccionales - UMB  
**Fecha:** Octubre 2025

