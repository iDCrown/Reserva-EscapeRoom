package io.bootify.reserva.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidNameValidator implements ConstraintValidator<ValidName, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        // ❌ Rechaza cosas como "ppp", "aaa", "xxxx"
        if (value.matches("^(.)\\1{2,}$")) return false;

        // ❌ Rechaza nombres repetidos (ej: nombre y apellido iguales)
        // (este se valida en el controller usando ambos campos juntos)

        // ✅ Debe contener solo letras y espacios
        return value.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$");
    }
}
