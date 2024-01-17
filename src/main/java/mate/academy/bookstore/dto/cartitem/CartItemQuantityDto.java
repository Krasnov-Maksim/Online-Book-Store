package mate.academy.bookstore.dto.cartitem;

import jakarta.validation.constraints.Min;

public record CartItemQuantityDto(
        @Min(1)
        Integer quantity) {
}
