package mate.academy.bookstore.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import mate.academy.bookstore.validation.CreateBookRequestDtoValidator;

@Constraint(validatedBy = CreateBookRequestDtoValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CreateBookRequestDtoValidation {
    String message() default "{blank title or blank author or price < 0}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
