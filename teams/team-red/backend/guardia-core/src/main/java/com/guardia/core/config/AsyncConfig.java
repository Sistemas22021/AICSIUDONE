package com.guardia.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Habilita el procesamiento de métodos {@code @Async} (usado por
 * {@code DeteccionModusOperandiServiceImpl} en la HU2).
 *
 * <p>El executor real que ejecuta esas tareas es el
 * {@code ApplicationTaskExecutor} auto-configurado por Spring Boot. Gracias a
 * {@code spring.threads.virtual.enabled=true} (ver application.properties),
 * Boot 3.2+ reemplaza ese executor por uno basado en Virtual Threads de
 * Java 21 automáticamente — no hace falta declarar un {@code Executor} manual
 * aquí. Si en el futuro se requiere un pool dedicado (por ejemplo, para
 * limitar la concurrencia de llamadas a OpenAI), este es el lugar para
 * agregar un bean {@code @Bean Executor moTaskExecutor()}.</p>
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}