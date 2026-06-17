-- ============================================================
--  Datos de ejemplo para probar el backend.
--  Este archivo se ejecuta solo cada vez que arranca la app.
--  Como la base H2 vive en memoria, se vacia al apagar la app,
--  por eso un INSERT simple alcanza (no se duplican datos).
-- ============================================================

-- Categorias de ejemplo
INSERT INTO Categoria (id_categoria, nombre, descripcion) VALUES
  (1, 'Notebooks', 'Computadoras portatiles'),
  (2, 'Perifericos', 'Teclados, mouse y accesorios'),
  (3, 'Monitores', 'Pantallas y monitores');

-- Productos de ejemplo (cada uno apunta a una categoria por su id)
INSERT INTO Producto (id_producto, nombre, descripcion, precio, stock, id_categoria, proveedor) VALUES
  (1, 'Notebook Lenovo IdeaPad', 'Ryzen 5, 16GB RAM, 512GB SSD', 750000.00, 12, 1, 'Lenovo'),
  (2, 'Teclado Mecanico Redragon', 'Switch red, retroiluminado', 45000.00, 30, 2, 'Redragon'),
  (3, 'Mouse Logitech G203', 'Mouse gamer 8000 DPI', 22000.00, 50, 2, 'Logitech'),
  (4, 'Monitor Samsung 24"', 'Full HD 75Hz IPS', 180000.00, 8, 3, 'Samsung');
