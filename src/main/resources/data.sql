-- ============================================================
--  Datos de ejemplo para el negocio completo (Novatech Store).
--  Se ejecuta al iniciar la app. INSERT IGNORE evita duplicados
--  si los datos ya existen (al reiniciar no se rompe ni se repite).
--  NOTA: los nombres de tabla van en snake_case (perfil_cliente, etc.)
--  porque asi los crea Hibernate por defecto.
-- ============================================================

-- Categorias (7 tipos que soporta la tienda)
-- ON DUPLICATE KEY UPDATE: si ya existian, las corregimos (por datos viejos de prueba).
INSERT INTO categoria (id_categoria, nombre, descripcion) VALUES
  (1, 'Notebooks', 'Computadoras portatiles'),
  (2, 'Perifericos', 'Teclados, mouse y accesorios'),
  (3, 'Monitores', 'Pantallas y monitores'),
  (4, 'Componentes', 'Placas de video, procesadores y RAM'),
  (5, 'Almacenamiento', 'SSD, HDD y pendrives'),
  (6, 'Audio', 'Auriculares y parlantes'),
  (7, 'Sillas Gamer', 'Sillas ergonomicas para gaming')
ON DUPLICATE KEY UPDATE
  nombre = VALUES(nombre),
  descripcion = VALUES(descripcion);

-- Borramos categorias basura que no correspondan al catalogo (ids de prueba).
DELETE FROM categoria WHERE id_categoria > 7;

-- Reasignamos productos huerfanos y eliminamos categorias con nombres invalidos.
UPDATE producto SET id_categoria = 1
WHERE id_categoria IN (
  SELECT id_categoria FROM (
    SELECT id_categoria FROM categoria
    WHERE id_categoria > 7
       OR CHAR_LENGTH(TRIM(nombre)) < 2
       OR nombre LIKE '%.%'
       OR nombre LIKE '%--%'
       OR nombre NOT REGEXP '^[A-Za-zÁÉÍÓÚáéíóúÑñüÜ]'
  ) AS categorias_basura
);

DELETE FROM categoria
WHERE id_categoria > 7
   OR CHAR_LENGTH(TRIM(nombre)) < 2
   OR nombre LIKE '%.%'
   OR nombre LIKE '%--%'
   OR nombre NOT REGEXP '^[A-Za-zÁÉÍÓÚáéíóúÑñüÜ]';

