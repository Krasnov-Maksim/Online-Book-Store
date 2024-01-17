package mate.academy.bookstore.service;

import mate.academy.bookstore.dto.cartitem.CartItemDto;
import mate.academy.bookstore.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.bookstore.dto.shoppingcart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto addBookToShoppingCart(CreateCartItemRequestDto requestDto, String email);

    ShoppingCartDto getShoppingCart(String email);

    void deleteItemFromShoppingCart(Long id, String email);

    CartItemDto updateQuantity(Long id, int quantity, String email);
}
