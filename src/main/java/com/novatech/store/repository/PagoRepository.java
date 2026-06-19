package com.novatech.store.repository;

import com.novatech.store.entity.Pago;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// Repositorio para los pagos.
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    // Trae todos los pagos de un pedido (sirve para sumar lo ya pagado).
    List<Pago> findByPedidoIdPedido(Integer idPedido);
}
