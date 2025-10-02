-- Script de datos de ejemplo para el sistema de gestión de inventario y ventas
-- Ejecutar después de que Hibernate cree las tablas

-- ============================================
-- 1. ROLES
-- ============================================
INSERT INTO roles (nombre_rol, descripcion) VALUES 
('ADMIN', 'Administrador del sistema'),
('VENDEDOR', 'Vendedor'),
('BODEGUERO', 'Encargado de bodega'),
('GERENTE', 'Gerente');

-- ============================================
-- 2. USUARIOS
-- ============================================
INSERT INTO usuarios (id_rol, tipo_documento, numero_documento, nombres, apellidos, email, contrasena_hash, telefono, estado, fecha_creacion) VALUES
(1, 'CC', '1234567890', 'Juan', 'Pérez', 'admin@sistema.com', 'hash_password_admin', '3001234567', 'ACTIVO', NOW()),
(2, 'CC', '0987654321', 'María', 'González', 'vendedor@sistema.com', 'hash_password_vendedor', '3009876543', 'ACTIVO', NOW()),
(3, 'CC', '1122334455', 'Pedro', 'Martínez', 'bodega@sistema.com', 'hash_password_bodeguero', '3001122334', 'ACTIVO', NOW());

-- ============================================
-- 3. CLIENTES
-- ============================================
INSERT INTO clientes (tipo_documento, numero_documento, nombre, apellidos, direccion, telefono, email, estado, fecha_creacion) VALUES
('CC', '1111111111', 'Carlos', 'Ramírez', 'Calle 100 # 10-20', '3101111111', 'carlos@email.com', 'ACTIVO', NOW()),
('CC', '2222222222', 'Ana', 'López', 'Carrera 50 # 25-30', '3102222222', 'ana@email.com', 'ACTIVO', NOW()),
('NIT', '900123456', 'Empresa XYZ', 'S.A.S', 'Av. Eldorado # 100-50', '6011234567', 'ventas@empresaxyz.com', 'ACTIVO', NOW());

-- ============================================
-- 4. CATEGORÍAS
-- ============================================
INSERT INTO categorias (nombre_categoria, descripcion) VALUES
('Electrónica', 'Productos electrónicos y tecnológicos'),
('Hogar', 'Artículos para el hogar'),
('Oficina', 'Suministros de oficina'),
('Alimentos', 'Productos alimenticios'),
('Ropa', 'Prendas de vestir');

-- ============================================
-- 5. PRODUCTOS
-- ============================================
INSERT INTO productos (id_categoria, codigo_barras, nombre, descripcion, precio_compra, precio_venta, porcentaje_iva, estado, fecha_creacion) VALUES
(1, '7701234567890', 'Laptop HP 15', 'Laptop HP 15 pulgadas, 8GB RAM, 256GB SSD', 1500000.00, 2200000.00, 19.00, 'ACTIVO', NOW()),
(1, '7701234567891', 'Mouse Inalámbrico', 'Mouse inalámbrico Logitech', 25000.00, 45000.00, 19.00, 'ACTIVO', NOW()),
(1, '7701234567892', 'Teclado Mecánico', 'Teclado mecánico RGB', 120000.00, 180000.00, 19.00, 'ACTIVO', NOW()),
(2, '7701234567893', 'Aspiradora', 'Aspiradora industrial 1200W', 200000.00, 350000.00, 19.00, 'ACTIVO', NOW()),
(3, '7701234567894', 'Papel Resma', 'Resma de papel carta 500 hojas', 8000.00, 15000.00, 19.00, 'ACTIVO', NOW()),
(3, '7701234567895', 'Bolígrafos Caja x12', 'Caja de 12 bolígrafos azules', 3000.00, 6000.00, 19.00, 'ACTIVO', NOW()),
(4, '7701234567896', 'Café Premium 500g', 'Café colombiano premium', 15000.00, 25000.00, 5.00, 'ACTIVO', NOW()),
(5, '7701234567897', 'Camiseta Polo', 'Camiseta tipo polo talla M', 30000.00, 60000.00, 19.00, 'ACTIVO', NOW());

-- ============================================
-- 6. BODEGAS
-- ============================================
INSERT INTO bodegas (nombre, ubicacion, estado, fecha_creacion) VALUES
('Bodega Principal', 'Bogotá - Centro', 'ACTIVA', NOW()),
('Bodega Norte', 'Bogotá - Calle 170', 'ACTIVA', NOW()),
('Bodega Sur', 'Bogotá - Autopista Sur', 'ACTIVA', NOW());

-- ============================================
-- 7. INVENTARIO INICIAL
-- ============================================
INSERT INTO inventario (id_producto, id_bodega, cantidad, stock_minimo, fecha_actualizacion) VALUES
-- Bodega Principal
(1, 1, 50, 10, NOW()),
(2, 1, 100, 20, NOW()),
(3, 1, 75, 15, NOW()),
(4, 1, 30, 5, NOW()),
(5, 1, 200, 50, NOW()),
(6, 1, 150, 30, NOW()),
(7, 1, 80, 20, NOW()),
(8, 1, 60, 15, NOW()),

-- Bodega Norte
(1, 2, 30, 10, NOW()),
(2, 2, 80, 20, NOW()),
(3, 2, 40, 10, NOW()),
(5, 2, 100, 30, NOW()),

-- Bodega Sur
(4, 3, 25, 5, NOW()),
(7, 3, 50, 15, NOW()),
(8, 3, 40, 10, NOW());

-- ============================================
-- 8. MÉTODOS DE PAGO
-- ============================================
INSERT INTO metodos_pago (nombre, descripcion) VALUES
('Efectivo', 'Pago en efectivo'),
('Tarjeta Débito', 'Pago con tarjeta débito'),
('Tarjeta Crédito', 'Pago con tarjeta crédito'),
('Transferencia', 'Transferencia bancaria'),
('PSE', 'Pago electrónico PSE');

-- ============================================
-- 9. MOVIMIENTOS DE INVENTARIO INICIALES
-- ============================================
INSERT INTO movimientos_inventario (id_producto, id_bodega, tipo_movimiento, cantidad_anterior, cantidad_nueva, motivo, referencia, fecha_movimiento) VALUES
-- Bodega Principal
(1, 1, 'ENTRADA', 0, 50, 'Inventario inicial', 'INV-INICIAL-1', NOW()),
(2, 1, 'ENTRADA', 0, 100, 'Inventario inicial', 'INV-INICIAL-2', NOW()),
(3, 1, 'ENTRADA', 0, 75, 'Inventario inicial', 'INV-INICIAL-3', NOW()),
(4, 1, 'ENTRADA', 0, 30, 'Inventario inicial', 'INV-INICIAL-4', NOW()),
(5, 1, 'ENTRADA', 0, 200, 'Inventario inicial', 'INV-INICIAL-5', NOW()),
(6, 1, 'ENTRADA', 0, 150, 'Inventario inicial', 'INV-INICIAL-6', NOW()),
(7, 1, 'ENTRADA', 0, 80, 'Inventario inicial', 'INV-INICIAL-7', NOW()),
(8, 1, 'ENTRADA', 0, 60, 'Inventario inicial', 'INV-INICIAL-8', NOW());

-- ============================================
-- NOTA: Las ventas y transferencias se deben crear
-- a través de la API para garantizar transacciones ACID
-- ============================================

