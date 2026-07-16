package com.nexocriminal.validacion;

import java.util.regex.Pattern;

/**
 * Logica de validacion de formatos venezolanos.
 * Clase de utilidad pura (sin estado), reutilizable desde validators,
 * services o cualquier parte del sistema.
 */
public final class ValidadorVenezolano {

    private ValidadorVenezolano() {} // utility class, no instanciable

    // Cedula: V o E seguido opcionalmente de guion, y 6 a 8 digitos
    // Acepta: V-12345678, V12345678, E-1234567, 12345678
    private static final Pattern CEDULA =
            Pattern.compile("^([VvEe]-?)?\\d{6,8}$");

    // Placa actual (2007+): 3 letras/numeros... formato AB123CD (2L 3N 2L)
    // Tambien acepta placas viejas: ABC123, AB123C
    private static final Pattern PLACA =
            Pattern.compile("^([A-Za-z]{2}\\d{3}[A-Za-z]{2}|[A-Za-z]{3}\\d{2}[A-Za-z]|[A-Za-z]{3}\\d{3})$");

    // Codigos de area validos en Venezuela
    private static final Pattern TELEFONO =
            Pattern.compile("^(0?(212|234|235|238|239|240|241|242|243|244|245|246|247|248|249|" +
                    "251|252|253|254|255|256|257|258|259|260|261|262|263|264|265|266|267|268|269|" +
                    "271|272|273|274|275|276|277|278|279|281|282|283|284|285|286|287|288|289|" +
                    "291|292|293|294|295|" +
                    "412|414|416|424|426))-?\\d{7}$");

    // RIF: J/G/V/E/P seguido de guion, 8-9 digitos, guion, 1 digito
    private static final Pattern RIF =
            Pattern.compile("^[JGVEPjgvep]-?\\d{8,9}-?\\d$");

    public static boolean cedulaValida(String cedula) {
        if (cedula == null || cedula.isBlank()) return false;
        return CEDULA.matcher(cedula.trim()).matches();
    }

    public static boolean placaValida(String placa) {
        if (placa == null || placa.isBlank()) return false;
        return PLACA.matcher(placa.trim()).matches();
    }

    public static boolean telefonoValido(String telefono) {
        if (telefono == null || telefono.isBlank()) return false;
        // Quitar espacios y parentesis comunes antes de validar
        String limpio = telefono.trim().replaceAll("[\\s()]", "");
        return TELEFONO.matcher(limpio).matches();
    }

    public static boolean rifValido(String rif) {
        if (rif == null || rif.isBlank()) return false;
        return RIF.matcher(rif.trim()).matches();
    }

    /** Normaliza una cedula al formato V-12345678 */
    public static String normalizarCedula(String cedula) {
        if (cedula == null) return null;
        String c = cedula.trim().toUpperCase().replace("-", "");
        if (c.isEmpty()) return cedula;
        char primera = c.charAt(0);
        if (primera == 'V' || primera == 'E') {
            return primera + "-" + c.substring(1);
        }
        // Si no tiene prefijo, asumir venezolano
        return "V-" + c;
    }
}