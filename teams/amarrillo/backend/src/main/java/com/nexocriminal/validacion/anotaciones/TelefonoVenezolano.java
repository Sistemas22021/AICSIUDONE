package com.nexocriminal.validacion.anotaciones;

import com.nexocriminal.validacion.validators.TelefonoValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TelefonoValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface TelefonoVenezolano {
    String message() default "Telefono venezolano invalido (formato esperado: 0414-1234567)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean permitirVacio() default true;
}