# Sistema de Gestión de Inventario y Ventas - Spring Boot

## Descripción del Proyecto

Sistema empresarial completo de gestión de inventario, ventas y transferencias entre bodegas, implementado con Spring Boot 3.5.6 y arquitectura en capas siguiendo principios ACID.

## Tecnologías Utilizadas

- **Spring Boot 3.5.6**
- **Java 21**
- **Spring Data JPA**
- **MySQL 8+**
- **Jakarta Validation**
- **Lombok**
- **Maven**

## Arquitectura

El proyecto sigue una arquitectura en capas:

```
Controller → Service → Repository → Entity
              ↕
            DTOs
```

### Capas del Sistema

1. **Controllers**: Exponen endpoints REST
2. **Services**: Lógica de negocio con transacciones ACID
3. **Repositories**: Acceso a datos con Spring Data JPA
4. **Entities**: Modelos JPA con relaciones
5. **DTOs**: Objetos de transferencia de datos (Request/Response)
6. **Exceptions**: Manejo centralizado de errores

## Entidades del Sistema

### 1. Gestión de Catálogos
- **Categoria**: Categorías de productos
- **Producto**: Productos del inventario
- **MetodoPago**: Métodos de pago disponibles

### 2. Gestión de Usuarios
- **Rol**: Roles del sistema
- **Usuario**: Usuarios del sistema
- **Cliente**: Clientes

### 3. Gestión de Inventario
- **Bodega**: Almacenes/bodegas
- **Inventario**: Stock de productos por bodega
- **MovimientoInventario**: Historial de movimientos

### 4. Operaciones Transaccionales
- **Venta**: Ventas realizadas
- **DetalleVenta**: Líneas de venta
- **Transferencia**: Transferencias entre bodegas
- **DetalleTransferencia**: Líneas de transferencia

## Operaciones ACID Implementadas

### 1. Creación de Venta (`VentaService.crearVenta`)

**Garantías ACID:**
- **Atomicidad**: Si falla cualquier paso, toda la venta se revierte
- **Consistencia**: Valida stock antes de descontar, mantiene integridad de datos
- **Aislamiento**: Transacción aislada con `@Transactional`
- **Durabilidad**: Cambios persistidos o rollback completo

**Flujo:**
1. Valida cliente y usuario activos
2. Valida número de factura único
3. Para cada detalle:
   - Verifica stock disponible
   - Calcula totales con IVA
   - Descuenta del inventario
   - Registra movimiento de inventario
4. Guarda venta con todos sus detalles
5. Rollback automático si hay cualquier error

### 2. Procesamiento de Transferencia (`TransferenciaService.procesarTransferencia`)

**Garantías ACID:**
- Descuenta inventario de bodega origen atómicamente
- Registra movimientos de inventario
- Estado cambia a EN_TRANSITO solo si todo es exitoso

### 3. Recepción de Transferencia (`TransferenciaService.recibirTransferencia`)

**Garantías ACID:**
- Incrementa inventario en bodega destino
- Crea inventario automáticamente si no existe
- Registra movimientos de entrada
- Marca transferencia como RECIBIDA
- Rollback completo si falla cualquier operación

### 4. Ajuste de Inventario (`InventarioService.ajustarInventario`)

**Garantías ACID:**
- Valida que el ajuste no resulte en stock negativo
- Actualiza inventario y registra movimiento atómicamente
- Auditoría completa del cambio

## Endpoints REST API

### 🔐 Autenticación
```
POST   /api/auth/login              - Login de usuario
POST   /api/auth/register           - Registro de nuevo usuario
GET    /api/auth/check-email        - Verificar si email existe
GET    /api/auth/user               - Obtener usuario por email
POST   /api/auth/change-password    - Cambiar contraseña
```

Ver documentación completa en: **`AUTH_API_DOCS.md`**

### Categorías
```
POST   /api/categorias          - Crear categoría
GET    /api/categorias          - Listar todas
GET    /api/categorias/{id}     - Obtener por ID
PUT    /api/categorias/{id}     - Actualizar
DELETE /api/categorias/{id}     - Eliminar
```

### Productos
```
POST   /api/productos           - Crear producto
GET    /api/productos           - Listar todos
GET    /api/productos/activos   - Listar activos
GET    /api/productos/{id}      - Obtener por ID
PUT    /api/productos/{id}      - Actualizar
DELETE /api/productos/{id}      - Eliminar (lógico)
```

### Bodegas
```
POST   /api/bodegas             - Crear bodega
GET    /api/bodegas             - Listar todas
GET    /api/bodegas/activas     - Listar activas
GET    /api/bodegas/{id}        - Obtener por ID
PUT    /api/bodegas/{id}        - Actualizar
DELETE /api/bodegas/{id}        - Eliminar (lógico)
```

