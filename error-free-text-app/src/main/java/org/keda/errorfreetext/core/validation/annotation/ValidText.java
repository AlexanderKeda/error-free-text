package org.keda.errorfreetext.core.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.keda.errorfreetext.core.validation.validator.ValidTextValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ValidTextValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidText {

    String message() default "Text must contain at least 3 characters and include letters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
