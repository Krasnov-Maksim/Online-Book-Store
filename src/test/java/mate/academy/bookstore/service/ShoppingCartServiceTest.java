package mate.academy.bookstore.service;

import static mate.academy.bookstore.config.DatabaseHelper.BOOK_1;
import static mate.academy.bookstore.config.DatabaseHelper.USER_JOHN;
import static mate.academy.bookstore.config.DatabaseHelper.createCartItem;
import static mate.academy.bookstore.config.DatabaseHelper.createShoppingCart;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import mate.academy.bookstore.dto.cartitem.CartItemDto;
import mate.academy.bookstore.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.bookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.bookstore.mapper.CartItemMapper;
import mate.academy.bookstore.mapper.ShoppingCartMapper;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.repository.cartitem.CartItemRepository;
import mate.academy.bookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.bookstore.repository.user.UserRepository;
import mate.academy.bookstore.service.impl.ShoppingCartServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {
    private static final int BOOK_QUANTITY = 1;
    private static final int NEW_BOOK_QUANTITY = 100;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private CartItemMapper cartItemMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    @DisplayName("Add Item to Shopping Cart")
    void addItemToShoppingCart_ValidCartItemRequestDto_Success() {

        CartItem cartItem = createCartItem(1L, null, BOOK_1, BOOK_QUANTITY, false);
        ShoppingCart shoppingCart = createShoppingCart(1L, USER_JOHN, Set.of(cartItem), false);
        cartItem.setShoppingCart(shoppingCart);

        CartItemDto cartItemDto = new CartItemDto(1L, cartItem.getBook().getId(),
                cartItem.getBook().getTitle(), BOOK_QUANTITY);
        ShoppingCartDto expected =
                new ShoppingCartDto(1L, shoppingCart.getUser().getId(), Set.of(cartItemDto));

        when(shoppingCartRepository.getShoppingCartByUserId(anyLong()))
                .thenReturn(shoppingCart);
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(USER_JOHN));
        when(cartItemRepository.save(any(CartItem.class)))
                .thenReturn(cartItem);
        when(shoppingCartRepository.save(any(ShoppingCart.class)))
                .thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart))
                .thenReturn(expected);
        CreateCartItemRequestDto createCartItemRequestDto =
                new CreateCartItemRequestDto(BOOK_1.getId(), BOOK_QUANTITY);
        ShoppingCartDto actual = shoppingCartService
                .addItemToShoppingCart(createCartItemRequestDto, USER_JOHN.getEmail());

        verify(shoppingCartRepository).getShoppingCartByUserId(anyLong());
        verify(cartItemRepository).save(any(CartItem.class));
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update quantity of existing cart item")
    void updateItemQuantity_ValidParams_Success() {
        CartItem existingCartItem = createCartItem(1L, null, BOOK_1, BOOK_QUANTITY, false);
        ShoppingCart shoppingCart = createShoppingCart(1L, USER_JOHN,
                Set.of(existingCartItem), false);
        existingCartItem.setShoppingCart(shoppingCart);

        CartItemDto expected =
                new CartItemDto(1L, existingCartItem.getBook().getId(),
                        existingCartItem.getBook().getTitle(), NEW_BOOK_QUANTITY);

        when(shoppingCartRepository.getShoppingCartByUserId(anyLong()))
                .thenReturn(shoppingCart);
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(USER_JOHN));
        when(cartItemRepository.save(any(CartItem.class)))
                .thenReturn(existingCartItem);
        when(cartItemMapper.toDto(existingCartItem))
                .thenReturn(expected);

        CartItemDto actual = shoppingCartService.updateItemQuantity(existingCartItem.getId(),
                NEW_BOOK_QUANTITY, USER_JOHN.getEmail());

        assertEquals(expected, actual);
        verify(shoppingCartRepository).getShoppingCartByUserId(USER_JOHN.getId());
        verify(cartItemRepository).save(any(CartItem.class));
        verify(userRepository).findByEmail(anyString());
    }

    @Test
    @DisplayName("Get shopping cart by valid email")
    void getShoppingCart_ValidEmail_Success() {
        CartItem cartItem = createCartItem(1L, null, BOOK_1, BOOK_QUANTITY, false);
        ShoppingCart shoppingCart = createShoppingCart(1L, USER_JOHN, Set.of(cartItem), false);
        cartItem.setShoppingCart(shoppingCart);

        CartItemDto cartItemDto =
                new CartItemDto(1L, cartItem.getBook().getId(), cartItem.getBook().getTitle(),
                        BOOK_QUANTITY);
        ShoppingCartDto expected =
                new ShoppingCartDto(1L, shoppingCart.getUser().getId(), Set.of(cartItemDto));

        when(shoppingCartRepository.getShoppingCartByUserId(anyLong()))
                .thenReturn(shoppingCart);
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(USER_JOHN));
        when(shoppingCartMapper.toDto(shoppingCart))
                .thenReturn(expected);

        ShoppingCartDto actual = shoppingCartService.getShoppingCart(USER_JOHN.getEmail());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Delete item from shopping cart by valid params")
    void deleteItemFromShoppingCart_ValidParams_Success() {
        CartItem cartItem = createCartItem(1L, null, BOOK_1, BOOK_QUANTITY, false);
        ShoppingCart shoppingCart = createShoppingCart(1L, USER_JOHN, Set.of(cartItem), false);
        cartItem.setShoppingCart(shoppingCart);

        when(shoppingCartRepository.getShoppingCartByUserId(anyLong()))
                .thenReturn(shoppingCart);
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(USER_JOHN));
        doNothing().when(cartItemRepository).deleteById(anyLong());

        shoppingCartService.deleteItemFromShoppingCart(cartItem.getId(),
                shoppingCart.getUser().getEmail());
        verify(cartItemRepository).deleteById(cartItem.getId());
    }
}
