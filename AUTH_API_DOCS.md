# 🔐 API de Autenticación - Documentación

## Descripción

Sistema de autenticación completo con registro y login de usuarios, encriptación de contraseñas con BCrypt y validaciones de seguridad.

---

## 🎯 Endpoints Disponibles

### 1. Login de Usuario

**POST** `/api/auth/login`

Autentica un usuario existente con email y contraseña.

#### Request Body

```json
{
  "email": "usuario@ejemplo.com",
  "password": "contraseña123"
}
```

#### Response (200 OK)

```json
{
  "idUsuario": 1,
  "nombres": "Juan",
  "apellidos": "Pérez",
  "email": "juan.perez@sistema.com",
  "tipoDocumento": "CC",
  "numeroDocumento": "1234567890",
  "telefono": "3001234567",
  "idRol": 1,
  "nombreRol": "ADMIN",
  "estado": "ACTIVO",
  "message": "Login exitoso"
}
```

#### Errores

- **401 Unauthorized**: Credenciales inválidas o usuario inactivo
- **400 Bad Request**: Datos de entrada inválidos

---

### 2. Registro de Usuario

**POST** `/api/auth/register`

Registra un nuevo usuario en el sistema.

#### Request Body

```json
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

#### Validaciones

- **idRol**: Obligatorio, debe existir en la tabla `roles`
- **tipoDocumento**: Obligatorio, máximo 3 caracteres
- **numeroDocumento**: Obligatorio, máximo 20 caracteres, único
- **nombres**: Obligatorio, máximo 100 caracteres
- **apellidos**: Obligatorio, máximo 100 caracteres
- **email**: Obligatorio, formato email válido, único
- **password**: Obligatorio, mínimo 6 caracteres
- **telefono**: Opcional, máximo 20 caracteres

#### Response (201 Created)

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

#### Errores

- **400 Bad Request**: Email o documento ya registrado
- **404 Not Found**: Rol no encontrado

---

### 3. Verificar Email Disponible

**GET** `/api/auth/check-email?email=ejemplo@email.com`

Verifica si un email ya está registrado en el sistema.

#### Response (200 OK)

```json
true  // Email ya existe
```

```json
false // Email disponible
```

---

### 4. Obtener Usuario por Email

**GET** `/api/auth/user?email=usuario@ejemplo.com`

Obtiene los datos de un usuario por su email.

#### Response (200 OK)

```json
{
  "idUsuario": 1,
  "nombres": "Juan",
  "apellidos": "Pérez",
  "email": "juan.perez@sistema.com",
  "tipoDocumento": "CC",
  "numeroDocumento": "1234567890",
  "telefono": "3001234567",
  "idRol": 1,
  "nombreRol": "ADMIN",
  "estado": "ACTIVO",
  "message": "Usuario encontrado"
}
```

#### Errores

- **404 Not Found**: Usuario no encontrado

---

### 5. Cambiar Contraseña

**POST** `/api/auth/change-password`

Permite a un usuario cambiar su contraseña.

#### Request Body

```json
{
  "email": "usuario@ejemplo.com",
  "oldPassword": "contraseñaActual",
  "newPassword": "nuevaContraseña123"
}
```

#### Response (200 OK)

```json
{
  "message": "Contraseña actualizada exitosamente"
}
```

#### Errores

- **401 Unauthorized**: Contraseña actual incorrecta
- **404 Not Found**: Usuario no encontrado

---

## 🔒 Seguridad Implementada

### Encriptación de Contraseñas

- **Algoritmo**: BCrypt
- **Factor de trabajo**: 10 (por defecto)
- **Características**:
  - ✅ Resistente a ataques de fuerza bruta
  - ✅ Salt aleatorio único por contraseña
  - ✅ No reversible (hash de una sola vía)

### Validaciones de Seguridad

1. **Email único**: No se permiten duplicados
2. **Documento único**: No se permiten duplicados
3. **Contraseña mínima**: 6 caracteres
4. **Estado del usuario**: Solo usuarios ACTIVOS pueden hacer login
5. **Último login**: Se registra automáticamente

---

## 📋 Flujos de Uso

### Flujo de Registro

```
1. Frontend → POST /api/auth/register
2. Validar datos (email único, documento único)
3. Verificar que el rol exista
4. Encriptar contraseña con BCrypt
5. Crear usuario en estado ACTIVO
6. Retornar datos del usuario creado
```

### Flujo de Login

```
1. Frontend → POST /api/auth/login
2. Buscar usuario por email
3. Verificar que esté ACTIVO
4. Comparar contraseña con BCrypt
5. Actualizar fecha de último login
6. Retornar datos del usuario autenticado
```

---

## 🧪 Ejemplos de Prueba

### 1. Registrar Usuario

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "idRol": 2,
    "tipoDocumento": "CC",
    "numeroDocumento": "1122334455",
    "nombres": "Pedro",
    "apellidos": "Martínez",
    "email": "pedro@test.com",
    "password": "password123",
    "telefono": "3001122334"
  }'
```

### 2. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "pedro@test.com",
    "password": "password123"
  }'
