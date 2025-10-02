# 🚀 INICIO RÁPIDO - Sistema de Gestión de Inventario

## ⚡ Pasos para Ejecutar en 5 Minutos

### 1️⃣ Prerequisitos

Asegúrate de tener instalado:

- ☑️ **Java 21** (verificar: `java -version`)
- ☑️ **Maven 3.6+** (verificar: `mvn -v`)
- ☑️ **MySQL 8+** (verificar: `mysql --version`)
- ☑️ **Git** (opcional)

---

### 2️⃣ Configurar Base de Datos MySQL

```sql
-- Abrir MySQL
mysql -u root -p

-- Crear base de datos
CREATE DATABASE sistemas_transaccionales;

-- Salir
exit;
```

---

### 3️⃣ Configurar Credenciales

Editar archivo: `src/main/resources/application.properties`

```properties
# Cambiar estas líneas con tus credenciales
spring.datasource.username=TU_USUARIO_MYSQL
spring.datasource.password=TU_CONTRASEÑA_MYSQL
```

**Ejemplo:**
```properties
spring.datasource.username=root
spring.datasource.password=miContraseña123
```

---

### 4️⃣ Compilar el Proyecto

Abre terminal en la carpeta del proyecto y ejecuta:

```bash
mvn clean install
```

⏳ Esto descargará dependencias (primera vez puede tomar 2-3 minutos)

---

### 5️⃣ Ejecutar la Aplicación

```bash
mvn spring-boot:run
```

✅ Si ves este mensaje, ¡está funcionando!:

```
Started SistemasTransaccionalesApplication in X.XXX seconds
```

---

### 6️⃣ Cargar Datos de Ejemplo (Opcional)

**Opción A - Desde MySQL:**

```bash
mysql -u root -p sistemas_transaccionales < src/main/resources/data-example.sql
```

**Opción B - Desde MySQL Workbench:**

1. Abrir MySQL Workbench
2. Conectar a la base de datos
3. File → Open SQL Script
4. Seleccionar `src/main/resources/data-example.sql`
5. Ejecutar (⚡ botón)

---

### 7️⃣ Probar la API

Abre tu navegador o Postman en:

```
http://localhost:8080/api/categorias
```

Deberías ver un array JSON (posiblemente vacío si no cargaste datos de ejemplo).

---

## 🧪 Pruebas Rápidas con cURL

### 0. Autenticación (Primero!)

**Registrar un usuario:**

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "idRol": 1,
    "tipoDocumento": "CC",
    "numeroDocumento": "1234567890",
    "nombres": "Admin",
    "apellidos": "Sistema",
    "email": "admin@sistema.com",
    "password": "admin123",
    "telefono": "3001234567"
  }'
```

**Login:**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@sistema.com",
    "password": "admin123"
  }'
```

### 1. Crear una Categoría

```bash
curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{"nombreCategoria":"Electrónica","descripcion":"Productos tecnológicos"}'
```

### 2. Listar Categorías

```bash
curl http://localhost:8080/api/categorias
```

### 3. Crear un Producto

```bash
curl -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" \
  -d '{
    "idCategoria": 1,
    "codigoBarras": "7701234567890",
    "nombre": "Laptop HP",
    "descripcion": "Laptop HP 15 pulgadas",
    "precioCompra": 1500000,
    "precioVenta": 2200000,
    "porcentajeIva": 19
  }'
```

---

## 🔧 Solución de Problemas Comunes

### ❌ Error: "Access denied for user"

**Problema:** Credenciales de MySQL incorrectas

**Solución:**
1. Verifica usuario y contraseña en `application.properties`
2. Asegúrate que el usuario tiene permisos en la base de datos

```sql
-- En MySQL, otorgar permisos
GRANT ALL PRIVILEGES ON sistemas_transaccionales.* TO 'tu_usuario'@'localhost';
FLUSH PRIVILEGES;
```

---

### ❌ Error: "Cannot find symbol" al compilar

**Problema:** Lombok no está configurado en tu IDE

**Solución:**

**Para IntelliJ IDEA:**
1. File → Settings → Plugins
2. Buscar "Lombok"
3. Instalar y reiniciar

**Para Eclipse:**
1. Descargar lombok.jar
2. Ejecutar: `java -jar lombok.jar`
3. Seleccionar ubicación de Eclipse

**O simplemente compilar desde terminal:**
```bash
mvn clean install -DskipTests
```

---

### ❌ Error: "Port 8080 already in use"

**Problema:** Otro servicio está usando el puerto 8080

**Solución:**

**Opción A - Cambiar puerto:**

Editar `application.properties`:
```properties
server.port=8081
```

**Opción B - Detener el servicio:**

En Linux/Mac:
```bash
lsof -i :8080
kill -9 <PID>
```

En Windows:
```cmd
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

---

### ❌ Error: "Table doesn't exist"

**Problema:** Hibernate no creó las tablas

**Solución:**

Verificar en `application.properties`:
```properties
spring.jpa.hibernate.ddl-auto=update
```

Debe estar en **update** o **create** (no en **none** o **validate**)

---

### ❌ Error: "Failed to load ApplicationContext"

**Problema:** Configuración incorrecta

**Solución:**

1. Verificar que MySQL esté corriendo:
   ```bash
   # Linux/Mac
   sudo service mysql status
   
   # Windows
   net start MySQL80
   ```

2. Verificar conexión:
   ```bash
   mysql -u root -p -e "SHOW DATABASES;"
   ```

---

## 📱 Herramientas Recomendadas para Probar la API

### Opción 1: Postman (Recomendado)

1. Descargar: https://www.postman.com/downloads/
2. Importar colección (crear nueva colección con los endpoints)
3. Configurar base URL: `http://localhost:8080`

