package mate.academy.bookstore.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import mate.academy.bookstore.model.Order;
import mate.academy.bookstore.model.OrderItem;

public record OrderDto(
        Long id,
        Long userId,
        Set<OrderItem> orderItems,
        LocalDateTime orderDate,
        BigDecimal total,
        Order.Status status,
        String shippingAddress) {
}