### Inventario
```
POST   /api/inventario                    - Crear inventario
POST   /api/inventario/ajustar            - Ajustar inventario
GET    /api/inventario                    - Listar todo
GET    /api/inventario/{id}               - Obtener por ID
GET    /api/inventario/bodega/{idBodega}  - Por bodega
GET    /api/inventario/stock-bajo         - Productos con stock bajo
```

### Clientes
```
POST   /api/clientes            - Crear cliente
GET    /api/clientes            - Listar todos
GET    /api/clientes/activos    - Listar activos
GET    /api/clientes/{id}       - Obtener por ID
PUT    /api/clientes/{id}       - Actualizar
DELETE /api/clientes/{id}       - Eliminar (lógico)
```

### Ventas
```
POST   /api/ventas                      - Crear venta (transaccional)
GET    /api/ventas                      - Listar todas
GET    /api/ventas/{id}                 - Obtener por ID
PUT    /api/ventas/{id}/estado-pago     - Actualizar estado pago
GET    /api/ventas/cliente/{idCliente}  - Por cliente
GET    /api/ventas/periodo              - Por período (fechaInicio, fechaFin)
```

### Transferencias
```
POST   /api/transferencias              - Crear transferencia
GET    /api/transferencias              - Listar todas
GET    /api/transferencias/pendientes   - Listar pendientes
GET    /api/transferencias/{id}         - Obtener por ID
POST   /api/transferencias/{id}/procesar - Procesar (descuenta origen)
POST   /api/transferencias/{id}/recibir  - Recibir (incrementa destino)
```

## Ejemplos de Uso

### 1. Crear una Venta

```json
POST /api/ventas
{
  "idCliente": 1,
  "idUsuario": 1,
  "numeroFactura": "FACT-2025-001",
  "totalDescuento": 0.00,
  "observaciones": "Venta de contado",
  "detalles": [
    {
      "idProducto": 1,
      "cantidad": 5,
      "precioUnitario": 25000.00
    },
    {
      "idProducto": 2,
      "cantidad": 3,
      "precioUnitario": 15000.00
    }
  ]
}
```

### 2. Crear Transferencia entre Bodegas

```json
POST /api/transferencias
{
  "idBodegaOrigen": 1,
  "idBodegaDestino": 2,
  "idUsuario": 1,
  "observaciones": "Transferencia mensual",
  "detalles": [
    {
      "idProducto": 1,
      "cantidad": 10
    }
  ]
}
```

### 3. Procesar Transferencia

```
POST /api/transferencias/1/procesar
```

### 4. Recibir Transferencia

```
POST /api/transferencias/1/recibir
```

### 5. Ajustar Inventario

```json
POST /api/inventario/ajustar
{
  "idProducto": 1,
  "idBodega": 1,
  "cantidad": 50,
  "motivo": "Ingreso de mercancía por compra"
}
```

## Configuración de Base de Datos

### 1. Crear Base de Datos MySQL

```sql
CREATE DATABASE sistemas_transaccionales;
```

### 2. Configurar Credenciales

Editar `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sistemas_transaccionales?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_CONTRASEÑA
```

## Ejecución del Proyecto

### 1. Compilar

```bash
mvn clean install
```

### 2. Ejecutar

```bash
mvn spring-boot:run
```

O desde tu IDE ejecutando `SistemasTransaccionalesApplication.java`

### 3. Acceder a la API

```
http://localhost:8080/api/
```

## Validaciones Implementadas

- **Jakarta Validation** en todos los DTOs
- Validaciones de negocio en servicios:
  - Stock suficiente antes de venta
  - Unicidad de códigos de barras, números de documento
  - Estados válidos de entidades
  - Cantidades no negativas
  - Validaciones de consistencia ACID

## Manejo de Excepciones

### Excepciones Personalizadas

- `ResourceNotFoundException`: Recurso no encontrado (404)
- `InsufficientStockException`: Stock insuficiente (400)
- `BusinessException`: Violación de regla de negocio (400)

### Response de Error

```json
{
  "timestamp": "2025-10-02T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Producto no encontrado con ID: 123"
}
```

## Características ACID Destacadas

### Atomicidad
- Todas las operaciones de venta son todo-o-nada
- Transferencias completas o rollback completo

### Consistencia
- Validaciones exhaustivas antes de modificar datos
- Restricciones de integridad referencial en BD
- Stock nunca queda negativo

### Aislamiento
- `@Transactional` con rollback automático
- Nivel de aislamiento por defecto de Spring

### Durabilidad
- Cambios persistidos en MySQL
- Auditoría con movimientos de inventario
- Timestamps automáticos

## Próximos Pasos Sugeridos

1. Implementar autenticación con Spring Security
2. Agregar paginación en endpoints de listado
3. Implementar reportes y dashboards
4. Agregar tests unitarios e integración
5. Documentar API con Swagger/OpenAPI
6. Implementar caché con Redis
7. Agregar métricas y monitoreo

## Autor

Sistema desarrollado como proyecto académico para Sistemas Transaccionales - Universidad Manuela Beltrán

## Licencia

Este proyecto es de uso académico.