### Opción 2: Thunder Client (VS Code)

1. Instalar extensión "Thunder Client" en VS Code
2. Crear nueva request
3. URL: `http://localhost:8080/api/categorias`

### Opción 3: cURL (Terminal)

Ya funciona en tu terminal, ver ejemplos arriba.

### Opción 4: Navegador Web

Para peticiones GET, simplemente abre:
- `http://localhost:8080/api/categorias`
- `http://localhost:8080/api/productos`
- etc.

---

## 📚 Siguientes Pasos

### 1. Probar Autenticación

Ver documentación completa: **`AUTH_API_DOCS.md`**

### 2. Probar Operaciones CRUD

Sigue los ejemplos en: **`API-EXAMPLES.md`**

### 3. Probar Operaciones Transaccionales

Crear una venta completa (ver `API-EXAMPLES.md` sección Ventas)

### 4. Explorar Transferencias

Probar el flujo completo:
1. Crear transferencia
2. Procesarla
3. Recibirla

### 5. Revisar Auditoría

Consultar movimientos de inventario:
```bash
curl http://localhost:8080/api/movimientos/producto/1
```

---

## 🎯 Flujo Recomendado de Pruebas

### Paso 0: Autenticación

```bash
# 0. Registrar y hacer login
POST /api/auth/register
POST /api/auth/login
```

### Paso 1: Crear Estructura Base

```bash
# 1. Crear categoría
POST /api/categorias

# 2. Crear producto
POST /api/productos

# 3. Crear bodega
POST /api/bodegas

# 4. Crear inventario
POST /api/inventario

# 5. Crear cliente
POST /api/clientes
```

### Paso 2: Realizar Operaciones

```bash
# 6. Crear venta (ACID)
POST /api/ventas

# 7. Verificar inventario descontado
GET /api/inventario/bodega/1

# 8. Crear transferencia
POST /api/transferencias

# 9. Procesarla
POST /api/transferencias/1/procesar

# 10. Recibirla
POST /api/transferencias/1/recibir
```

### Paso 3: Consultar Resultados

```bash
# Ver todas las ventas
GET /api/ventas

# Ver productos con stock bajo
GET /api/inventario/stock-bajo

# Ver transferencias pendientes
GET /api/transferencias/pendientes
```

---

## 📊 Verificar que Todo Funciona

### Test 1: Categorías ✅

```bash
# Crear
curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{"nombreCategoria":"Test","descripcion":"Prueba"}'

# Listar (debe mostrar 1 categoría)
curl http://localhost:8080/api/categorias
```

### Test 2: Validaciones ✅

```bash
# Intentar crear sin nombre (debe dar error 400)
curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{"descripcion":"Sin nombre"}'
```

### Test 3: Relaciones ✅

```bash
# Crear producto con categoría inexistente (debe dar error 404)
curl -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" \
  -d '{
    "idCategoria": 999,
    "codigoBarras": "123",
    "nombre": "Test",
    "precioCompra": 100,
    "precioVenta": 200,
    "porcentajeIva": 19
  }'
```

---

## 🔒 Seguridad (Próximos Pasos)

⚠️ **IMPORTANTE:** Este sistema NO tiene autenticación implementada.

Para producción, agregar:

1. **Spring Security**
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-security</artifactId>
   </dependency>
   ```

2. **JWT para autenticación**
3. **Roles y permisos**
4. **HTTPS**

---

## 📈 Monitoreo

### Ver Logs en Consola

Los logs muestran:
- ✅ Consultas SQL ejecutadas
- ✅ Parámetros de queries
- ✅ Logs de servicios (INFO, DEBUG, ERROR)

### Verificar Base de Datos

```sql
-- Ver todas las tablas creadas
USE sistemas_transaccionales;
SHOW TABLES;

-- Ver registros
SELECT * FROM categorias;
SELECT * FROM productos;
SELECT * FROM inventario;
SELECT * FROM ventas;
```

---

## 🎓 Documentación Completa

- 📘 **README_API.md** - Documentación completa del sistema
- 📗 **API-EXAMPLES.md** - Ejemplos de todos los endpoints
- 📕 **IMPLEMENTACION_COMPLETA.md** - Resumen de implementación
- 📙 **INICIO_RAPIDO.md** - Este archivo

---

## ✅ Checklist de Inicio

- [ ] Java 21 instalado
- [ ] Maven instalado
- [ ] MySQL corriendo
- [ ] Base de datos creada
- [ ] Credenciales configuradas en `application.properties`
- [ ] Proyecto compilado con `mvn clean install`
- [ ] Aplicación corriendo con `mvn spring-boot:run`
- [ ] API respondiendo en `http://localhost:8080`
- [ ] Datos de ejemplo cargados (opcional)
- [ ] Primera categoría creada exitosamente

---

## 🎉 ¡Listo!

Tu sistema está **funcionando correctamente** si:

✅ La aplicación inicia sin errores  
✅ Puedes acceder a `http://localhost:8080/api/categorias`  
✅ Puedes crear una categoría via POST  
✅ La base de datos tiene las tablas creadas

---

**¿Necesitas ayuda?**

1. Revisa "Solución de Problemas" arriba
2. Verifica los logs en la consola
3. Consulta `README_API.md` para más detalles

**¡Disfruta tu sistema de gestión de inventario con transacciones ACID! 🚀**

