package com.novatech.store.config;

/** Plantillas HTML por defecto con placeholders {{clave}}. */
public final class PlantillaTemplates {

    public static final String CSS = """
            @page { size: A4; margin: 10mm; }
            .doc { font-family: Arial, sans-serif; font-size: 10px; color: #111; line-height: 1.35; }
            .doc-header { display: flex; justify-content: space-between; border-bottom: 2px solid #111; padding-bottom: 8px; margin-bottom: 10px; }
            .doc-title { font-size: 18px; font-weight: 700; margin: 4px 0 0; }
            .doc-meta { text-align: right; font-size: 10px; }
            .doc-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin-bottom: 10px; }
            .doc-grid dt { font-weight: 700; font-size: 9px; text-transform: uppercase; color: #555; }
            .doc-grid dd { margin: 0 0 6px; }
            .doc-table { width: 100%; border-collapse: collapse; margin-bottom: 10px; font-size: 9px; }
            .doc-table th, .doc-table td { border: 1px solid #999; padding: 3px 4px; }
            .doc-table th { background: #eee; text-align: left; }
            .doc-table .num { text-align: right; }
            .doc-totals { margin-left: auto; width: 220px; font-size: 10px; }
            .doc-totals div { display: flex; justify-content: space-between; padding: 2px 0; }
            .doc-totals .final { font-weight: 700; border-top: 2px solid #111; margin-top: 4px; padding-top: 4px; }
            .doc-obs { margin-top: 10px; font-size: 9px; border-top: 1px solid #ccc; padding-top: 8px; }
            .doc-footer { margin-top: 14px; font-size: 8px; color: #666; text-align: center; }
            .doc-cronograma { margin-top: 10px; font-size: 9px; }
            .doc-cronograma table { width: 100%; border-collapse: collapse; }
            .doc-cronograma th, .doc-cronograma td { border: 1px solid #999; padding: 3px 5px; }
            """;

    private static final String ITEM_ROWS = buildItemRows();

    private PlantillaTemplates() {
    }

    public static String jsonFactura() {
        return toJson(buildBody("FACTURA", "Nº {{factura_numero}}", "{{factura_fecha}}",
                true, false));
    }

    public static String jsonPresupuesto() {
        return toJson(buildBody("PRESUPUESTO", "Nº {{presupuesto_numero}}", "{{presupuesto_fecha}}",
                false, true));
    }

    public static String jsonRemito() {
        return toJson(buildBody("REMITO", "Nº {{remito_numero}}", "{{remito_fecha}}",
                false, false));
    }

    private static String buildBody(String titulo, String numeroLine, String fechaLine,
                                    boolean factura, boolean presupuesto) {
        String extraObs = "";
        if (factura) {
            extraObs = """
                    <p><strong>Ref. presupuesto:</strong> {{presupuesto_ref}}</p>
                    <p><strong>Forma de pago:</strong> {{forma_pago}} · <strong>Tasa financ.:</strong> {{tasa_financiacion}}</p>
                    """;
        } else if (presupuesto) {
            extraObs = "<p><strong>Vigencia:</strong> {{vigencia}}</p>";
        }

        return """
                <div class="doc">
                  <header class="doc-header">
                    <div>
                      <strong>{{empresa_nombre}}</strong><br/>
                      {{empresa_direccion}}<br/>
                      Tel: {{empresa_telefono}} · {{empresa_email}}<br/>
                      CUIT: {{empresa_cuit}} · IIBB: {{empresa_ingresos_brutos}}<br/>
                      Inicio act.: {{empresa_inicio_actividades}} · {{empresa_condicion_iva}}
                    </div>
                    <div class="doc-meta">
                      <h1 class="doc-title">%s</h1>
                      <p>%s</p>
                      <p>Fecha: %s</p>
                    </div>
                  </header>
                  <dl class="doc-grid">
                    <div>
                      <dt>Cliente</dt><dd>{{cliente_nombre}}</dd>
                      <dt>Dirección</dt><dd>{{cliente_direccion}}</dd>
                      <dt>CUIT</dt><dd>{{cliente_cuit}}</dd>
                      <dt>Condición IVA</dt><dd>{{cliente_condicion_iva}}</dd>
                    </div>
                    <div>
                      <dt>Vendedor</dt><dd>{{vendedor_nombre}}</dd>
                      <dt>Orden de compra</dt><dd>{{orden_compra}}</dd>
                      <dt>Plazos cobranza</dt><dd>{{plazos_cobranza}}</dd>
                      <dt>Fecha entrega</dt><dd>{{fecha_entrega}}</dd>
                      <dt>Dirección entrega</dt><dd>{{direccion_entrega}}</dd>
                    </div>
                  </dl>
                  <table class="doc-table">
                    <thead>
                      <tr>
                        <th>Cód.</th><th>Descripción</th><th>Detalle</th>
                        <th class="num">Cant.</th><th class="num">Precio</th><th class="num">Subtotal</th>
                      </tr>
                    </thead>
                    <tbody>
                """.formatted(titulo, numeroLine, fechaLine)
                + ITEM_ROWS
                + """
                    </tbody>
                  </table>
                  <p style="font-size:9px;"><strong>Son pesos:</strong> {{total_en_letras}}</p>
                  <div class="doc-totals">
                    <div><span>Subtotal</span><span>{{total_subtotal}}</span></div>
                    <div><span>Bonificación</span><span>{{total_bonificacion}}</span></div>
                    <div><span>Subtotal neto</span><span>{{total_subtotal_neto}}</span></div>
                    {{filas_totales_extra}}
                    <div class="final"><span>Total</span><span>{{total_final}}</span></div>
                  </div>
                  <div class="doc-obs">
                    <p><strong>Observaciones:</strong> {{observaciones_texto}}</p>
                    <p><strong>Plazo entrega:</strong> {{plazo_entrega}} · <strong>Garantía:</strong> {{garantia}}</p>
                """
                + extraObs
                + """
                  </div>
                  {{seccion_cronograma}}
                  <footer class="doc-footer">Documento generado por NovaTech ERP</footer>
                </div>
                """;
    }

    private static String buildItemRows() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 20; i++) {
            sb.append("""
                    <tr>
                      <td>{{item%d_codigo}}</td>
                      <td>{{item%d_descripcion_corta}}</td>
                      <td>{{item%d_descripcion_larga}}</td>
                      <td class="num">{{item%d_cantidad}}</td>
                      <td class="num">{{item%d_precio}}</td>
                      <td class="num">{{item%d_subtotal}}</td>
                    </tr>
                    """.formatted(i, i, i, i, i, i));
        }
        return sb.toString();
    }

    private static String toJson(String html) {
        String escaped = html.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
        String cssEscaped = CSS.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
        return "{\"html\":\"" + escaped + "\",\"css\":\"" + cssEscaped + "\"}";
    }
}
