package mate.academy.bookstore.service;

import java.util.List;
import mate.academy.bookstore.dto.order.CreateOrderRequestDto;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.order.OrderStatusDto;
import mate.academy.bookstore.dto.orderitem.OrderItemDto;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface OrderService {

    OrderDto createOrder(Authentication authentication, CreateOrderRequestDto shippingAddress);

    List<OrderDto> getAllOrders(Authentication authentication, Pageable pageable);

    List<OrderItemDto> getAllOrderItems(Authentication authentication,
                                        Long orderId, Pageable pageable);

    OrderItemDto getSpecificOrderItem(Authentication authentication, Long orderId, Long itemId);

    OrderDto updateOrderStatus(Authentication authentication, Long orderId,
                               OrderStatusDto statusDto);
}
