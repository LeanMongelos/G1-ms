package com.novatech.store.service;

import com.novatech.store.entity.AlicuotaIva;
import com.novatech.store.entity.ConfiguracionSistema;
import com.novatech.store.repository.AlicuotaIvaRepository;
import com.novatech.store.repository.ConfiguracionSistemaRepository;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContabilidadConfigService {

    private final AlicuotaIvaRepository alicuotaRepository;
    private final ConfiguracionSistemaRepository configRepository;

    public ContabilidadConfigService(AlicuotaIvaRepository alicuotaRepository,
                                       ConfiguracionSistemaRepository configRepository) {
        this.alicuotaRepository = alicuotaRepository;
        this.configRepository = configRepository;
    }

    public Map<String, Object> resumen() {
        ensureContabilidadArgentina();
        return buildResumen();
    }

    @Transactional
    public Map<String, Object> ensureContabilidadArgentina() {
        if (alicuotaRepository.count() == 0) {
            seedAlicuotas();
        }
        ensureConfig("contabilidad", "moneda_base", "ARS", "Moneda base");
        ensureConfig("contabilidad", "ejercicio_fiscal", "2026", "Ejercicio fiscal");
        ensureConfig("contabilidad", "iva_general", "21", "IVA general (%)");
        ensureConfig("contabilidad", "iibb", "3", "IIBB (%)");
        return buildResumen();
    }

    private Map<String, Object> buildResumen() {
        Map<String, Object> out = new HashMap<>();
        out.put("alicuotas", alicuotaRepository.findAllByOrderByPorcentajeAsc());
        out.put("config", configRepository.findByGrupoIgnoreCase("contabilidad"));
        out.put("contadores", Map.of(
                "alicuotas", alicuotaRepository.count(),
                "condicionesIva", countConfig("contabilidad", "condicion_iva"),
                "regimenes", countConfig("contabilidad", "regimen"),
                "planCuentas", countConfig("contabilidad", "cuenta")));
        return out;
    }

    private void seedAlicuotas() {
        crearAlicuota("0003", "0%", BigDecimal.ZERO);
        crearAlicuota("0004", "10,5%", new BigDecimal("10.5"));
        crearAlicuota("0005", "21%", new BigDecimal("21"));
        crearAlicuota("0006", "27%", new BigDecimal("27"));
    }

    private void crearAlicuota(String codigo, String nombre, BigDecimal pct) {
        AlicuotaIva a = new AlicuotaIva();
        a.setCodigo(codigo);
        a.setNombre(nombre);
        a.setPorcentaje(pct);
        a.setActivo(true);
        alicuotaRepository.save(a);
    }

    private void ensureConfig(String grupo, String clave, String valor, String desc) {
        if (configRepository.findByGrupoIgnoreCase(grupo).stream()
                .noneMatch(c -> clave.equalsIgnoreCase(c.getClave()))) {
            ConfiguracionSistema c = new ConfiguracionSistema();
            c.setGrupo(grupo);
            c.setClave(clave);
            c.setValor(valor);
            c.setDescripcion(desc);
            configRepository.save(c);
        }
    }

    private long countConfig(String grupo, String prefijoClave) {
        return configRepository.findByGrupoIgnoreCase(grupo).stream()
                .filter(c -> c.getClave() != null && c.getClave().startsWith(prefijoClave))
                .count();
    }
}
