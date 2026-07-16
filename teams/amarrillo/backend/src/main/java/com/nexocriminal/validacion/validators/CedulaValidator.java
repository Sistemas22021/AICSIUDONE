package com.nexocriminal.validacion.validators;

import com.nexocriminal.validacion.ValidadorVenezolano;
import com.nexocriminal.validacion.anotaciones.CedulaVenezolana;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CedulaValidator implements ConstraintValidator<CedulaVenezolana, String> {

    private boolean permitirVacio;

    @Override
    public void initialize(CedulaVenezolana annotation) {
        this.permitirVacio = annotation.permitirVacio();
    }

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext context) {
        if (valor == null || valor.isBlank()) {
            return permitirVacio;
        }
        return ValidadorVenezolano.cedulaValida(valor);
    }
}