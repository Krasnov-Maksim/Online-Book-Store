package mate.academy.bookstore.service.impl;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.cartitem.CartItemDto;
import mate.academy.bookstore.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.bookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.BookMapper;
import mate.academy.bookstore.mapper.CartItemMapper;
import mate.academy.bookstore.mapper.ShoppingCartMapper;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.repository.book.BookRepository;
import mate.academy.bookstore.repository.cartitem.CartItemRepository;
import mate.academy.bookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.bookstore.repository.user.UserRepository;
import mate.academy.bookstore.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
class ShoppingCartServiceImpl implements ShoppingCartService {
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final BookMapper bookMapper;
    private final CartItemMapper cartItemMapper;

    @Override
    public ShoppingCartDto addItemToShoppingCart(CreateCartItemRequestDto requestDto,
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
        return shoppingCartMapper.toDto(shoppingCart, cartItemMapper);
    }

    @Override
    public ShoppingCartDto getShoppingCart(String email) {
        return shoppingCartMapper.toDto(getShoppingCartByEmail(email), cartItemMapper);
    }

    @Override
    public void deleteItemFromShoppingCart(Long cartItemId, String email) {
        getShoppingCartByEmail(email);
        cartItemRepository.deleteById(cartItemId);
    }

    @Override
    public CartItemDto updateItemQuantity(Long cartItemId, int quantity, String email) {
        ShoppingCart shoppingCart = getShoppingCartByEmail(email);
        CartItem cartItem = shoppingCart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find cart item with id: "
                                + cartItemId)
                );
        cartItem.setQuantity(quantity);
        return cartItemMapper.toDto(cartItemRepository.save(cartItem));
    }

    private ShoppingCart getShoppingCartByEmail(String email) {
        return shoppingCartRepository
                .getShoppingCartById(userRepository.findByEmail(email)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Can't find shopping cart by email" + email))
                        .getId());
    }

    private CartItem createCartItem(CreateCartItemRequestDto requestDto,
                                    ShoppingCart shoppingCart) {
        CartItem cartItem = new CartItem();
        Long bookId = requestDto.bookId();
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id:" + bookId));
        cartItem.setBook(book);
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
