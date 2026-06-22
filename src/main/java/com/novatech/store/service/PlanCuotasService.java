package com.novatech.store.service;

import com.novatech.store.entity.PerfilCliente;
import com.novatech.store.entity.Pedido;
import com.novatech.store.entity.PlanCuotas;
import com.novatech.store.exception.ReglaNegocioException;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.PedidoRepository;
import com.novatech.store.repository.PerfilClienteRepository;
import com.novatech.store.repository.PlanCuotasRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Logica de negocio para los planes de cuotas.
@Service
public class PlanCuotasService {

    private final PlanCuotasRepository repository;
    // Necesitamos pedidos y perfiles de cliente para validar que el plan corresponda.
    private final PedidoRepository pedidoRepository;
    private final PerfilClienteRepository perfilClienteRepository;
    private final CuotaService cuotaService;

    public PlanCuotasService(PlanCuotasRepository repository,
                             PedidoRepository pedidoRepository,
                             PerfilClienteRepository perfilClienteRepository,
                             CuotaService cuotaService) {
        this.repository = repository;
        this.pedidoRepository = pedidoRepository;
        this.perfilClienteRepository = perfilClienteRepository;
        this.cuotaService = cuotaService;
    }

    // Trae todos los planes de cuotas.
    public List<PlanCuotas> listar() {
        return repository.findAll();
    }

    // Trae un plan por su id. Si no existe, lanza error 404.
    public PlanCuotas obtener(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan de cuotas no encontrado: " + id));
    }

    // Crea un plan nuevo. Si no viene estado, lo dejamos "ACTIVO".
    @Transactional
    public PlanCuotas crear(PlanCuotas plan) {
        plan.setIdPlan(null);
        if (plan.getEstado() == null || plan.getEstado().isBlank()) {
            plan.setEstado("ACTIVO");
        }

        // 1) El plan tiene que indicar a que pedido y a que perfil de cliente pertenece.
        if (plan.getPedido() == null || plan.getPedido().getIdPedido() == null) {
            throw new ReglaNegocioException("El plan debe indicar a que pedido corresponde (id_pedido).");
        }
        if (plan.getCliente() == null || plan.getCliente().getIdCliente() == null) {
            throw new ReglaNegocioException("El plan debe indicar a que cliente corresponde (id_cliente).");
        }

        // 2) Cargamos el pedido y el perfil reales desde la base.
        Pedido pedido = pedidoRepository.findById(plan.getPedido().getIdPedido())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pedido no encontrado: " + plan.getPedido().getIdPedido()));
        PerfilCliente perfil = perfilClienteRepository.findById(plan.getCliente().getIdCliente())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Perfil de cliente no encontrado: " + plan.getCliente().getIdCliente()));

        // 3) Validamos coherencia: el usuario dueño del pedido tiene que ser el mismo
        //    que el usuario del perfil de cliente del plan. Si no, no le corresponde.
        Integer usuarioPedido = pedido.getUsuario() == null ? null : pedido.getUsuario().getIdUsuario();
        Integer usuarioPerfil = perfil.getUsuario() == null ? null : perfil.getUsuario().getIdUsuario();
        if (usuarioPedido == null || usuarioPerfil == null
                || !usuarioPedido.equals(usuarioPerfil)) {
            throw new ReglaNegocioException(
                    "El plan de cuotas no corresponde: el pedido es de otro usuario distinto al del cliente.");
        }

        // Usamos las entidades reales de la base.
        plan.setPedido(pedido);
        plan.setCliente(perfil);

        if (plan.getCantidadCuotas() == null || plan.getCantidadCuotas() < 1
                || plan.getCantidadCuotas() > 24) {
            throw new ReglaNegocioException("Cantidad de cuotas inválida (1-24).");
        }
        if (plan.getInteres() != null && (plan.getInteres().compareTo(java.math.BigDecimal.ZERO) < 0
                || plan.getInteres().compareTo(new java.math.BigDecimal("100")) > 0)) {
            throw new ReglaNegocioException("Interés inválido (0-100%).");
        }

        PlanCuotas guardado = repository.save(plan);
        cuotaService.generarCuotasParaPlan(guardado);
        return guardado;
    }

    // Actualiza un plan existente.
    public PlanCuotas actualizar(Integer id, PlanCuotas datos) {
        PlanCuotas plan = obtener(id);
        plan.setCliente(datos.getCliente());
        plan.setPedido(datos.getPedido());
        plan.setCantidadCuotas(datos.getCantidadCuotas());
        plan.setInteres(datos.getInteres());
        plan.setEstado(datos.getEstado());
        return repository.save(plan);
    }

    // Borra un plan por su id.
    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Plan de cuotas no encontrado: " + id);
        }
        repository.deleteById(id);
    }
}