-- Productos (60 en total repartidos en las 7 categorias)
-- ON DUPLICATE KEY UPDATE: corrige productos viejos o de prueba que ya existian.
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock, id_categoria, proveedor) VALUES
  -- Notebooks (cat 1)
  (1,  'Notebook Lenovo IdeaPad',              'Ryzen 5, 16GB RAM, 512GB SSD', 750000.00, 12, 1, 'Lenovo'),
  (5,  'Notebook ASUS VivoBook 15',             'Intel Core i5-1235U, 8GB RAM, 512GB SSD, pantalla 15.6 FHD', 680000.00, 8,  1, 'ASUS'),
  (6,  'Notebook Acer Aspire 5',                'Ryzen 7 5700U, 16GB RAM, 1TB SSD, pantalla 15.6 FHD', 890000.00, 6,  1, 'Acer'),
  (7,  'Notebook HP Pavilion 14',               'Intel i5 12th Gen, 8GB RAM, 256GB SSD, pantalla 14', 620000.00, 10, 1, 'HP'),
  (8,  'Notebook Dell Inspiron 15',             'Intel i7 12th Gen, 16GB RAM, 512GB SSD, pantalla 15.6 FHD', 1100000.00, 5, 1, 'Dell'),
  (9,  'Notebook Lenovo Legion 5',              'Ryzen 7 6800H, RTX 3060, 16GB RAM, 512GB SSD', 1800000.00, 4, 1, 'Lenovo'),
  (10, 'Notebook MSI Katana 15',                'Intel i7-12650H, RTX 4060, 16GB RAM, 1TB SSD, 144Hz', 2100000.00, 3, 1, 'MSI'),
  (11, 'Notebook MacBook Air M2',               'Chip Apple M2, 8GB RAM, 256GB SSD, pantalla 13.6 Retina', 1450000.00, 7, 1, 'Apple'),
  (12, 'Notebook Samsung Galaxy Book3',         'Intel i5, 16GB RAM, 512GB SSD, pantalla AMOLED 15.6', 980000.00, 6, 1, 'Samsung'),

  -- Perifericos (cat 2)
  (2,  'Teclado Mecanico Redragon',             'Switch red, retroiluminado RGB', 45000.00, 30, 2, 'Redragon'),
  (3,  'Mouse Logitech G203',                   'Mouse gamer 8000 DPI, 6 botones', 22000.00, 50, 2, 'Logitech'),
  (13, 'Teclado Mecanico HyperX Alloy Origins', 'Switch HyperX Red, RGB por tecla, chasis aluminio', 85000.00, 15, 2, 'HyperX'),
  (14, 'Mouse Logitech G502 X',                   'Sensor 25600 DPI, 13 botones programables', 65000.00, 20, 2, 'Logitech'),
  (15, 'Mousepad Redragon P036 XXL',            'Superficie 900x400mm, base antideslizante', 18000.00, 40, 2, 'Redragon'),
  (16, 'Webcam Logitech C920 HD Pro',           'Full HD 1080p, autofoco, microfono estereo', 55000.00, 12, 2, 'Logitech'),
  (17, 'Control Xbox Series X/S',               'Inalambrico Bluetooth/USB, compatible PC y Xbox', 42000.00, 18, 2, 'Microsoft'),
  (18, 'Teclado Logitech MX Keys',              'Inalambrico, retroiluminacion inteligente, ideal oficina', 72000.00, 14, 2, 'Logitech'),
  (19, 'Mouse Razer DeathAdder V3',             'Sensor Focus Pro 30K, ultraliviano 59g', 78000.00, 16, 2, 'Razer'),
  (20, 'Microfono HyperX SoloCast',             'USB plug and play, ideal streaming y reuniones', 48000.00, 22, 2, 'HyperX'),

  -- Monitores (cat 3)
  (4,  'Monitor Samsung 24"',                   'Full HD 75Hz IPS, 1ms, FreeSync', 180000.00, 8, 3, 'Samsung'),
  (21, 'Monitor LG UltraGear 27 QHD',           '2560x1440, 165Hz, 1ms, IPS, HDR10', 350000.00, 6, 3, 'LG'),
  (22, 'Monitor AOC 27 Curvo 240Hz',            'Full HD curvo, 240Hz, 1ms, panel VA', 280000.00, 5, 3, 'AOC'),
  (23, 'Monitor Philips 32 4K UHD',             '3840x2160, 60Hz, IPS, HDR400, DisplayPort', 480000.00, 4, 3, 'Philips'),
  (24, 'Monitor ASUS TUF 24 165Hz',             'Full HD, 165Hz, 1ms, FreeSync Premium', 195000.00, 9, 3, 'ASUS'),
  (25, 'Monitor Samsung Odyssey G5 32',         'QHD curvo, 144Hz, 1ms, FreeSync Premium', 420000.00, 5, 3, 'Samsung'),
  (26, 'Monitor BenQ MOBIUZ 27',                'Full HD, 165Hz, HDRi, altavoces integrados', 310000.00, 7, 3, 'BenQ'),
  (27, 'Monitor Dell UltraSharp 27 4K',         'UHD IPS, 99% sRGB, USB-C, ideal diseno', 650000.00, 3, 3, 'Dell'),
  (28, 'Monitor MSI Optix MAG 24',              'Full HD, 144Hz, panel VA, antireflejo', 175000.00, 10, 3, 'MSI'),

  -- Componentes (cat 4)
  (29, 'Placa de Video RTX 4060 8GB',           'NVIDIA GeForce RTX 4060, 8GB GDDR6, DLSS 3', 580000.00, 7, 4, 'ASUS'),
  (30, 'Placa de Video RX 7600 8GB',            'AMD Radeon RX 7600, 8GB GDDR6, FSR 3', 520000.00, 6, 4, 'Sapphire'),
  (31, 'Procesador AMD Ryzen 5 7600X',          '6 nucleos, 12 hilos, socket AM5, hasta 5.3GHz', 320000.00, 10, 4, 'AMD'),
  (32, 'Procesador Intel Core i5-13400F',       '10 nucleos, 16 hilos, socket LGA1700', 290000.00, 11, 4, 'Intel'),
  (33, 'Memoria RAM DDR5 32GB 5600MHz',         'Kit 2x16GB DDR5, CL36, compatible XMP 3.0', 140000.00, 15, 4, 'Kingston'),
  (34, 'Memoria RAM DDR4 16GB 3200MHz',         'Kit 2x8GB DDR4, ideal upgrades y armado', 65000.00, 25, 4, 'Corsair'),
  (35, 'Motherboard ASUS B650M',                'Socket AM5, DDR5, PCIe 4.0, WiFi 6', 210000.00, 8, 4, 'ASUS'),
  (36, 'Fuente Corsair RM750 80 Plus Gold',     '750W modular, certificacion 80 Plus Gold', 185000.00, 12, 4, 'Corsair'),

  -- Almacenamiento (cat 5)
  (37, 'SSD NVMe Samsung 980 Pro 1TB',          'PCIe 4.0, 7000MB/s lectura, M.2 2280', 120000.00, 20, 5, 'Samsung'),
  (38, 'SSD NVMe Kingston NV2 500GB',           'PCIe 4.0, 3500MB/s, ideal notebooks', 55000.00, 35, 5, 'Kingston'),
  (39, 'SSD SATA Crucial MX500 1TB',            'SATA III, 560MB/s, upgrade facil para PC', 85000.00, 18, 5, 'Crucial'),
  (40, 'HDD Seagate Barracuda 2TB',             '7200 RPM, SATA III, almacenamiento masivo', 72000.00, 14, 5, 'Seagate'),
  (41, 'Pendrive SanDisk Ultra 128GB',          'USB 3.2, 150MB/s, compacto y resistente', 12000.00, 60, 5, 'SanDisk'),
  (42, 'Tarjeta microSD Samsung Evo Plus 256GB','Clase 10, U3, ideal camaras y consolas', 28000.00, 45, 5, 'Samsung'),
  (43, 'SSD Externo WD My Passport 2TB',        'USB 3.0, portable, backup y archivos', 95000.00, 16, 5, 'Western Digital'),
  (44, 'SSD NVMe WD Black SN850X 2TB',          'PCIe 4.0, 7300MB/s, gaming y edicion', 220000.00, 9, 5, 'Western Digital'),

  -- Audio (cat 6)
  (45, 'Auricular HyperX Cloud II',             'Surround 7.1 virtual, microfono desmontable', 75000.00, 14, 6, 'HyperX'),
  (46, 'Auricular Logitech G733 Lightspeed',    'Inalambrico RGB, DTS 2.0, 29hs bateria', 110000.00, 8, 6, 'Logitech'),
  (47, 'Parlante JBL Flip 6',                   'Bluetooth 5.1, IP67, 12hs bateria, 30W RMS', 95000.00, 10, 6, 'JBL'),
  (48, 'Auricular Sony WH-1000XM5',             'Cancelacion de ruido, Bluetooth, 30hs bateria', 380000.00, 5, 6, 'Sony'),
  (49, 'Parlante Logitech Z407',                'Sistema 2.1 Bluetooth, control por app', 85000.00, 12, 6, 'Logitech'),
  (50, 'Auricular Razer BlackShark V2 X',       'Liviano, microfono cardioides, multiplataforma', 62000.00, 17, 6, 'Razer'),
  (51, 'Parlante Edifier R1280T',               'Monitores de estudio 2.0, madera, ideal PC', 98000.00, 9, 6, 'Edifier'),
  (52, 'Auricular Apple AirPods Pro 2',         'Cancelacion activa, estuche MagSafe, resistente agua', 290000.00, 11, 6, 'Apple'),

  -- Sillas Gamer (cat 7)
  (53, 'Silla Gamer DXRacer Formula',           'Tapizado PU, reclinable 135, soporte lumbar, hasta 100kg', 280000.00, 5, 7, 'DXRacer'),
  (54, 'Silla Gamer Corsair TC100',             'Tela transpirable, soporte lumbar y cervical, 120kg', 240000.00, 6, 7, 'Corsair'),
  (55, 'Silla Gamer Secretlab Titan Evo',       'Apoyabrazos 4D, reclinable, espuma cold-cure', 450000.00, 4, 7, 'Secretlab'),
  (56, 'Silla Gamer Cougar Armor Elite',        'Reclinable 180, base metalica, hasta 120kg', 195000.00, 8, 7, 'Cougar'),
  (57, 'Silla Ergonómica ErgoHuman',            'Malla respirable, apoyacabeza ajustable, oficina premium', 520000.00, 3, 7, 'ErgoHuman'),
  (58, 'Silla Gamer Redragon C101',             'Apoyabrazos ajustables, reclinable, buena relacion precio', 165000.00, 10, 7, 'Redragon'),
  (59, 'Silla Gamer Noblechairs Epic',          'Cuero sintetico premium, base aluminio, 150kg', 380000.00, 4, 7, 'Noblechairs'),
  (60, 'Silla Gamer Thermaltake Argent',        'Apoyabrazos 2D, reclinable, ideal setups compactos', 175000.00, 7, 7, 'Thermaltake')
