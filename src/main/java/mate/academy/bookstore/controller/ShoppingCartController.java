package mate.academy.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.cartitem.CartItemDto;
import mate.academy.bookstore.dto.cartitem.CartItemQuantityDto;
import mate.academy.bookstore.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.bookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.bookstore.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoints for managing shopping cart")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add book to shopping cart", description = "Add book to shopping cart")
    public ShoppingCartDto addBookToShoppingCart(
            @RequestBody @Valid CreateCartItemRequestDto requestDto,
            Authentication authentication) {
        return shoppingCartService.addBookToShoppingCart(requestDto, authentication.getName());
    }

    @GetMapping
    @Operation(summary = "Get shopping cart", description = "Get shopping cart")
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        return shoppingCartService.getShoppingCart(authentication.getName());
    }

    @DeleteMapping("/cart-items/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete cart item", description = "Delete cart item by id")
    public void deleteItemFromShoppingCart(@PathVariable Long id, Authentication authentication) {
        shoppingCartService.deleteItemFromShoppingCart(id, authentication.getName());
    }

    @PutMapping("/cart-items/{id}")
    @Operation(summary = "Update books quantity", description = "Update books quantity")
    public CartItemDto updateQuantity(@PathVariable Long id,
                                      @RequestBody @Valid CartItemQuantityDto quantityDto,
                                      Authentication authentication
    ) {
        return shoppingCartService.updateQuantity(id, quantityDto.quantity(),
                authentication.getName());
    }
}
