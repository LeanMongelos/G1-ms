package com.novatech.store.service;

import com.novatech.store.dto.ListaPrecioDetalleRequest;
import com.novatech.store.dto.ListaPrecioUpdateRequest;
import com.novatech.store.dto.PrecioResueltoDto;
import com.novatech.store.entity.ListaPrecio;
import com.novatech.store.entity.ListaPrecioDetalle;
import com.novatech.store.entity.Producto;
import com.novatech.store.exception.ReglaNegocioException;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.ListaPrecioDetalleRepository;
import com.novatech.store.repository.ListaPrecioRepository;
import com.novatech.store.repository.ProductoRepository;
import com.novatech.store.util.ListaPrecioCodigo;
import com.novatech.store.util.PrecioListaUtil;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListaPrecioService {

    private final ListaPrecioRepository listaRepository;
    private final ListaPrecioDetalleRepository detalleRepository;
    private final ProductoRepository productoRepository;

    public ListaPrecioService(ListaPrecioRepository listaRepository,
                              ListaPrecioDetalleRepository detalleRepository,
                              ProductoRepository productoRepository) {
        this.listaRepository = listaRepository;
        this.detalleRepository = detalleRepository;
        this.productoRepository = productoRepository;
    }

    public List<ListaPrecio> listar() {
        return listaRepository.findAllByOrderByCodigoAsc();
    }

    public ListaPrecio obtener(Integer id) {
        return listaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lista de precios no encontrada: " + id));
    }

    public ListaPrecio porCodigo(String codigo) {
        return listaRepository.findByCodigoIgnoreCase(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Lista de precios no encontrada: " + codigo));
    }

    @Transactional
    public ListaPrecio actualizar(Integer id, ListaPrecioUpdateRequest req) {
        ListaPrecio lista = obtener(id);
        if (req.getNombre() != null && !req.getNombre().isBlank()) {
            lista.setNombre(req.getNombre().trim());
        }
        if (req.getDescripcion() != null) {
            lista.setDescripcion(req.getDescripcion().trim());
        }
        if (req.getActivo() != null) {
            lista.setActivo(req.getActivo());
        }
        if (req.getDescuentoGlobal() != null) {
            BigDecimal desc = PrecioListaUtil.normalizarDescuento(req.getDescuentoGlobal());
            validarDescuentoGlobalJerarquia(lista.getCodigo(), desc);
            lista.setDescuentoGlobal(desc);
        }
        return listaRepository.save(lista);
    }

    public List<ListaPrecioDetalle> listarDetalles(Integer idLista) {
        return detalleRepository.findByListaPrecioIdListaPrecioOrderByProductoNombreAsc(idLista);
    }

    @Transactional
    public ListaPrecioDetalle guardarDetalle(Integer idLista, ListaPrecioDetalleRequest req) {
        if (req.getIdProducto() == null) {
            throw new ReglaNegocioException("Debe indicar el producto.");
        }
        if (req.getDescuentoPorcentaje() == null && req.getPrecioFijo() == null) {
            throw new ReglaNegocioException("Indicá descuento unitario % o precio fijo.");
        }
        if (req.getDescuentoPorcentaje() != null && req.getPrecioFijo() != null) {
            throw new ReglaNegocioException("Usá descuento unitario o precio fijo, no ambos a la vez.");
        }

        ListaPrecio lista = obtener(idLista);
        Producto producto = productoRepository.findById(req.getIdProducto())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + req.getIdProducto()));

        BigDecimal descUnit = req.getDescuentoPorcentaje() != null
                ? PrecioListaUtil.normalizarDescuento(req.getDescuentoPorcentaje()) : null;
        BigDecimal precioFijo = req.getPrecioFijo();

        if (precioFijo != null && precioFijo.compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaNegocioException("El precio fijo no puede ser negativo.");
        }

        BigDecimal precioEfectivo = PrecioListaUtil.calcularPrecioEfectivo(
                producto, lista.getDescuentoGlobal(), descUnit, precioFijo);
        validarPrecioEfectivoJerarquia(producto, lista.getCodigo(), precioEfectivo, null);

        ListaPrecioDetalle det = detalleRepository
                .findByListaPrecioIdListaPrecioAndProductoIdProducto(idLista, req.getIdProducto())
                .orElseGet(ListaPrecioDetalle::new);
        det.setListaPrecio(lista);
        det.setProducto(producto);
        det.setDescuentoPorcentaje(descUnit);
        det.setPrecioFijo(precioFijo);
        return detalleRepository.save(det);
    }

    @Transactional
    public void eliminarDetalle(Integer idLista, Integer idDetalle) {
        ListaPrecioDetalle det = detalleRepository.findById(idDetalle)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle no encontrado: " + idDetalle));
        if (!det.getListaPrecio().getIdListaPrecio().equals(idLista)) {
            throw new ReglaNegocioException("El detalle no pertenece a esta lista.");
        }
        detalleRepository.delete(det);
    }

    public PrecioResueltoDto resolverPrecio(Integer idProducto, String codigoLista) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + idProducto));
        return resolverPrecio(producto, codigoLista);
    }

    public PrecioResueltoDto resolverPrecio(Producto producto, String codigoLista) {
        String codigoRaw = codigoLista != null ? codigoLista.trim().toUpperCase() : ListaPrecioCodigo.ECOMMERCE;
        final String codigo = ListaPrecioCodigo.esValido(codigoRaw) ? codigoRaw : ListaPrecioCodigo.ECOMMERCE;

        ListaPrecio lista = listaRepository.findByCodigoIgnoreCase(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Lista de precios no configurada: " + codigo));

        if (Boolean.FALSE.equals(lista.getActivo())) {
            throw new ReglaNegocioException("La lista " + lista.getNombre() + " está inactiva.");
        }

        ListaPrecioDetalle det = detalleRepository
                .findByListaPrecioIdListaPrecioAndProductoIdProducto(
                        lista.getIdListaPrecio(), producto.getIdProducto())
                .orElse(null);

        BigDecimal descUnit = det != null ? det.getDescuentoPorcentaje() : null;
        BigDecimal precioFijo = det != null ? det.getPrecioFijo() : null;
        BigDecimal descGlobal = PrecioListaUtil.normalizarDescuento(lista.getDescuentoGlobal());
        BigDecimal precioEfectivo = PrecioListaUtil.calcularPrecioEfectivo(
                producto, descGlobal, descUnit, precioFijo);

        PrecioResueltoDto dto = new PrecioResueltoDto();
        dto.setIdProducto(producto.getIdProducto());
        dto.setCodigoLista(lista.getCodigo());
        dto.setNombreLista(lista.getNombre());
        dto.setPrecioBase(PrecioListaUtil.precioBase(producto));
        dto.setDescuentoGlobal(descGlobal);
        dto.setDescuentoUnitario(descUnit);
        dto.setPrecioFijo(precioFijo);
        dto.setPrecioEfectivo(precioEfectivo);
        dto.setDescuentoEfectivoPorcentaje(PrecioListaUtil.descuentoEfectivoPorcentaje(producto, precioEfectivo));
        dto.setUsaPrecioFijo(precioFijo != null);
        dto.setUsaDescuentoUnitario(descUnit != null);
        return dto;
    }

    /**
     * Resuelve lista según canal de venta y tipo de cliente CRM.
     */
    public String resolverCodigoLista(String canalOrigen, String tipoCliente) {
        if (tipoCliente != null) {
            String t = tipoCliente.trim().toUpperCase();
            if ("MAYORISTA".equals(t)) {
                return ListaPrecioCodigo.MAYORISTA;
            }
            if ("EMPRESA".equals(t) || "CORPORATIVO".equals(t) || "INSTITUCION_EDUCATIVA".equals(t)) {
                return ListaPrecioCodigo.B2B;
            }
        }
        if (canalOrigen != null) {
            String c = canalOrigen.trim().toUpperCase();
            if ("POS".equals(c)) {
                return ListaPrecioCodigo.LOCAL;
            }
            if ("WEB".equals(c)) {
                return ListaPrecioCodigo.ECOMMERCE;
            }
            if ("ADMIN".equals(c) || "WHATSAPP".equals(c) || "EMAIL".equals(c)
                    || "INSTAGRAM".equals(c) || "FACEBOOK".equals(c)) {
                return tipoCliente != null ? resolverCodigoLista(null, tipoCliente) : ListaPrecioCodigo.B2B;
            }
        }
        return ListaPrecioCodigo.ECOMMERCE;
    }

    public BigDecimal precioVenta(Producto producto, String canalOrigen, String tipoCliente) {
        String codigo = resolverCodigoLista(canalOrigen, tipoCliente);
        return resolverPrecio(producto, codigo).getPrecioEfectivo();
    }

    /**
     * Descuento global: una lista con mejor precio (mayorista) debe tener descuento global
     * mayor o igual que las listas de retail (e-commerce / local).
     */
    void validarDescuentoGlobalJerarquia(String codigoLista, BigDecimal nuevoDescuentoGlobal) {
        int rank = ListaPrecioCodigo.jerarquia(codigoLista);
        if (rank >= 99) {
            return;
        }
        for (ListaPrecio otra : listaRepository.findAll()) {
            if (otra.getCodigo().equalsIgnoreCase(codigoLista)) {
                continue;
            }
            int rankOtra = ListaPrecioCodigo.jerarquia(otra.getCodigo());
            if (rankOtra >= 99) {
                continue;
            }
            BigDecimal descOtra = PrecioListaUtil.normalizarDescuento(otra.getDescuentoGlobal());
            if (rank < rankOtra && nuevoDescuentoGlobal.compareTo(descOtra) < 0) {
                throw new ReglaNegocioException(
                        "El descuento global de " + ListaPrecioCodigo.etiqueta(codigoLista)
                                + " (" + nuevoDescuentoGlobal + "%) no puede ser menor que el de "
                                + ListaPrecioCodigo.etiqueta(otra.getCodigo())
                                + " (" + descOtra + "%). "
                                + "Mayoristas/B2B deben tener igual o mayor descuento que e-commerce/local.");
            }
            if (rank > rankOtra && nuevoDescuentoGlobal.compareTo(descOtra) > 0) {
                throw new ReglaNegocioException(
                        "El descuento global de " + ListaPrecioCodigo.etiqueta(codigoLista)
                                + " (" + nuevoDescuentoGlobal + "%) no puede superar al de "
                                + ListaPrecioCodigo.etiqueta(otra.getCodigo())
                                + " (" + descOtra + "%). "
                                + "E-commerce y local no pueden tener más descuento que mayoristas/B2B.");
            }
        }
    }

    /**
     * Precio efectivo: mayorista ≤ B2B ≤ e-commerce ≤ local (mismo producto).
     *
     * @param excluirDetalleId detalle en edición (nullable)
     */
    void validarPrecioEfectivoJerarquia(Producto producto, String codigoLista,
                                        BigDecimal precioEfectivo, Integer excluirDetalleId) {
        if (precioEfectivo.compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaNegocioException("El precio efectivo no puede ser negativo.");
        }
        int rank = ListaPrecioCodigo.jerarquia(codigoLista);
        if (rank >= 99) {
            return;
        }

        for (ListaPrecio otra : listaRepository.findAll()) {
            if (otra.getCodigo().equalsIgnoreCase(codigoLista)) {
                continue;
            }
            int rankOtra = ListaPrecioCodigo.jerarquia(otra.getCodigo());
            if (rankOtra >= 99) {
                continue;
            }

            BigDecimal precioOtra = calcularPrecioEnLista(producto, otra, excluirDetalleId);

            if (rank < rankOtra && precioEfectivo.compareTo(precioOtra) > 0) {
                throw new ReglaNegocioException(
                        "El precio en " + ListaPrecioCodigo.etiqueta(codigoLista)
                                + " ($" + precioEfectivo + ") no puede ser mayor que en "
                                + ListaPrecioCodigo.etiqueta(otra.getCodigo())
                                + " ($" + precioOtra + ") para \"" + producto.getNombre() + "\". "
                                + "Mayoristas/B2B deben tener el mejor precio.");
            }
            if (rank > rankOtra && precioEfectivo.compareTo(precioOtra) < 0) {
                throw new ReglaNegocioException(
                        "El precio en " + ListaPrecioCodigo.etiqueta(codigoLista)
                                + " ($" + precioEfectivo + ") no puede ser menor que en "
                                + ListaPrecioCodigo.etiqueta(otra.getCodigo())
                                + " ($" + precioOtra + ") para \"" + producto.getNombre() + "\". "
                                + "E-commerce y local deben ser iguales o más caros que mayoristas/B2B.");
            }
        }
    }

    private BigDecimal calcularPrecioEnLista(Producto producto, ListaPrecio lista, Integer excluirDetalleId) {
        ListaPrecioDetalle det = detalleRepository
                .findByListaPrecioIdListaPrecioAndProductoIdProducto(
                        lista.getIdListaPrecio(), producto.getIdProducto())
                .orElse(null);
        if (det != null && excluirDetalleId != null && excluirDetalleId.equals(det.getIdDetalle())) {
            det = null;
        }
        BigDecimal descUnit = det != null ? det.getDescuentoPorcentaje() : null;
        BigDecimal precioFijo = det != null ? det.getPrecioFijo() : null;
        return PrecioListaUtil.calcularPrecioEfectivo(
                producto, lista.getDescuentoGlobal(), descUnit, precioFijo);
    }
}
