package mate.academy.bookstore.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Checks that the annotated character sequence is a valid
 * <a href="https://en.wikipedia.org/wiki/International_Standard_Book_Number">ISBN</a>.
 * <p> {@code null} is considered valid.
 */
@Constraint(validatedBy = IsbnValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Isbn {
    String message() default "{Isbn not correct}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
