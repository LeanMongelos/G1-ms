package com.novatech.store.validation;

import com.novatech.store.exception.ReglaNegocioException;
import java.util.regex.Pattern;

// Valida que el nombre de una categoria sea coherente (ej: "Notebooks", "Sillas Gamer").
// Bloquea basura como "4dfs---0.0.00.df0sdf" o "H: x[][]{} ...".
public final class NombreCategoriaValidator {

    // Solo letras (con acentos), numeros, espacios y guiones simples.
    private static final Pattern CARACTERES_PERMITIDOS = Pattern.compile(
            "^[\\p{L}\\p{N} '-]+$", Pattern.UNICODE_CHARACTER_CLASS);

    private NombreCategoriaValidator() {
    }

    public static void validar(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaNegocioException("El nombre de la categoria es obligatorio.");
        }

        String limpio = nombre.trim();

        if (limpio.length() < 2 || limpio.length() > 50) {
            throw new ReglaNegocioException("El nombre debe tener entre 2 y 50 caracteres.");
        }

        // Debe empezar con letra (no con numero ni simbolo).
        if (!Character.isLetter(limpio.charAt(0))) {
            throw new ReglaNegocioException("El nombre debe empezar con una letra.");
        }

        // Debe terminar con letra o numero (no con espacio ni guion suelto).
        char ultimo = limpio.charAt(limpio.length() - 1);
        if (!Character.isLetterOrDigit(ultimo)) {
            throw new ReglaNegocioException("El nombre debe terminar con letra o numero.");
        }

        if (!CARACTERES_PERMITIDOS.matcher(limpio).matches()) {
            throw new ReglaNegocioException(
                    "Solo se permiten letras, numeros, espacios y guiones simples.");
        }

        // Secuencias raras que suelen ser pruebas basura.
        if (limpio.contains(".") || limpio.contains("_") || limpio.contains("--")
                || limpio.contains("  ") || limpio.contains("''")) {
            throw new ReglaNegocioException(
                    "El nombre no puede tener puntos, guiones dobles ni espacios seguidos.");
        }

        long letras = limpio.chars().filter(Character::isLetter).count();
        if (letras < 2) {
            throw new ReglaNegocioException("El nombre debe incluir al menos 2 letras.");
        }

        long digitos = limpio.chars().filter(Character::isDigit).count();
        // Si hay mas digitos que letras, probablemente no es un nombre real de categoria.
        if (digitos > letras) {
            throw new ReglaNegocioException(
                    "El nombre parece incoherente: tiene demasiados numeros.");
        }
    }
}
