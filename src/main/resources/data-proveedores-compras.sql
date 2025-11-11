-- Script SQL con datos de ejemplo para Proveedores y Compras

-- ========================================
-- PROVEEDORES DE EJEMPLO
-- ========================================

INSERT INTO proveedores (nit_ruc, nombre, nombre_contacto, direccion, telefono, email, estado, observaciones) VALUES
('900123456-7', 'Distribuidora Nacional S.A.', 'Carlos Mendoza', 'Calle 100 #15-20, Bogotá', '3101234567', 'ventas@distribuidoranacional.com', 'ACTIVO', 'Proveedor principal de tecnología'),
('800987654-3', 'Importadora Global LTDA', 'Ana María López', 'Carrera 50 #25-35, Medellín', '3209876543', 'contacto@importadoraglobal.com', 'ACTIVO', 'Especializado en importaciones'),
('900555888-1', 'Suministros del Valle', 'Jorge Ramírez', 'Avenida 6N #28-45, Cali', '3155558888', 'suministros@delvalle.com', 'ACTIVO', 'Proveedor de oficina y papelería'),
('800222333-4', 'Tecnología y Más S.A.S', 'Patricia Gómez', 'Calle 72 #10-50, Bogotá', '3002223334', 'info@tecnologiaymas.com', 'ACTIVO', 'Equipos de cómputo y electrónica'),
('900444555-6', 'Comercializadora del Norte', 'Luis Fernández', 'Carrera 45 #80-12, Barranquilla', '3154445556', 'comercial@delnorte.com', 'ACTIVO', 'Variedad de productos de consumo');

-- ========================================
-- AGREGAR NUEVOS PERMISOS
-- ========================================

INSERT INTO permisos (nombre, descripcion) VALUES
-- Permisos de Proveedores
('PROVEEDORES_CREAR', 'Permite crear nuevos proveedores'),
('PROVEEDORES_LEER', 'Permite visualizar proveedores'),
('PROVEEDORES_ACTUALIZAR', 'Permite modificar proveedores existentes'),
('PROVEEDORES_ELIMINAR', 'Permite eliminar proveedores'),

-- Permisos de Compras
('COMPRAS_CREAR', 'Permite crear nuevas órdenes de compra'),
('COMPRAS_LEER', 'Permite visualizar órdenes de compra'),
('COMPRAS_ACTUALIZAR', 'Permite modificar órdenes de compra'),
('COMPRAS_ELIMINAR', 'Permite eliminar órdenes de compra'),
('COMPRAS_RECIBIR', 'Permite marcar compras como recibidas e ingresar stock'),
('COMPRAS_CANCELAR', 'Permite cancelar órdenes de compra');

-- ========================================
-- ASIGNAR PERMISOS AL ROL ADMINISTRADOR
-- ========================================
-- Nota: Asumiendo que el rol Administrador tiene id_rol = 1
-- Obtener los IDs de los nuevos permisos y asignarlos

INSERT INTO rol_permisos (id_rol, id_permiso)
SELECT 1, id_permiso FROM permisos
WHERE nombre IN (
    'PROVEEDORES_CREAR',
    'PROVEEDORES_LEER',
    'PROVEEDORES_ACTUALIZAR',
    'PROVEEDORES_ELIMINAR',
    'COMPRAS_CREAR',
    'COMPRAS_LEER',
    'COMPRAS_ACTUALIZAR',
    'COMPRAS_ELIMINAR',
    'COMPRAS_RECIBIR',
    'COMPRAS_CANCELAR'
)
AND EXISTS (SELECT 1 FROM roles WHERE id_rol = 1);

-- ========================================
-- EJEMPLO DE COMPRA
-- ========================================
-- Nota: Estos INSERT son ejemplos, ajusta los IDs según tu base de datos

-- Ejemplo de compra (requiere que existan proveedor, usuario, productos y bodegas)
/*
INSERT INTO compras (id_proveedor, id_usuario, numero_compra, fecha_compra, subtotal, total_iva, total_compra, estado, observaciones)
VALUES (1, 1, 'COMP-2025-001', '2025-11-11', 1000000.00, 190000.00, 1190000.00, 'PENDIENTE', 'Primera compra de ejemplo');

-- Detalle de compra
INSERT INTO detalle_compras (id_compra, id_producto, id_bodega, cantidad, precio_unitario_compra, subtotal_linea, total_iva_linea, total_linea)
VALUES 
(1, 1, 1, 10, 50000.00, 500000.00, 95000.00, 595000.00),
(1, 2, 1, 20, 25000.00, 500000.00, 95000.00, 595000.00);
*/

