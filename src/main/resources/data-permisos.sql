-- Script SQL con datos de ejemplo para permisos

-- Insertar permisos básicos del sistema
INSERT INTO permisos (nombre, descripcion) VALUES
-- Permisos de Usuarios
('USUARIOS_CREAR', 'Permite crear nuevos usuarios en el sistema'),
('USUARIOS_LEER', 'Permite visualizar información de usuarios'),
('USUARIOS_ACTUALIZAR', 'Permite modificar información de usuarios existentes'),
('USUARIOS_ELIMINAR', 'Permite eliminar usuarios del sistema'),

-- Permisos de Roles
('ROLES_CREAR', 'Permite crear nuevos roles'),
('ROLES_LEER', 'Permite visualizar roles del sistema'),
('ROLES_ACTUALIZAR', 'Permite modificar roles existentes'),
('ROLES_ELIMINAR', 'Permite eliminar roles'),
('ROLES_ASIGNAR_PERMISOS', 'Permite asignar o remover permisos a roles'),

-- Permisos de Productos
('PRODUCTOS_CREAR', 'Permite crear nuevos productos'),
('PRODUCTOS_LEER', 'Permite visualizar productos'),
('PRODUCTOS_ACTUALIZAR', 'Permite modificar productos existentes'),
('PRODUCTOS_ELIMINAR', 'Permite eliminar productos'),

-- Permisos de Categorías
('CATEGORIAS_CREAR', 'Permite crear nuevas categorías'),
('CATEGORIAS_LEER', 'Permite visualizar categorías'),
('CATEGORIAS_ACTUALIZAR', 'Permite modificar categorías existentes'),
('CATEGORIAS_ELIMINAR', 'Permite eliminar categorías'),

-- Permisos de Inventario
('INVENTARIO_CREAR', 'Permite crear registros de inventario'),
('INVENTARIO_LEER', 'Permite visualizar inventario'),
('INVENTARIO_ACTUALIZAR', 'Permite modificar inventario'),
('INVENTARIO_ELIMINAR', 'Permite eliminar registros de inventario'),
('INVENTARIO_AJUSTAR', 'Permite realizar ajustes de inventario'),

-- Permisos de Bodegas
('BODEGAS_CREAR', 'Permite crear nuevas bodegas'),
('BODEGAS_LEER', 'Permite visualizar bodegas'),
('BODEGAS_ACTUALIZAR', 'Permite modificar bodegas existentes'),
('BODEGAS_ELIMINAR', 'Permite eliminar bodegas'),

-- Permisos de Transferencias
('TRANSFERENCIAS_CREAR', 'Permite crear transferencias entre bodegas'),
('TRANSFERENCIAS_LEER', 'Permite visualizar transferencias'),
('TRANSFERENCIAS_ACTUALIZAR', 'Permite modificar transferencias'),
('TRANSFERENCIAS_ELIMINAR', 'Permite eliminar transferencias'),
('TRANSFERENCIAS_APROBAR', 'Permite aprobar transferencias pendientes'),

-- Permisos de Ventas
('VENTAS_CREAR', 'Permite crear nuevas ventas'),
('VENTAS_LEER', 'Permite visualizar ventas'),
('VENTAS_ACTUALIZAR', 'Permite modificar ventas'),
('VENTAS_ELIMINAR', 'Permite eliminar ventas'),
('VENTAS_ANULAR', 'Permite anular ventas'),

-- Permisos de Clientes
('CLIENTES_CREAR', 'Permite crear nuevos clientes'),
('CLIENTES_LEER', 'Permite visualizar clientes'),
('CLIENTES_ACTUALIZAR', 'Permite modificar clientes existentes'),
('CLIENTES_ELIMINAR', 'Permite eliminar clientes'),

-- Permisos de Reportes
('REPORTES_VENTAS', 'Permite generar reportes de ventas'),
('REPORTES_INVENTARIO', 'Permite generar reportes de inventario'),
('REPORTES_FINANCIEROS', 'Permite generar reportes financieros'),
('REPORTES_AUDITORIA', 'Permite generar reportes de auditoría'),

-- Permisos Administrativos
('SISTEMA_CONFIGURAR', 'Permite configurar parámetros del sistema'),
('SISTEMA_BACKUP', 'Permite realizar respaldos del sistema'),
('SISTEMA_RESTORE', 'Permite restaurar respaldos'),
('SISTEMA_LOGS', 'Permite visualizar logs del sistema');


-- Ejemplo: Asignar permisos completos al rol de Administrador (asumiendo que id_rol = 1 es Administrador)
-- Nota: Ajusta el id_rol según tu base de datos
INSERT INTO rol_permisos (id_rol, id_permiso)
SELECT 1, id_permiso FROM permisos
WHERE EXISTS (SELECT 1 FROM roles WHERE id_rol = 1);

-- Ejemplo: Asignar permisos básicos a un rol de Vendedor (asumiendo que id_rol = 2 es Vendedor)
INSERT INTO rol_permisos (id_rol, id_permiso)
SELECT 2, id_permiso FROM permisos
WHERE nombre IN (
    'PRODUCTOS_LEER',
    'CATEGORIAS_LEER',
    'INVENTARIO_LEER',
    'VENTAS_CREAR',
    'VENTAS_LEER',
    'CLIENTES_CREAR',
    'CLIENTES_LEER',
    'CLIENTES_ACTUALIZAR'
) AND EXISTS (SELECT 1 FROM roles WHERE id_rol = 2);

-- Ejemplo: Asignar permisos de bodega a un rol de Bodeguero (asumiendo que id_rol = 3 es Bodeguero)
INSERT INTO rol_permisos (id_rol, id_permiso)
SELECT 3, id_permiso FROM permisos
WHERE nombre IN (
    'PRODUCTOS_LEER',
    'INVENTARIO_CREAR',
    'INVENTARIO_LEER',
    'INVENTARIO_ACTUALIZAR',
    'INVENTARIO_AJUSTAR',
    'BODEGAS_LEER',
    'TRANSFERENCIAS_CREAR',
    'TRANSFERENCIAS_LEER',
    'TRANSFERENCIAS_ACTUALIZAR'
) AND EXISTS (SELECT 1 FROM roles WHERE id_rol = 3);

