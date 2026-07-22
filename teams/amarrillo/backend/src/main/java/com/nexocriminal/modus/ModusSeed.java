package com.nexocriminal.modus;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Siembra el catálogo de modus operandi al arrancar (solo los que falten).
 */
@Component
@RequiredArgsConstructor
public class ModusSeed implements CommandLineRunner {

    private final ModusOperandiService service;

    @Override
    public void run(String... args) {
        // Robo de vehículo
        service.sembrarSiNoExiste("INHIBIDOR_SENAL", "Inhibidor de señal", "Bloqueo de la señal de alarma/cierre centralizado.");
        service.sembrarSiNoExiste("LLAVE_MAESTRA", "Llave maestra", "Uso de llave maestra o ganzúa.");
        service.sembrarSiNoExiste("ENGANO_VICTIMA", "Engaño a la víctima", "El delincuente se gana la confianza de la víctima.");
        service.sembrarSiNoExiste("GRUA_FALSA", "Grúa falsa", "Retiro del vehículo con una grúa simulando un servicio.");
        service.sembrarSiNoExiste("CLONACION_LLAVE", "Clonación de llave", "Copia electrónica de la llave del vehículo.");
        service.sembrarSiNoExiste("ASALTO_ARMADO", "Asalto armado", "Sustracción con uso de arma.");
        service.sembrarSiNoExiste("HURTO_DESCUIDO", "Hurto por descuido", "Aprovechamiento de un descuido de la víctima.");
        service.sembrarSiNoExiste("ROBO_ESTACIONAMIENTO", "Robo en estacionamiento", "Sustracción en estacionamiento público o privado.");
        service.sembrarSiNoExiste("INTERCEPTACION_VIA", "Interceptación en vía", "Interceptación del vehículo en circulación.");
        service.sembrarSiNoExiste("ROBO_DOMICILIO", "Robo en domicilio", "Sustracción del vehículo desde el domicilio.");

        // Escolta / banda
        service.sembrarSiNoExiste("ESCOLTA_VEHICULAR", "Escolta vehicular", "Vehículo que acompaña al robado.");
        service.sembrarSiNoExiste("VEHICULO_APOYO", "Vehículo de apoyo", "Vehículo de soporte logístico de la banda.");

        // Desaparición
        service.sembrarSiNoExiste("DESAPARICION_FORZADA", "Desaparición forzada", "Privación de libertad seguida de ocultamiento.");
        service.sembrarSiNoExiste("SECUESTRO_EXPRESS", "Secuestro exprés", "Retención breve para extracción de bienes.");
        service.sembrarSiNoExiste("RECLUTAMIENTO", "Reclutamiento", "Captación forzada o engañosa de la víctima.");
        service.sembrarSiNoExiste("TRATA_PERSONAS", "Trata de personas", "Explotación mediante captación y traslado.");

        // Transversal
        service.sembrarSiNoExiste("INTIMIDACION", "Intimidación", "Uso de amenazas o coacción.");
        service.sembrarSiNoExiste("SUPLANTACION_IDENTIDAD", "Suplantación de identidad", "Hacerse pasar por otra persona o autoridad.");
        service.sembrarSiNoExiste("SOBORNO", "Soborno", "Pago indebido para facilitar el hecho.");
        service.sembrarSiNoExiste("OTRO", "Otro", "Modus no clasificado en el catálogo.");
    }
}