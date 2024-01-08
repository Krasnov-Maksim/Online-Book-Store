package mate.academy.bookstore.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import mate.academy.bookstore.validation.Isbn;

public record CreateBookRequestDto(
        @NotBlank
        String title,
        @NotBlank
        String author,
        @Isbn
        String isbn,
        @PositiveOrZero
        BigDecimal price,
        @Size(min = 3)
        String description,
        String coverImage) {
}
