package com.nexocriminal.validacion.anotaciones;

import com.nexocriminal.validacion.validators.PlacaValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PlacaValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PlacaVenezolana {
    String message() default "Placa venezolana invalida (formato esperado: AB123CD)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean permitirVacio() default true;
}