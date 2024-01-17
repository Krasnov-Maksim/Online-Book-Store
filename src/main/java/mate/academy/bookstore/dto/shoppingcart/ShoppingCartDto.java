package mate.academy.bookstore.dto.shoppingcart;

import java.util.List;
import mate.academy.bookstore.dto.cartitem.CartItemDto;

public record ShoppingCartDto(
        Long id,
        Long userId,
        List<CartItemDto> cartItems) {
}
