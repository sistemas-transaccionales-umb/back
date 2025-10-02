# Ejemplos de Uso de la API

## Tabla de Contenidos
- [Configuración Inicial](#configuración-inicial)
- [Categorías](#categorías)
- [Productos](#productos)
- [Bodegas](#bodegas)
- [Inventario](#inventario)
- [Clientes](#clientes)
- [Ventas](#ventas)
- [Transferencias](#transferencias)

---

## Configuración Inicial

**Base URL**: `http://localhost:8080`

---

## Categorías

### 1. Crear Categoría
```http
POST /api/categorias
Content-Type: application/json

{
  "nombreCategoria": "Electrónica",
  "descripcion": "Productos electrónicos y tecnológicos"
}
```

### 2. Listar Todas las Categorías
```http
GET /api/categorias
```

### 3. Obtener Categoría por ID
```http
GET /api/categorias/1
```

### 4. Actualizar Categoría
```http
PUT /api/categorias/1
Content-Type: application/json

{
  "nombreCategoria": "Electrónica y Computación",
  "descripcion": "Productos electrónicos, computación y tecnología"
}
```

---

## Productos

### 1. Crear Producto
```http
POST /api/productos
Content-Type: application/json

{
  "idCategoria": 1,
  "codigoBarras": "7701234567890",
  "nombre": "Laptop HP 15",
  "descripcion": "Laptop HP 15 pulgadas, 8GB RAM, 256GB SSD",
  "precioCompra": 1500000.00,
  "precioVenta": 2200000.00,
  "porcentajeIva": 19.00
}
```

### 2. Listar Productos Activos
```http
GET /api/productos/activos
```

### 3. Actualizar Producto
```http
PUT /api/productos/1
Content-Type: application/json

{
  "idCategoria": 1,
  "codigoBarras": "7701234567890",
  "nombre": "Laptop HP 15 - Actualizado",
  "descripcion": "Laptop HP 15 pulgadas, 16GB RAM, 512GB SSD",
  "precioCompra": 1800000.00,
  "precioVenta": 2500000.00,
  "porcentajeIva": 19.00
}
```

---

## Bodegas

### 1. Crear Bodega
```http
POST /api/bodegas
Content-Type: application/json

{
  "nombre": "Bodega Principal",
  "ubicacion": "Bogotá - Centro"
}
```

### 2. Listar Bodegas Activas
```http
GET /api/bodegas/activas
```

---

## Inventario

### 1. Crear Inventario
```http
POST /api/inventario
Content-Type: application/json

{
  "idProducto": 1,
  "idBodega": 1,
  "cantidad": 50,
  "stockMinimo": 10
}
```

### 2. Ajustar Inventario (Entrada)
```http
POST /api/inventario/ajustar
Content-Type: application/json

{
  "idProducto": 1,
  "idBodega": 1,
  "cantidad": 20,
  "motivo": "Ingreso de mercancía por compra"
}
```

### 3. Ajustar Inventario (Salida)
```http
POST /api/inventario/ajustar
Content-Type: application/json

{
  "idProducto": 1,
  "idBodega": 1,
  "cantidad": -5,
  "motivo": "Ajuste por producto dañado"
}
```

### 4. Consultar Inventario por Bodega
```http
GET /api/inventario/bodega/1
```

### 5. Consultar Productos con Stock Bajo
```http
GET /api/inventario/stock-bajo
```

---

## Clientes

### 1. Crear Cliente
```http
POST /api/clientes
Content-Type: application/json

{
  "tipoDocumento": "CC",
  "numeroDocumento": "1111111111",
  "nombre": "Carlos",
  "apellidos": "Ramírez",
  "direccion": "Calle 100 # 10-20",
  "telefono": "3101111111",
  "email": "carlos@email.com"
}
```

### 2. Listar Clientes Activos
```http
GET /api/clientes/activos
```

### 3. Actualizar Cliente
```http
PUT /api/clientes/1
Content-Type: application/json

{
  "tipoDocumento": "CC",
  "numeroDocumento": "1111111111",
  "nombre": "Carlos Alberto",
  "apellidos": "Ramírez González",
  "direccion": "Calle 100 # 10-20 Apto 301",
  "telefono": "3101111111",
  "email": "carlos.ramirez@email.com"
}
```

---

## Ventas

### 1. Crear Venta (Operación ACID Crítica)
```http
POST /api/ventas
Content-Type: application/json

{
  "idCliente": 1,
  "idUsuario": 1,
  "numeroFactura": "FACT-2025-001",
  "totalDescuento": 0.00,
  "observaciones": "Venta de contado",
  "detalles": [
    {
      "idProducto": 1,
      "cantidad": 2,
      "precioUnitario": 2200000.00
    },
    {
      "idProducto": 2,
      "cantidad": 3,
      "precioUnitario": 45000.00
    }
  ]
}
```

**Nota**: Esta operación es transaccional ACID:
- Valida stock disponible
- Descuenta inventario automáticamente
- Registra movimientos de inventario
- Rollback completo si falla cualquier paso

### 2. Crear Venta con Descuento
```http
POST /api/ventas
Content-Type: application/json

{
  "idCliente": 1,
  "idUsuario": 1,
  "numeroFactura": "FACT-2025-002",
  "totalDescuento": 50000.00,
  "observaciones": "Venta con descuento por cliente frecuente",
  "olaCode": "OLA-2025-ABC123",
  "detalles": [
    {
      "idProducto": 3,
      "cantidad": 5,
      "precioUnitario": 180000.00
    }
  ]
}
```

### 3. Consultar Venta
```http
GET /api/ventas/1
```

**Response esperado:**
```json
{
  "idVenta": 1,
  "nombreCliente": "Carlos Ramírez",
  "nombreUsuario": "Juan Pérez",
  "numeroFactura": "FACT-2025-001",
  "fechaVenta": "2025-10-02T10:30:00",
  "totalDescuento": 0.00,
  "totalVenta": 4535000.00,
  "observaciones": "Venta de contado",
  "olaCode": null,
  "estadoPago": "PENDIENTE",
  "detalles": [
    {
      "idDetalleVenta": 1,
      "nombreProducto": "Laptop HP 15",
      "codigoBarras": "7701234567890",
      "cantidad": 2,
      "precioUnitario": 2200000.00,
      "subtotalLinea": 4400000.00,
      "totalIvaLinea": 836000.00,
      "totalLinea": 5236000.00
    }
  ]
}
```

### 4. Actualizar Estado de Pago
```http
PUT /api/ventas/1/estado-pago?estadoPago=PAGADO
```

### 5. Consultar Ventas por Cliente
```http
GET /api/ventas/cliente/1
```

### 6. Consultar Ventas por Período
```http
GET /api/ventas/periodo?fechaInicio=2025-10-01T00:00:00&fechaFin=2025-10-31T23:59:59
```

---

## Transferencias

### 1. Crear Transferencia
```http
POST /api/transferencias
Content-Type: application/json

{
  "idBodegaOrigen": 1,
  "idBodegaDestino": 2,
  "idUsuario": 1,
  "observaciones": "Transferencia mensual de inventario",
  "detalles": [
    {
      "idProducto": 1,
      "cantidad": 10
    },
    {
      "idProducto": 2,
      "cantidad": 20
    }
  ]
}
```

**Estado inicial**: `PENDIENTE`

### 2. Procesar Transferencia (Operación ACID)
```http
POST /api/transferencias/1/procesar
```

**Esta operación**:
- Descuenta inventario de bodega origen
- Registra movimientos de salida
- Cambia estado a `EN_TRANSITO`
- Rollback completo si falla

### 3. Recibir Transferencia (Operación ACID)
```http
POST /api/transferencias/1/recibir
```

**Esta operación**:
- Incrementa inventario en bodega destino
- Crea inventario si no existe
- Registra movimientos de entrada
- Marca como `RECIBIDO`
- Registra fecha de recepción
- Rollback completo si falla

### 4. Consultar Transferencia
```http
GET /api/transferencias/1
```

### 5. Listar Transferencias Pendientes
```http
GET /api/transferencias/pendientes
```

---

## Flujo Completo de Ejemplo

### Paso 1: Crear Datos Maestros
```bash
# 1. Crear categoría
POST /api/categorias
{ "nombreCategoria": "Electrónica", "descripcion": "..." }

# 2. Crear producto
POST /api/productos
{ "idCategoria": 1, "codigoBarras": "...", ... }

# 3. Crear bodegas
POST /api/bodegas
{ "nombre": "Bodega Principal", ... }

# 4. Crear inventario
POST /api/inventario
{ "idProducto": 1, "idBodega": 1, "cantidad": 100, ... }

# 5. Crear cliente
POST /api/clientes
{ "tipoDocumento": "CC", ... }
```

### Paso 2: Realizar Venta
```bash
POST /api/ventas
{
  "idCliente": 1,
  "idUsuario": 1,
  "numeroFactura": "FACT-001",
  "detalles": [...]
}
```

### Paso 3: Transferencia entre Bodegas
```bash
# 1. Crear transferencia
POST /api/transferencias
{ "idBodegaOrigen": 1, "idBodegaDestino": 2, ... }

# 2. Procesar (descuenta origen)
POST /api/transferencias/1/procesar

# 3. Recibir (incrementa destino)
POST /api/transferencias/1/recibir
```

### Paso 4: Ajustar Inventario
```bash
POST /api/inventario/ajustar
{
  "idProducto": 1,
  "idBodega": 1,
  "cantidad": 50,
  "motivo": "Compra a proveedor"
}
```

---

## Validaciones Importantes

### Venta
- ❌ Stock insuficiente → Error 400
- ❌ Cliente inactivo → Error 400
- ❌ Número de factura duplicado → Error 400
- ❌ Producto inactivo → Error 400

### Transferencia
- ❌ Bodega origen = Bodega destino → Error 400
- ❌ Stock insuficiente en origen → Error 400
- ❌ Estado incorrecto para procesar/recibir → Error 400

### Inventario
- ❌ Ajuste que resulte en stock negativo → Error 400
- ❌ Producto-Bodega duplicado → Error 400

---

## Códigos de Respuesta HTTP

- **200 OK**: Operación exitosa
- **201 Created**: Recurso creado
- **204 No Content**: Eliminación exitosa
- **400 Bad Request**: Error de validación
- **404 Not Found**: Recurso no encontrado
- **500 Internal Server Error**: Error del servidor

---

## Testing con cURL

### Crear una venta
```bash
curl -X POST http://localhost:8080/api/ventas \
  -H "Content-Type: application/json" \
  -d '{
    "idCliente": 1,
    "idUsuario": 1,
    "numeroFactura": "FACT-2025-001",
    "totalDescuento": 0,
    "detalles": [
      {
        "idProducto": 1,
        "cantidad": 2,
        "precioUnitario": 2200000.00
      }
    ]
  }'
```

### Procesar transferencia
```bash
curl -X POST http://localhost:8080/api/transferencias/1/procesar
```

---

## Notas Finales

1. **Orden de creación recomendado**:
   - Categorías → Productos → Bodegas → Inventario → Clientes → Ventas

2. **Operaciones transaccionales críticas**:
   - Crear venta
   - Procesar transferencia
   - Recibir transferencia
   - Ajustar inventario

3. **Auditoría automática**:
   - Todos los movimientos de inventario se registran
   - Timestamps automáticos en creación
   - Estados de entidades para soft delete

4. **Rollback automático**:
   - Cualquier error en operación transaccional revierte todos los cambios
   - Garantía ACID completa

