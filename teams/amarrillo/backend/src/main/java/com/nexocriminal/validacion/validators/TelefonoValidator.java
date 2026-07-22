package com.nexocriminal.validacion.validators;

import com.nexocriminal.validacion.ValidadorVenezolano;
import com.nexocriminal.validacion.anotaciones.TelefonoVenezolano;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefonoValidator implements ConstraintValidator<TelefonoVenezolano, String> {

    private boolean permitirVacio;

    @Override
    public void initialize(TelefonoVenezolano annotation) {
        this.permitirVacio = annotation.permitirVacio();
    }

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext context) {
        if (valor == null || valor.isBlank()) {
            return permitirVacio;
        }
        return ValidadorVenezolano.telefonoValido(valor);
    }
}