ON DUPLICATE KEY UPDATE
  nombre = VALUES(nombre),
  descripcion = VALUES(descripcion),
  precio = VALUES(precio),
  stock = VALUES(stock),
  id_categoria = VALUES(id_categoria),
  proveedor = VALUES(proveedor);

-- Borramos productos basura que quedaron fuera del catalogo oficial.
DELETE FROM detalle_carrito WHERE id_producto > 60;
DELETE FROM detalle_pedido WHERE id_producto > 60;
DELETE FROM resena WHERE id_producto > 60;
DELETE FROM producto WHERE id_producto > 60;

-- Usuarios
-- IMPORTANTE: las contrasenas se guardan HASHEADAS con BCrypt (nunca en texto plano).
-- Los textos que empiezan con "$2a$10$..." son el resultado de aplicarle BCrypt a la
-- contrasena real. Aca dejamos:
--   admin@novatech.com   -> contrasena real: admin123   (rol SUPERADMIN)
--   cliente@novatech.com -> contrasena real: cliente123 (rol CLIENTE)
-- El login del backend compara lo que escribe el usuario contra estos hashes.
INSERT IGNORE INTO usuario (id_usuario, nombre, email, contrasena, rol, fecha_registro) VALUES
  (1, 'Admin Novatech', 'admin@novatech.com', '$2a$10$5/A7KT9NtU4SAMeujmt83.dCGMni9DDkrzH9mY5CAwKsIiRc9d/S2', 'SUPERADMIN', NOW()),
  (2, 'Cliente Demo', 'cliente@novatech.com', '$2a$10$dxxZcXFu1jsKxgfTxpxCe.HkPXphC5Jm4K9p15yA2RQ7aPfYkjgiS', 'CLIENTE', NOW());

