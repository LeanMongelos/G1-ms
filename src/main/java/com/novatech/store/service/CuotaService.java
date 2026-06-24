package com.novatech.store.service;

import com.novatech.store.entity.Cuota;
import com.novatech.store.entity.Pago;
import com.novatech.store.entity.Pedido;
import com.novatech.store.entity.PlanCuotas;
import com.novatech.store.exception.ReglaNegocioException;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.CuotaRepository;
import com.novatech.store.repository.PlanCuotasRepository;
import com.novatech.store.util.FechaCobranzaUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio `CuotaService`: reglas de negocio, transacciones y orquestación de Cuota. Los controllers delegan aquí; no accede HTTP directamente.
 */
@Service
public class CuotaService {

    private final CuotaRepository repository;
    private final PlanCuotasRepository planRepository;
    private final PagoService pagoService;

    public CuotaService(CuotaRepository repository,
                        PlanCuotasRepository planRepository,
                        PagoService pagoService) {
        this.repository = repository;
        this.planRepository = planRepository;
        this.pagoService = pagoService;
    }

    public List<Cuota> listar() {
        return repository.findAll();
    }

    public Cuota obtener(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuota no encontrada: " + id));
    }

    public List<Cuota> listarPorPlan(Integer idPlan) {
        return repository.findByPlanIdPlan(idPlan);
    }

    public List<Cuota> listarVencidas() {
        actualizarVencidas();
        return repository.findByEstado("VENCIDA");
    }

    public List<Cuota> listarPorVencer(int dias) {
        actualizarVencidas();
        LocalDate limite = LocalDate.now().plusDays(dias);
        return repository.findAll().stream()
                .filter(c -> "PENDIENTE".equals(c.getEstado()))
                .filter(c -> !c.getFechaVencimiento().isBefore(LocalDate.now()))
                .filter(c -> !c.getFechaVencimiento().isAfter(limite))
                .toList();
    }

    @Transactional
    public void generarCuotasParaPlan(PlanCuotas plan) {
        if (plan.getCantidadCuotas() == null || plan.getCantidadCuotas() < 1) {
            throw new ReglaNegocioException("El plan debe tener al menos 1 cuota.");
        }
        if (plan.getPedido() == null || plan.getPedido().getTotal() == null) {
            throw new ReglaNegocioException("El plan debe estar vinculado a un pedido con total.");
        }

        BigDecimal total = plan.getPedido().getTotal();
        if (plan.getInteres() != null && plan.getInteres().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal factor = BigDecimal.ONE.add(
                    plan.getInteres().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
            total = total.multiply(factor);
        }

        BigDecimal montoCuota = total.divide(
                BigDecimal.valueOf(plan.getCantidadCuotas()), 2, RoundingMode.HALF_UP);
        BigDecimal ajuste = total.subtract(montoCuota.multiply(BigDecimal.valueOf(plan.getCantidadCuotas())));
        LocalDate referencia = LocalDate.now();

        for (int i = 1; i <= plan.getCantidadCuotas(); i++) {
            Cuota cuota = new Cuota();
            cuota.setPlan(plan);
            cuota.setNumeroCuota(i);
            BigDecimal monto = montoCuota;
            if (i == plan.getCantidadCuotas()) {
                monto = monto.add(ajuste);
            }
            cuota.setMonto(monto);
            cuota.setFechaVencimiento(FechaCobranzaUtil.vencimientoCuota(referencia, i));
            cuota.setEstado("PENDIENTE");
            cuota.setAvisoVencimientoEnviado(false);
            repository.save(cuota);
        }
    }

    @Transactional
    public Cuota marcarPagada(Integer id) {
        Cuota cuota = obtener(id);
        if ("PAGADA".equals(cuota.getEstado())) {
            throw new ReglaNegocioException("La cuota ya está pagada.");
        }
        PlanCuotas plan = cuota.getPlan();
        if (plan == null || plan.getPedido() == null || plan.getPedido().getIdPedido() == null) {
            throw new ReglaNegocioException("La cuota no está vinculada a un pedido.");
        }

        Pago pago = new Pago();
        Pedido pedidoRef = new Pedido();
        pedidoRef.setIdPedido(plan.getPedido().getIdPedido());
        pago.setPedido(pedidoRef);
        pago.setMonto(cuota.getMonto());
        pago.setMetodo("TRANSFERENCIA");
        pago.setReferencia("CUOTA-" + cuota.getNumeroCuota() + "-" + cuota.getIdCuota());
        pago.setEstado("APROBADO");
        pago.setFechaPago(LocalDateTime.now());
        pagoService.crear(pago);

        cuota.setEstado("PAGADA");
        cuota.setFechaPago(LocalDateTime.now());
        Cuota guardada = repository.save(cuota);
        verificarPlanFinalizado(plan.getIdPlan());
        return guardada;
    }

    private void verificarPlanFinalizado(Integer idPlan) {
        List<Cuota> cuotas = repository.findByPlanIdPlan(idPlan);
        boolean todasPagadas = cuotas.stream().allMatch(c -> "PAGADA".equals(c.getEstado()));
        if (todasPagadas) {
            planRepository.findById(idPlan).ifPresent(plan -> {
                plan.setEstado("FINALIZADO");
                planRepository.save(plan);
            });
        }
    }

    @Transactional
    public void actualizarVencidas() {
        List<Cuota> pendientes = repository.findByEstadoAndFechaVencimientoBefore("PENDIENTE", LocalDate.now());
        for (Cuota cuota : pendientes) {
            cuota.setEstado("VENCIDA");
            repository.save(cuota);
        }
    }

    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cuota no encontrada: " + id);
        }
        repository.deleteById(id);
    }
}
