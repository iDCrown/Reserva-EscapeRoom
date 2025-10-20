package io.bootify.reserva.valid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidEmailDomainValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmailDomain {
    String message() default "El dominio del correo no es válido o parece falso.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
