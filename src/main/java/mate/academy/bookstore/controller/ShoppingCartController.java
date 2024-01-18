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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoints for managing shopping cart")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Add book to shopping cart", description = "Add book to shopping cart")
    @PostMapping
    public ShoppingCartDto addBookToShoppingCart(
            @RequestBody @Valid CreateCartItemRequestDto requestDto,
            Authentication authentication) {
        return shoppingCartService.addBookToShoppingCart(requestDto, authentication.getName());
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get shopping cart", description = "Get shopping cart")
    @GetMapping
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        return shoppingCartService.getShoppingCart(authentication.getName());
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Delete cart item", description = "Delete cart item by id")
    @DeleteMapping("/cart-items/{id}")
    public void deleteItemFromShoppingCart(@PathVariable Long id, Authentication authentication) {
        shoppingCartService.deleteItemFromShoppingCart(id, authentication.getName());
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Update books quantity", description = "Update books quantity")
    @PutMapping("/cart-items/{id}")
    public CartItemDto updateQuantity(@PathVariable Long id,
                                      @RequestBody @Valid CartItemQuantityDto quantityDto,
                                      Authentication authentication
    ) {
        return shoppingCartService.updateItemQuantity(id, quantityDto.quantity(),
                authentication.getName());
    }
}
