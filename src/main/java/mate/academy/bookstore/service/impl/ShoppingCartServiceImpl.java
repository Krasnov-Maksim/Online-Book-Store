package mate.academy.bookstore.service.impl;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.cartitem.CartItemDto;
import mate.academy.bookstore.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.bookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.BookMapper;
import mate.academy.bookstore.mapper.ShoppingCartMapper;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.repository.cartitem.CartItemRepository;
import mate.academy.bookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.bookstore.repository.user.UserRepository;
import mate.academy.bookstore.service.BookService;
import mate.academy.bookstore.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
class ShoppingCartServiceImpl implements ShoppingCartService {
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;
    private final BookService bookService;
    private final ShoppingCartMapper shoppingCartMapper;
    private final BookMapper bookMapper;

    @Override
    public ShoppingCartDto addBookToShoppingCart(CreateCartItemRequestDto requestDto,
                                                 String email) {
        ShoppingCart shoppingCart = getShoppingCartByEmail(email);
        Optional<CartItem> cartItemInShoppingCart =
                findCartItemInShoppingCart(shoppingCart, requestDto.bookId());
        CartItem cartItem;
        if (cartItemInShoppingCart.isPresent()) {
            cartItem = cartItemInShoppingCart.get();
            cartItem.setQuantity(cartItem.getQuantity() + requestDto.quantity());
        } else {
            cartItem = createCartItem(requestDto, shoppingCart);
        }
        cartItemRepository.save(cartItem);
        shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto getShoppingCart(String email) {
        return null;
    }

    @Override
    public void deleteItemFromShoppingCart(Long id, String email) {

    }

    @Override
    public CartItemDto updateItemQuantity(Long id, int quantity, String email) {
        return null;
    }

    private ShoppingCart getShoppingCartByEmail(String email) {
        return shoppingCartRepository
                .getShoppingCartById(userRepository.findByEmail(email).orElseThrow(
                                () -> new EntityNotFoundException(
                                        "Can't find shopping cart by email" + email))
                        .getId());
    }

    private CartItem createCartItem(CreateCartItemRequestDto requestDto,
                                    ShoppingCart shoppingCart) {
        CartItem cartItem = new CartItem();
        cartItem.setBook(bookMapper.toModel(bookService.getBookById(requestDto.bookId())));
        cartItem.setQuantity(requestDto.quantity());
        cartItem.setShoppingCart(shoppingCart);
        shoppingCart.getCartItems().add(cartItem);
        return cartItem;
    }

    private Optional<CartItem> findCartItemInShoppingCart(ShoppingCart shoppingCart, Long id) {
        return shoppingCart.getCartItems().stream()
                .filter(cartItem -> cartItem.getBook().getId().equals(id))
                .findFirst();

    }
}
