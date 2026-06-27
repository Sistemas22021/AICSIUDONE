package com.nexocriminal.validacion.validators;

import com.nexocriminal.validacion.ValidadorVenezolano;
import com.nexocriminal.validacion.anotaciones.PlacaVenezolana;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PlacaValidator implements ConstraintValidator<PlacaVenezolana, String> {

    private boolean permitirVacio;

    @Override
    public void initialize(PlacaVenezolana annotation) {
        this.permitirVacio = annotation.permitirVacio();
    }

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext context) {
        if (valor == null || valor.isBlank()) {
            return permitirVacio;
        }
        return ValidadorVenezolano.placaValida(valor);
    }
}