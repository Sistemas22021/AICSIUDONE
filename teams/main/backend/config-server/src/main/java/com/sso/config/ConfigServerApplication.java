package com.sso.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Punto de entrada del Config Server.
 *
 * <p>El Config Server actúa como fuente única de verdad para toda la
 * configuración del ecosistema. Lee los archivos YAML del directorio
 * {@code /config-repo} del monorepo y los sirve a los demás servicios
 * a través de HTTP.
 *
 * <p><b>Flujo de configuración:</b>
 * <ol>
 *   <li>El servicio arranca y se conecta al Config Server</li>
 *   <li>Solicita su configuración: {@code /{nombre-servicio}/{ambiente}}</li>
 *   <li>Recibe el YAML correspondiente antes de inicializar sus beans</li>
 * </ol>
 *
 * <p><b>Namespaces de archivos en config-repo:</b>
 * {@code auth-service-dev.yml}, {@code auth-service-prod.yml}, etc.
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
