package mate.academy.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record CreateBookRequestDto(
        @NotBlank()
        String title,
        @NotBlank
        String author,
        String isbn,
        @NotNull
        @PositiveOrZero
        BigDecimal price,
        String description,
        String coverImage) {
}
