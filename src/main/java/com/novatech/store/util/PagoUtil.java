package com.novatech.store.util;

import com.novatech.store.entity.Pago;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public final class PagoUtil {

    private PagoUtil() {
    }

    public static boolean aprobado(Pago pago) {
        if (pago == null) {
            return false;
        }
        String est = pago.getEstado();
        return est == null || est.isBlank() || "APROBADO".equalsIgnoreCase(est);
    }

    public static BigDecimal sumaAprobada(List<Pago> pagos) {
        if (pagos == null || pagos.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return pagos.stream()
                .filter(PagoUtil::aprobado)
                .map(Pago::getMonto)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal saldoPedido(BigDecimal totalPedido, List<Pago> pagos) {
        BigDecimal total = totalPedido != null ? totalPedido : BigDecimal.ZERO;
        return total.subtract(sumaAprobada(pagos)).max(BigDecimal.ZERO);
    }
}
