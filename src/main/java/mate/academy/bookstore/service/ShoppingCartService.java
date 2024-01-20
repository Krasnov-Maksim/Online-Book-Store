package mate.academy.bookstore.service;

import mate.academy.bookstore.dto.cartitem.CartItemDto;
import mate.academy.bookstore.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.bookstore.dto.shoppingcart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto addItemToShoppingCart(CreateCartItemRequestDto requestDto, String email);

    ShoppingCartDto getShoppingCart(String email);

    void deleteItemFromShoppingCart(Long cartItemId, String email);

    CartItemDto updateItemQuantity(Long cartItemId, int quantity, String email);
}
