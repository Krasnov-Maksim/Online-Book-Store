package mate.academy.bookstore.dto.cartitem;

import jakarta.validation.constraints.NotNull;

public record CreateCartItemRequestDto(
        @NotNull
        Long bookId,
        @NotNull
        Integer quantity) {
}
