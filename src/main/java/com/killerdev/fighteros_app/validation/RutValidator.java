package com.killerdev.fighteros_app.validation;

// Validación de RUT chileno: formato + dígito verificador (módulo 11).
public final class RutValidator {

    private RutValidator() {
    }

    public static boolean esValido(String rut) {
        if (rut == null) {
            return false;
        }
        String limpio = limpiar(rut);
        if (limpio.length() < 2) {
            return false;
        }
        String cuerpo = limpio.substring(0, limpio.length() - 1);
        char dv = limpio.charAt(limpio.length() - 1);
        if (!cuerpo.chars().allMatch(Character::isDigit)) {
            return false;
        }
        return dv == calcularDigitoVerificador(cuerpo);
    }

    // Devuelve el RUT en formato "########-#" para persistir (calza con el check de la DB).
    public static String normalizar(String rut) {
        String limpio = limpiar(rut);
        String cuerpo = limpio.substring(0, limpio.length() - 1);
        char dv = limpio.charAt(limpio.length() - 1);
        return cuerpo + "-" + dv;
    }

    private static String limpiar(String rut) {
        return rut.replace(".", "").replace("-", "").trim().toUpperCase();
    }

    private static char calcularDigitoVerificador(String cuerpo) {
        int suma = 0;
        int multiplicador = 2;
        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            suma += Character.getNumericValue(cuerpo.charAt(i)) * multiplicador;
            multiplicador = multiplicador == 7 ? 2 : multiplicador + 1;
        }
        int resto = 11 - (suma % 11);
        return switch (resto) {
            case 11 -> '0';
            case 10 -> 'K';
            default -> Character.forDigit(resto, 10);
        };
    }
}