-- Por si estos usuarios YA existian de una version anterior (cuando la clave estaba en
-- texto plano), el INSERT IGNORE de arriba no los toca. Para asegurarnos de que el login
-- funcione, forzamos aca la contrasena hasheada y el rol correcto de los dos usuarios demo.
UPDATE usuario SET contrasena = '$2a$10$5/A7KT9NtU4SAMeujmt83.dCGMni9DDkrzH9mY5CAwKsIiRc9d/S2', rol = 'SUPERADMIN'
  WHERE email = 'admin@novatech.com';
UPDATE usuario SET contrasena = '$2a$10$dxxZcXFu1jsKxgfTxpxCe.HkPXphC5Jm4K9p15yA2RQ7aPfYkjgiS', rol = 'CLIENTE'
  WHERE email = 'cliente@novatech.com';

-- Usuario SUPERADMIN dedicado (misma clave demo: admin123)
INSERT IGNORE INTO usuario (id_usuario, nombre, email, contrasena, rol, fecha_registro) VALUES
  (99, 'Super Admin NovaTech', 'superadmin@novatech.com', '$2a$10$5/A7KT9NtU4SAMeujmt83.dCGMni9DDkrzH9mY5CAwKsIiRc9d/S2', 'SUPERADMIN', NOW());
UPDATE usuario SET contrasena = '$2a$10$5/A7KT9NtU4SAMeujmt83.dCGMni9DDkrzH9mY5CAwKsIiRc9d/S2', rol = 'SUPERADMIN'
  WHERE email = 'superadmin@novatech.com';

-- Perfil de cliente (datos extra del usuario cliente)
INSERT IGNORE INTO perfil_cliente (id_cliente, id_usuario, direccion, telefono, historial_crediticio, tipo_cliente) VALUES
  (1, 2, 'Av. Siempre Viva 742', '11-5555-1234', 700, 'CONSUMIDOR_FINAL');

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
