package mate.academy.bookstore.repository;

import static mate.academy.bookstore.config.DatabaseHelper.BOOK_1;
import static mate.academy.bookstore.config.DatabaseHelper.USER_JOHN;
import static mate.academy.bookstore.config.DatabaseHelper.createCartItem;
import static mate.academy.bookstore.config.DatabaseHelper.createShoppingCart;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.repository.shoppingcart.ShoppingCartRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShoppingCartRepositoryTest {
    private static final int QUANTITY = 5;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("Get shopping cart by valid User id")
    @Sql(scripts = {
            "classpath:sql/repository/shoppingcart/before/add-user-to-users-table.sql",
            "classpath:sql/repository/shoppingcart/before/add-books-to-books-table.sql",
            "classpath:sql/repository/shoppingcart/before/add-category-to-categories-table.sql",
            "classpath:sql/repository/shoppingcart/before/add-category-to-book.sql",
            "classpath:sql/repository/shoppingcart/before/add-shopping-cart.sql",
            "classpath:sql/repository/shoppingcart/before/add-cartitem-to-cart_items-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:sql/repository/shoppingcart/after/remove-from-cart_items_table.sql",
            "classpath:sql/repository/shoppingcart/after/remove-from-book_category-table.sql",
            "classpath:sql/repository/shoppingcart/after/remove-from-categories-table.sql",
            "classpath:sql/repository/shoppingcart/after/remove-from-shopping_carts_table.sql",
            "classpath:sql/repository/shoppingcart/after/remove-from-books-table.sql",
            "classpath:sql/repository/shoppingcart/after/remove-from-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getShoppingCartByUserId_ValidId_Success() {
        CartItem cartItem = createCartItem(1L, null, BOOK_1, QUANTITY, false);
        ShoppingCart expected = createShoppingCart(1L, USER_JOHN, Set.of(cartItem), false);
        ShoppingCart actual = shoppingCartRepository.getShoppingCartByUserId(USER_JOHN.getId());
        assertNotNull(actual);
        assertEquals(expected.getCartItems().stream().findFirst().get().getBook().getTitle(),
                actual.getCartItems().stream().findFirst().get().getBook().getTitle());
    }
}
