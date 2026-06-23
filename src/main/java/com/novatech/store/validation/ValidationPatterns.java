package com.novatech.store.validation;

/** Regex compartidos entre DTOs, entidades y validación manual. */
public final class ValidationPatterns {

    public static final String EMAIL =
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    public static final String TELEFONO = "^[\\d\\s+\\-()]{8,20}$";

    /** Nombre de persona: 2–100 caracteres alfanuméricos con espacios, apóstrofes o guiones. */
    public static final String NOMBRE =
            "^[\\p{L}][\\p{L}\\p{N} '\\-]{0,98}[\\p{L}\\p{N}]$|^[\\p{L}]{2,100}$";

    public static final String METODO_PAGO =
            "^(TARJETA|EFECTIVO|TRANSFERENCIA|MERCADO_PAGO|BILLETERA_VIRTUAL|QR|PRESTAMO_CASA|CONTRA_ENTREGA)$";

    public static final String TIPO_ENTREGA = "^(ENVIO|RETIRO_TIENDA)$";

    public static final String CANAL_ORIGEN =
            "^(WEB|ADMIN|WHATSAPP|EMAIL|INSTAGRAM|FACEBOOK|POS)$";

    public static final String CUIT = "^\\d{2}-\\d{8}-\\d{1}$";

    public static final String TELEFONO_OPCIONAL = "^(|[\\d\\s+\\-()]{8,20})$";

    public static final String EMAIL_OPCIONAL =
            "^(|[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})$";

    public static final String NOMBRE_OPCIONAL =
            "^(|[\\p{L}][\\p{L}\\p{N} '\\-]{0,98}[\\p{L}\\p{N}]$|^[\\p{L}]{2,100})$";

    private ValidationPatterns() {
    }
}
