-- Script SQL para crear tablas de Proveedores y Compras

-- Tabla de proveedores
CREATE TABLE IF NOT EXISTS proveedores (
    id_proveedor INT AUTO_INCREMENT PRIMARY KEY,
    nit_ruc VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(200) NOT NULL,
    nombre_contacto VARCHAR(100),
    direccion TEXT,
    telefono VARCHAR(20),
    email VARCHAR(255),
    estado ENUM('ACTIVO', 'INACTIVO') DEFAULT 'ACTIVO',
    observaciones TEXT,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_nit_ruc (nit_ruc),
    INDEX idx_nombre (nombre),
    INDEX idx_estado (estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de compras
CREATE TABLE IF NOT EXISTS compras (
    id_compra BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_proveedor INT NOT NULL,
    id_usuario INT NOT NULL,
    numero_compra VARCHAR(50) NOT NULL UNIQUE,
    fecha_compra DATE NOT NULL,
    subtotal DECIMAL(14, 2) NOT NULL DEFAULT 0.00,
    total_iva DECIMAL(14, 2) NOT NULL DEFAULT 0.00,
    total_compra DECIMAL(14, 2) NOT NULL DEFAULT 0.00,
    estado ENUM('PENDIENTE', 'RECIBIDA', 'CANCELADA') DEFAULT 'PENDIENTE',
    observaciones TEXT,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_proveedor) REFERENCES proveedores(id_proveedor),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    INDEX idx_numero_compra (numero_compra),
    INDEX idx_proveedor (id_proveedor),
    INDEX idx_usuario (id_usuario),
    INDEX idx_fecha_compra (fecha_compra),
    INDEX idx_estado (estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de detalle de compras
CREATE TABLE IF NOT EXISTS detalle_compras (
    id_detalle_compra BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_compra BIGINT NOT NULL,
    id_producto INT NOT NULL,
    id_bodega INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario_compra DECIMAL(12, 2) NOT NULL,
    subtotal_linea DECIMAL(14, 2) NOT NULL,
    total_iva_linea DECIMAL(14, 2) NOT NULL,
    total_linea DECIMAL(14, 2) NOT NULL,
    FOREIGN KEY (id_compra) REFERENCES compras(id_compra) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto),
    FOREIGN KEY (id_bodega) REFERENCES bodegas(id_bodega),
    INDEX idx_compra (id_compra),
    INDEX idx_producto (id_producto),
    INDEX idx_bodega (id_bodega)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

