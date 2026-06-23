package com.novatech.store.service;

import com.novatech.store.entity.DetalleOrdenCompra;
import com.novatech.store.entity.OrdenCompra;
import com.novatech.store.entity.Producto;
import com.novatech.store.exception.ReglaNegocioException;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.OrdenCompraRepository;
import com.novatech.store.repository.ProductoRepository;
import com.novatech.store.util.StockInventarioUtil;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdenCompraService {

    private final OrdenCompraRepository repository;
    private final ProductoRepository productoRepository;

    public OrdenCompraService(OrdenCompraRepository repository, ProductoRepository productoRepository) {
        this.repository = repository;
        this.productoRepository = productoRepository;
    }

    public List<OrdenCompra> listar() {
        return repository.findAll();
    }

    public OrdenCompra obtener(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de compra no encontrada: " + id));
    }

    public List<Producto> listarProductosStockBajo() {
        return productoRepository.findAll().stream()
                .filter(StockInventarioUtil::esStockBajo)
                .toList();
    }

    @Transactional
    public List<OrdenCompra> generarDesdeStockBajo() {
        List<Producto> bajos = listarProductosStockBajo();
        if (bajos.isEmpty()) {
            throw new ReglaNegocioException("No hay productos con stock bajo para reponer.");
        }
        return generarOrdenes(bajos);
    }

    @Transactional
    public List<OrdenCompra> generarParaProductos(List<Integer> productoIds) {
        if (productoIds == null || productoIds.isEmpty()) {
            throw new ReglaNegocioException("Indicá al menos un producto.");
        }
        List<Producto> productos = new ArrayList<>();
        for (Integer id : productoIds) {
            productos.add(productoRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id)));
        }
        return generarOrdenes(productos);
    }

    @Transactional
    public OrdenCompra enviar(Integer id) {
        OrdenCompra orden = obtener(id);
        if (!"BORRADOR".equals(orden.getEstado())) {
            throw new ReglaNegocioException("Solo se pueden enviar órdenes en borrador.");
        }
        orden.setEstado("ENVIADA");
        return repository.save(orden);
    }

    @Transactional
    public OrdenCompra recibir(Integer id) {
        OrdenCompra orden = obtener(id);
        if (!"ENVIADA".equals(orden.getEstado()) && !"BORRADOR".equals(orden.getEstado())) {
            throw new ReglaNegocioException("La orden no puede recibirse en estado " + orden.getEstado());
        }
        for (DetalleOrdenCompra det : orden.getDetalles()) {
            Producto p = det.getProducto();
            if (p != null && p.getIdProducto() != null) {
                Producto actual = productoRepository.findById(p.getIdProducto()).orElse(p);
                int nuevoStock = (actual.getStock() != null ? actual.getStock() : 0) + det.getCantidad();
                actual.setStock(nuevoStock);
                productoRepository.save(actual);
            }
        }
        orden.setEstado("RECIBIDA");
        return repository.save(orden);
    }

    public void eliminar(Integer id) {
        OrdenCompra orden = obtener(id);
        if ("RECIBIDA".equals(orden.getEstado())) {
            throw new ReglaNegocioException("No se puede eliminar una orden ya recibida.");
        }
        repository.deleteById(id);
    }

    private List<OrdenCompra> generarOrdenes(List<Producto> productos) {
        Map<String, List<Producto>> porProveedor = new LinkedHashMap<>();
        for (Producto p : productos) {
            String prov = p.getProveedor() != null && !p.getProveedor().isBlank()
                    ? p.getProveedor().trim() : "Sin proveedor";
            porProveedor.computeIfAbsent(prov, k -> new ArrayList<>()).add(p);
        }

        List<OrdenCompra> creadas = new ArrayList<>();
        for (Map.Entry<String, List<Producto>> entry : porProveedor.entrySet()) {
            creadas.add(crearOrden(entry.getKey(), entry.getValue()));
        }
        return creadas;
    }

    private OrdenCompra crearOrden(String proveedor, List<Producto> productos) {
        OrdenCompra orden = new OrdenCompra();
        orden.setFecha(LocalDateTime.now());
        orden.setEstado("BORRADOR");
        orden.setProveedor(proveedor);
        orden.setNotas("Generada automáticamente por stock bajo — NovaTech ERP");

        BigDecimal total = BigDecimal.ZERO;
        List<DetalleOrdenCompra> detalles = new ArrayList<>();

        for (Producto p : productos) {
            int min = StockInventarioUtil.stockMinimoEfectivo(p);
            int stock = StockInventarioUtil.stockActual(p);
            int cantidad = Math.max(min * 2 - stock, min);

            BigDecimal costo = p.getPrecioLista() != null ? p.getPrecioLista()
                    : (p.getPrecio() != null ? p.getPrecio().multiply(new BigDecimal("0.65")) : BigDecimal.ZERO);
            BigDecimal subtotal = costo.multiply(BigDecimal.valueOf(cantidad));

            DetalleOrdenCompra det = new DetalleOrdenCompra();
            det.setOrden(orden);
            det.setProducto(p);
            det.setCantidad(cantidad);
            det.setPrecioUnitario(costo);
            det.setSubtotal(subtotal);
            detalles.add(det);
            total = total.add(subtotal);
        }

        orden.setDetalles(detalles);
        orden.setTotal(total);
        return repository.save(orden);
    }
}
