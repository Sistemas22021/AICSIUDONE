package com.nexocriminal.validacion.anotaciones;

import com.nexocriminal.validacion.validators.CedulaValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CedulaValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CedulaVenezolana {
    String message() default "Cedula venezolana invalida (formato esperado: V-12345678)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean permitirVacio() default true; // util para campos opcionales
}