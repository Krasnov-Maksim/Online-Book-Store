package mate.academy.bookstore.dto.category;

import jakarta.validation.constraints.NotNull;

public record CreateCategoryRequestDto(
        @NotNull
        String name,
        String description) {
}