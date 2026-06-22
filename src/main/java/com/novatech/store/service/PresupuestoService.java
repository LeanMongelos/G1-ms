package com.novatech.store.service;

import com.novatech.store.dto.LineaComprobanteDto;
import com.novatech.store.dto.PresupuestoRequest;
import com.novatech.store.entity.DetallePresupuesto;
import com.novatech.store.entity.Presupuesto;
import com.novatech.store.entity.Producto;
import com.novatech.store.exception.ReglaNegocioException;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.PerfilClienteRepository;
import com.novatech.store.repository.PresupuestoRepository;
import com.novatech.store.repository.ProductoRepository;
import com.novatech.store.util.ComprobanteCalculoUtil;
import com.novatech.store.util.ComprobanteCalculoUtil.TotalesComprobante;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PresupuestoService {

    private final PresupuestoRepository repository;
    private final PerfilClienteRepository perfilRepository;
    private final ProductoRepository productoRepository;

    public PresupuestoService(PresupuestoRepository repository,
                              PerfilClienteRepository perfilRepository,
                              ProductoRepository productoRepository) {
        this.repository = repository;
        this.perfilRepository = perfilRepository;
        this.productoRepository = productoRepository;
    }

    public List<Presupuesto> listar() {
        return repository.findAll().stream()
                .peek(this::actualizarVencimientoSiCorresponde)
                .toList();
    }

    public Presupuesto obtener(Integer id) {
        Presupuesto p = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto no encontrado: " + id));
        actualizarVencimientoSiCorresponde(p);
        return p;
    }

    @Transactional
    public Presupuesto crear(PresupuestoRequest request) {
        validarRequest(request);
        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setNumeroPresupuesto(generarNumero());
        presupuesto.setFecha(LocalDateTime.now());
        presupuesto.setEstado("BORRADOR");
        presupuesto.setValidezHasta(request.getValidezHasta() != null
                ? request.getValidezHasta()
                : LocalDate.now().plusDays(15));
        presupuesto.setNotas(request.getNotas());
        presupuesto.setCliente(perfilRepository.findById(request.getIdCliente())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + request.getIdCliente())));
        aplicarLineas(presupuesto, request.getLineas());
        return repository.save(presupuesto);
    }

    @Transactional
    public Presupuesto actualizar(Integer id, PresupuestoRequest request) {
        Presupuesto presupuesto = obtener(id);
        if ("FACTURADO".equalsIgnoreCase(presupuesto.getEstado())) {
            throw new ReglaNegocioException("No se puede editar un presupuesto ya facturado.");
        }
        validarRequest(request);
        if (request.getIdCliente() != null) {
            presupuesto.setCliente(perfilRepository.findById(request.getIdCliente())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + request.getIdCliente())));
        }
        if (request.getValidezHasta() != null) {
            presupuesto.setValidezHasta(request.getValidezHasta());
        }
        presupuesto.setNotas(request.getNotas());
        aplicarLineas(presupuesto, request.getLineas());
        return repository.save(presupuesto);
    }

    @Transactional
    public Presupuesto cambiarEstado(Integer id, String nuevoEstado) {
        Presupuesto presupuesto = obtener(id);
        String estado = nuevoEstado != null ? nuevoEstado.toUpperCase() : "";
        switch (estado) {
            case "ENVIADO" -> {
                if (!"BORRADOR".equalsIgnoreCase(presupuesto.getEstado())
                        && !"ENVIADO".equalsIgnoreCase(presupuesto.getEstado())) {
                    throw new ReglaNegocioException("Solo se puede enviar un presupuesto en borrador.");
                }
                presupuesto.setEstado("ENVIADO");
            }
            case "APROBADO" -> {
                if ("FACTURADO".equalsIgnoreCase(presupuesto.getEstado())) {
                    throw new ReglaNegocioException("El presupuesto ya fue facturado.");
                }
                presupuesto.setEstado("APROBADO");
            }
            case "VENCIDO" -> presupuesto.setEstado("VENCIDO");
            default -> throw new ReglaNegocioException("Estado no válido: " + nuevoEstado);
        }
        return repository.save(presupuesto);
    }

    public void eliminar(Integer id) {
        Presupuesto presupuesto = obtener(id);
        if ("FACTURADO".equalsIgnoreCase(presupuesto.getEstado())) {
            throw new ReglaNegocioException("No se puede eliminar un presupuesto facturado.");
        }
        repository.delete(presupuesto);
    }

    private void validarRequest(PresupuestoRequest request) {
        if (request.getIdCliente() == null) {
            throw new ReglaNegocioException("Debe indicar el cliente.");
        }
        if (request.getLineas() == null || request.getLineas().isEmpty()) {
            throw new ReglaNegocioException("Agregá al menos una línea al presupuesto.");
        }
    }

    private void aplicarLineas(Presupuesto presupuesto, List<LineaComprobanteDto> lineasDto) {
        presupuesto.getLineas().clear();
        BigDecimal subtotalAcum = BigDecimal.ZERO;
        List<DetallePresupuesto> lineas = new ArrayList<>();

        for (LineaComprobanteDto dto : lineasDto) {
            if (dto.getIdProducto() == null || dto.getCantidad() == null || dto.getCantidad() < 1) {
                throw new ReglaNegocioException("Cada línea debe tener producto y cantidad válida.");
            }
            Producto producto = productoRepository.findById(dto.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + dto.getIdProducto()));
            BigDecimal precio = dto.getPrecioUnitario() != null ? dto.getPrecioUnitario() : producto.getPrecio();
            BigDecimal descuento = dto.getDescuentoPorcentaje() != null ? dto.getDescuentoPorcentaje() : BigDecimal.ZERO;
            BigDecimal subtotalLinea = ComprobanteCalculoUtil.calcularSubtotalLinea(dto.getCantidad(), precio, descuento);

            DetallePresupuesto det = new DetallePresupuesto();
            det.setPresupuesto(presupuesto);
            det.setProducto(producto);
            det.setCantidad(dto.getCantidad());
            det.setPrecioUnitario(precio);
            det.setDescuentoPorcentaje(descuento);
            det.setSubtotal(subtotalLinea);
            lineas.add(det);
            subtotalAcum = subtotalAcum.add(subtotalLinea);
        }

        presupuesto.getLineas().addAll(lineas);
        String condicionIva = presupuesto.getCliente() != null ? presupuesto.getCliente().getCondicionIva() : null;
        BigDecimal tasa = ComprobanteCalculoUtil.tasaIvaParaCondicion(condicionIva);
        TotalesComprobante totales = ComprobanteCalculoUtil.calcularTotales(subtotalAcum, tasa);
        presupuesto.setSubtotal(totales.subtotal());
        presupuesto.setIva(totales.iva());
        presupuesto.setTotal(totales.total());
    }

    private void actualizarVencimientoSiCorresponde(Presupuesto presupuesto) {
        if (presupuesto.getValidezHasta() != null
                && presupuesto.getValidezHasta().isBefore(LocalDate.now())
                && !"FACTURADO".equalsIgnoreCase(presupuesto.getEstado())
                && !"VENCIDO".equalsIgnoreCase(presupuesto.getEstado())) {
            presupuesto.setEstado("VENCIDO");
            repository.save(presupuesto);
        }
    }

    private String generarNumero() {
        long count = repository.count() + 1;
        return "PRES-" + Year.now().getValue() + "-" + String.format("%06d", count);
    }
}
