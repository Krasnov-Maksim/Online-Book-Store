package mate.academy.bookstore.repository;

import static mate.academy.bookstore.config.DatabaseHelper.USER_JOHN;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import mate.academy.bookstore.model.Order;
import mate.academy.bookstore.repository.order.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryTest {
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 3;
    private static final Long ORDER_ID = 1L;
    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("Get all orders by valid User id")
    @Sql(scripts = {
            "classpath:sql/repository/order/before/add-books-to-books-table.sql",
            "classpath:sql/repository/order/before/add-category-to-categories-table.sql",
            "classpath:sql/repository/order/before/add-category-to-book.sql",
            "classpath:sql/repository/order/before/add-user-to-users-table.sql",
            "classpath:sql/repository/order/before/add-orders-to-orders_table.sql",
            "classpath:sql/repository/order/before/add-items-to-order_items-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:sql/repository/order/after/remove-from-order_items-table.sql",
            "classpath:sql/repository/order/after/remove-from-orders-table.sql",
            "classpath:sql/repository/order/after/remove-from-users-table.sql",
            "classpath:sql/repository/order/after/remove-from-book_category.sql",
            "classpath:sql/repository/order/after/remove-from-books.sql",
            "classpath:sql/repository/order/after/remove-from-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAllByUserId_WithPaginationAndValidId_Success() {
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        List<Order> actual = orderRepository.getAllByUserId(USER_JOHN.getId(), pageable);
        assertNotNull(actual);
        assertFalse(actual.get(0).getOrderItems().isEmpty());
    }

    @Test
    @DisplayName("Find order by valid User id and valid Order id")
    @Sql(scripts = {
            "classpath:sql/repository/order/before/add-books-to-books-table.sql",
            "classpath:sql/repository/order/before/add-category-to-categories-table.sql",
            "classpath:sql/repository/order/before/add-category-to-book.sql",
            "classpath:sql/repository/order/before/add-user-to-users-table.sql",
            "classpath:sql/repository/order/before/add-orders-to-orders_table.sql",
            "classpath:sql/repository/order/before/add-items-to-order_items-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:sql/repository/order/after/remove-from-order_items-table.sql",
            "classpath:sql/repository/order/after/remove-from-orders-table.sql",
            "classpath:sql/repository/order/after/remove-from-users-table.sql",
            "classpath:sql/repository/order/after/remove-from-book_category.sql",
            "classpath:sql/repository/order/after/remove-from-books.sql",
            "classpath:sql/repository/order/after/remove-from-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByUserIdAndId_ValidParams_Success() {
        Optional<Order> actual = orderRepository.findByUserIdAndId(USER_JOHN.getId(), ORDER_ID);
        assertTrue(actual.isPresent());
        assertFalse(actual.get().getOrderItems().isEmpty());
    }
}
