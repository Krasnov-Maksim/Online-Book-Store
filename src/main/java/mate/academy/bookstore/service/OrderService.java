package mate.academy.bookstore.service;

import java.util.List;
import mate.academy.bookstore.dto.order.CreateOrderRequestDto;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.order.OrderStatusDto;
import mate.academy.bookstore.dto.orderitem.OrderItemDto;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderDto createOrder(String email, CreateOrderRequestDto createOrderRequestDto);

    List<OrderDto> getAllOrders(String email, Pageable pageable);

    List<OrderItemDto> getAllOrderItems(String email, Long orderId, Pageable pageable);

    OrderItemDto getSpecificOrderItem(String email, Long orderId, Long itemId);

    OrderDto updateOrderStatus(String email, Long orderId, OrderStatusDto statusDto);
}