```

### 3. Verificar Email

```bash
curl http://localhost:8080/api/auth/check-email?email=pedro@test.com
```

### 4. Cambiar Contraseña

```bash
curl -X POST http://localhost:8080/api/auth/change-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "pedro@test.com",
    "oldPassword": "password123",
    "newPassword": "newpassword456"
  }'
```

---

## 🔄 Integración con Frontend

### TypeScript/JavaScript

```typescript
// interfaces.ts
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

// authService.ts
import axios from 'axios';

const API_URL = 'http://localhost:8080/api/auth';

export const authService = {
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const response = await axios.post<AuthResponse>(`${API_URL}/login`, credentials);
    return response.data;
  },

  async register(data: RegisterRequest): Promise<AuthResponse> {
    const response = await axios.post<AuthResponse>(`${API_URL}/register`, data);
    return response.data;
  },

  async checkEmail(email: string): Promise<boolean> {
    const response = await axios.get<boolean>(`${API_URL}/check-email`, {
      params: { email }
    });
    return response.data;
  },

  async changePassword(email: string, oldPassword: string, newPassword: string): Promise<void> {
    await axios.post(`${API_URL}/change-password`, {
      email,
      oldPassword,
      newPassword
    });
  }
};

// Uso en componente
async function handleLogin(email: string, password: string) {
  try {
    const user = await authService.login({ email, password });
    console.log('Usuario autenticado:', user);
    // Guardar en localStorage o context
    localStorage.setItem('user', JSON.stringify(user));
    // Redirigir al dashboard
  } catch (error) {
    console.error('Error en login:', error);
    alert('Credenciales inválidas');
  }
}

async function handleRegister(data: RegisterRequest) {
  try {
    const user = await authService.register(data);
    console.log('Usuario registrado:', user);
    // Auto-login después de registro
    localStorage.setItem('user', JSON.stringify(user));
    // Redirigir al dashboard
  } catch (error) {
    console.error('Error en registro:', error);
    alert('Error al registrar usuario');
  }
}
```

---

## 💾 Estructura de la Base de Datos

### Tabla: usuarios

```sql
CREATE TABLE usuarios (
  id_usuario INT AUTO_INCREMENT PRIMARY KEY,
  id_rol INT NOT NULL,
  tipo_documento VARCHAR(3) NOT NULL,
  numero_documento VARCHAR(20) NOT NULL UNIQUE,
  nombres VARCHAR(100) NOT NULL,
  apellidos VARCHAR(100) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  contrasena_hash VARCHAR(255) NOT NULL,
  telefono VARCHAR(20),
  estado ENUM('ACTIVO', 'INACTIVO') DEFAULT 'ACTIVO',
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_ultimo_login TIMESTAMP NULL,
  FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
);
```

---

## ⚠️ Errores Comunes

### 401 - Credenciales inválidas

```json
{
  "timestamp": "2025-10-02T10:30:00",
  "status": 401,
  "error": "Authentication Failed",
  "message": "Credenciales inválidas"
}
```

**Causas:**
- Email no registrado
- Contraseña incorrecta
- Usuario inactivo

---

### 400 - Email ya existe

```json
{
  "timestamp": "2025-10-02T10:30:00",
  "status": 400,
  "error": "Business Rule Violation",
  "message": "Ya existe un usuario con el email: test@ejemplo.com"
}
```

---

### 400 - Validación de campos

```json
{
  "email": "El email debe ser válido",
  "password": "La contraseña debe tener al menos 6 caracteres"
}
```

---

## 🔐 Mejores Prácticas

### Para el Frontend

1. **Almacenar token de sesión** (localStorage/sessionStorage)
2. **Implementar auto-logout** después de inactividad
3. **Validar campos antes de enviar**
4. **Mostrar mensajes de error claros**
5. **Implementar "Recordarme"** si es necesario

### Para el Backend (Ya Implementado)

✅ Contraseñas encriptadas con BCrypt  
✅ Validaciones exhaustivas  
✅ Manejo de excepciones personalizado  
✅ Transacciones ACID  
✅ Logs de auditoría  
✅ CORS habilitado para frontend

---

## 📈 Próximos Pasos Recomendados

1. **JWT (JSON Web Tokens)**
   - Implementar tokens de sesión
   - Refresh tokens
   - Expiración automática

2. **Roles y Permisos**
   - Middleware de autorización
   - Endpoints protegidos por rol
   - Verificación de permisos

3. **Recuperación de Contraseña**
   - Envío de email con token
   - Reset de contraseña
   - Verificación de identidad

4. **Autenticación de 2 Factores (2FA)**
   - TOTP (Google Authenticator)
   - SMS
   - Email

5. **OAuth 2.0**
   - Login con Google
   - Login con Facebook
   - Login con GitHub

---

## 🎉 ¡Sistema de Autenticación Completado!

Tu API de autenticación está lista para usar con:

✅ **Registro de usuarios** con validaciones  
✅ **Login seguro** con BCrypt  
✅ **Cambio de contraseña**  
✅ **Verificación de email**  
✅ **Manejo de errores** HTTP apropiados  
✅ **CORS habilitado** para frontend  
✅ **Documentación completa**

---

**Desarrollado por:** Harold Camargo  
**Proyecto:** Sistemas Transaccionales - UMB  
**Fecha:** Octubre 2025

