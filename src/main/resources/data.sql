-- ============================================================
--  Datos de ejemplo para el negocio completo (Novatech Store).
--  Se ejecuta al iniciar la app. INSERT IGNORE evita duplicados
--  si los datos ya existen (al reiniciar no se rompe ni se repite).
--  NOTA: los nombres de tabla van en snake_case (perfil_cliente, etc.)
--  porque asi los crea Hibernate por defecto.
-- ============================================================

-- Categorias
INSERT IGNORE INTO categoria (id_categoria, nombre, descripcion) VALUES
  (1, 'Notebooks', 'Computadoras portatiles'),
  (2, 'Perifericos', 'Teclados, mouse y accesorios'),
  (3, 'Monitores', 'Pantallas y monitores');

-- Productos
INSERT IGNORE INTO producto (id_producto, nombre, descripcion, precio, stock, id_categoria, proveedor) VALUES
  (1, 'Notebook Lenovo IdeaPad', 'Ryzen 5, 16GB RAM, 512GB SSD', 750000.00, 12, 1, 'Lenovo'),
  (2, 'Teclado Mecanico Redragon', 'Switch red, retroiluminado', 45000.00, 30, 2, 'Redragon'),
  (3, 'Mouse Logitech G203', 'Mouse gamer 8000 DPI', 22000.00, 50, 2, 'Logitech'),
  (4, 'Monitor Samsung 24"', 'Full HD 75Hz IPS', 180000.00, 8, 3, 'Samsung');

-- Usuarios
-- IMPORTANTE: las contrasenas se guardan HASHEADAS con BCrypt (nunca en texto plano).
-- Los textos que empiezan con "$2a$10$..." son el resultado de aplicarle BCrypt a la
-- contrasena real. Aca dejamos:
--   admin@novatech.com   -> contrasena real: admin123   (rol ADMIN)
--   cliente@novatech.com -> contrasena real: cliente123 (rol CLIENTE)
-- El login del backend compara lo que escribe el usuario contra estos hashes.
INSERT IGNORE INTO usuario (id_usuario, nombre, email, contrasena, rol, fecha_registro) VALUES
  (1, 'Admin Novatech', 'admin@novatech.com', '$2a$10$5/A7KT9NtU4SAMeujmt83.dCGMni9DDkrzH9mY5CAwKsIiRc9d/S2', 'ADMIN', NOW()),
  (2, 'Cliente Demo', 'cliente@novatech.com', '$2a$10$dxxZcXFu1jsKxgfTxpxCe.HkPXphC5Jm4K9p15yA2RQ7aPfYkjgiS', 'CLIENTE', NOW());

-- Por si estos usuarios YA existian de una version anterior (cuando la clave estaba en
-- texto plano), el INSERT IGNORE de arriba no los toca. Para asegurarnos de que el login
-- funcione, forzamos aca la contrasena hasheada y el rol correcto de los dos usuarios demo.
UPDATE usuario SET contrasena = '$2a$10$5/A7KT9NtU4SAMeujmt83.dCGMni9DDkrzH9mY5CAwKsIiRc9d/S2', rol = 'ADMIN'
  WHERE email = 'admin@novatech.com';
UPDATE usuario SET contrasena = '$2a$10$dxxZcXFu1jsKxgfTxpxCe.HkPXphC5Jm4K9p15yA2RQ7aPfYkjgiS', rol = 'CLIENTE'
  WHERE email = 'cliente@novatech.com';

-- Perfil de cliente (datos extra del usuario cliente)
INSERT IGNORE INTO perfil_cliente (id_cliente, id_usuario, direccion, telefono, historial_crediticio, tipo_cliente) VALUES
  (1, 2, 'Av. Siempre Viva 742', '11-5555-1234', 700, 'MINORISTA');

-- Carrito del cliente
INSERT IGNORE INTO carrito (id_carrito, id_usuario, fecha_creacion) VALUES
  (1, 2, NOW());

-- Detalle del carrito
INSERT IGNORE INTO detalle_carrito (id_detalle_carrito, id_carrito, id_producto, cantidad) VALUES
  (1, 1, 2, 1),
  (2, 1, 3, 2);

-- Pedido del cliente
INSERT IGNORE INTO pedido (id_pedido, id_usuario, fecha, estado, total) VALUES
  (1, 2, NOW(), 'PAGADO', 89000.00);

-- Detalle del pedido
INSERT IGNORE INTO detalle_pedido (id_detalle, id_pedido, id_producto, cantidad, precio_unitario) VALUES
  (1, 1, 2, 1, 45000.00),
  (2, 1, 3, 2, 22000.00);

-- Pago del pedido (aprobado por el admin)
INSERT IGNORE INTO pago (id_pago, id_pedido, fecha_pago, monto, metodo, aprobado_por) VALUES
  (1, 1, NOW(), 89000.00, 'TARJETA', 1);

-- Plan de cuotas del pedido
INSERT IGNORE INTO plan_cuotas (id_plan, id_cliente, id_pedido, cantidad_cuotas, interes, estado) VALUES
  (1, 1, 1, 3, 15.00, 'ACTIVO');

-- Envio del pedido
INSERT IGNORE INTO envio (id_envio, id_pedido, direccion_envio, empresa_logistica, estado_envio) VALUES
  (1, 1, 'Av. Siempre Viva 742', 'Andreani', 'EN_CAMINO');

-- Resenas de productos
INSERT IGNORE INTO resena (id_resena, id_producto, id_usuario, comentario, puntuacion, fecha) VALUES
  (1, 2, 2, 'Excelente teclado, muy comodo', 5, NOW()),
  (2, 3, 2, 'Buen mouse por el precio', 4, NOW());
