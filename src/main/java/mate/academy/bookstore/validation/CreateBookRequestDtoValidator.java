package mate.academy.bookstore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import mate.academy.bookstore.dto.CreateBookRequestDto;
import mate.academy.bookstore.validation.annotation.CreateBookRequestDtoValidation;

public class CreateBookRequestDtoValidator
        implements ConstraintValidator<CreateBookRequestDtoValidation, CreateBookRequestDto> {
    @Override
    public boolean isValid(CreateBookRequestDto value, ConstraintValidatorContext context) {
        return value.price() != null && value.price().doubleValue() > 0
                && value.title() != null && !value.title().isBlank()
                && value.author() != null && !value.author().isBlank();
    }
}
