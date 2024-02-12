package mate.academy.bookstore.service;

import static mate.academy.bookstore.config.DatabaseHelper.BOOK_1;
import static mate.academy.bookstore.config.DatabaseHelper.USER_JOHN;
import static mate.academy.bookstore.config.DatabaseHelper.createCartItem;
import static mate.academy.bookstore.config.DatabaseHelper.createOrder;
import static mate.academy.bookstore.config.DatabaseHelper.createOrderItem;
import static mate.academy.bookstore.config.DatabaseHelper.createOrderItemDto;
import static mate.academy.bookstore.config.DatabaseHelper.createShoppingCart;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import mate.academy.bookstore.dto.order.CreateOrderRequestDto;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.order.OrderStatusDto;
import mate.academy.bookstore.dto.orderitem.OrderItemDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.OrderItemMapper;
import mate.academy.bookstore.mapper.OrderMapper;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.Order;
import mate.academy.bookstore.model.OrderItem;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.repository.order.OrderRepository;
import mate.academy.bookstore.repository.orderitem.OrderItemRepository;
import mate.academy.bookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.bookstore.repository.user.UserRepository;
import mate.academy.bookstore.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderItemMapper orderItemMapper;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("Create order with valid params")
    void createOrder_ValidParams_Success() {
        CartItem cartItem = createCartItem(1L, null, BOOK_1, 100, false);
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);
        ShoppingCart shoppingCart = createShoppingCart(1L, USER_JOHN, cartItems, false);
        cartItem.setShoppingCart(shoppingCart);

        OrderItem orderItem = createOrderItem(1L, null, cartItem.getBook(),
                cartItem.getQuantity(), cartItem.getBook().getPrice(), false);

        Order order = createOrder(1L, USER_JOHN, Order.Status.PENDING,
                BigDecimal.valueOf(12500, 2), LocalDateTime.now(),
                USER_JOHN.getShippingAddress(), Set.of(orderItem), false);
        orderItem.setOrder(order);

        OrderItemDto orderItemDto = createOrderItemDto(1L, orderItem.getBook().getId(),
                orderItem.getQuantity());
        OrderDto expected = new OrderDto(order.getId(), order.getUser().getId(),
                Set.of(orderItemDto), order.getOrderDate(), order.getTotal(), order.getStatus(),
                order.getShippingAddress());

        when(shoppingCartRepository.getShoppingCartByUserId(anyLong()))
                .thenReturn(shoppingCart);
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(USER_JOHN));
        when(orderItemRepository.saveAll(any()))
                .thenReturn(List.of(orderItem));
        when(shoppingCartRepository.save(any(ShoppingCart.class)))
                .thenReturn(shoppingCart);
        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);
        when(orderMapper.toDto(any(Order.class)))
                .thenReturn(expected);

        CreateOrderRequestDto createOrderRequestDto =
                new CreateOrderRequestDto(USER_JOHN.getShippingAddress());
        OrderDto actual = orderService.createOrder(USER_JOHN.getEmail(), createOrderRequestDto);
        assertEquals(expected, actual);
        verify(orderItemRepository).saveAll(any());
        verify(shoppingCartRepository).save(any(ShoppingCart.class));
        verify(orderRepository).save(any(Order.class));
        verify(userRepository).findByEmail(anyString());
    }

    @Test
    @DisplayName("Create order with invalid params should throw EntityNotFoundException")
    void createOrder_invalidUser_shouldReturnEntityNotFoundException() {
        CreateOrderRequestDto createOrderRequestDto =
                new CreateOrderRequestDto(USER_JOHN.getShippingAddress());
        EntityNotFoundException entityNotFoundException = assertThrows(
                EntityNotFoundException.class,
                () -> orderService.createOrder("invalid@mail.com", createOrderRequestDto));
        assertEquals(EntityNotFoundException.class, entityNotFoundException.getClass());
    }

    @Test
    @DisplayName("Get all orders with valid params")
    void getAllOrders_ValidEmail_Success() {
        OrderItem orderItem = createOrderItem(1L, null, BOOK_1, 100,
                BOOK_1.getPrice(), false);
        Order order = createOrder(1L, USER_JOHN, Order.Status.PENDING,
                BigDecimal.valueOf(12500, 2), LocalDateTime.now(),
                USER_JOHN.getShippingAddress(), Set.of(orderItem), false);
        orderItem.setOrder(order);

        OrderItemDto orderItemDto = createOrderItemDto(1L, orderItem.getBook().getId(),
                orderItem.getQuantity());
        OrderDto orderDto = new OrderDto(order.getId(), order.getUser().getId(),
                Set.of(orderItemDto), order.getOrderDate(), order.getTotal(), order.getStatus(),
                order.getShippingAddress());

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(USER_JOHN));
        when(orderMapper.toDto(any(Order.class)))
                .thenReturn(orderDto);
        when(orderRepository.getAllByUserId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(order));

        Pageable pageable = PageRequest.of(0, 10);
        List<OrderDto> actual = orderService.getAllOrders(USER_JOHN.getEmail(), pageable);
        List<OrderDto> expected = List.of(orderDto);
        assertEquals(expected, actual);
        verify(orderRepository).getAllByUserId(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("Get all order items with valid params")
    void getAllOrderItems_ValidParams_Success() {
        OrderItem orderItem = createOrderItem(1L, null, BOOK_1, 100,
                BOOK_1.getPrice(), false);
        Order order = createOrder(1L, USER_JOHN, Order.Status.PENDING,
                BigDecimal.valueOf(12500, 2), LocalDateTime.now(),
                USER_JOHN.getShippingAddress(), Set.of(orderItem), false);
        orderItem.setOrder(order);

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(USER_JOHN));
        when(orderRepository.findByUserIdAndId(anyLong(), anyLong()))
                .thenReturn(Optional.of(order));
        OrderItemDto orderItemDto = createOrderItemDto(1L, orderItem.getBook().getId(),
                orderItem.getQuantity());
        when(orderItemMapper.toDto(any(OrderItem.class))).thenReturn(orderItemDto);

        List<OrderItemDto> expected = List.of(orderItemDto);
        Pageable pageable = PageRequest.of(0, 10);
        List<OrderItemDto> actual =
                orderService.getAllOrderItems(USER_JOHN.getEmail(), order.getId(), pageable);
        assertEquals(expected, actual);
        verify(orderRepository).findByUserIdAndId(anyLong(), anyLong());
        verify(userRepository).findByEmail(anyString());
    }

    @Test
    @DisplayName("Update order status")
    void updateOrderStatus_ValidParams_Success() {
        OrderItem orderItem = createOrderItem(1L, null, BOOK_1, 100,
                BOOK_1.getPrice(), false);
        Order order = createOrder(1L, USER_JOHN, Order.Status.PENDING,
                BigDecimal.valueOf(12500, 2), LocalDateTime.now(),
                USER_JOHN.getShippingAddress(), Set.of(orderItem), false);
        orderItem.setOrder(order);

        OrderStatusDto orderStatusDto = new OrderStatusDto(Order.Status.DELIVERED);
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);
        OrderItemDto orderItemDto = createOrderItemDto(1L, orderItem.getBook().getId(),
                orderItem.getQuantity());
        OrderDto expected = new OrderDto(1L, USER_JOHN.getId(), Set.of(orderItemDto),
                order.getOrderDate(), order.getTotal(), order.getStatus(),
                order.getShippingAddress());
        when(orderMapper.toDto(any(Order.class)))
                .thenReturn(expected);

        OrderDto actual = orderService.updateOrderStatus(USER_JOHN.getEmail(), order.getId(),
                orderStatusDto);
        assertEquals(expected, actual);
        verify(orderRepository).findById(anyLong());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Get order item from order")
    void getSpecificOrderItem_ValidParams_Success() {
        OrderItem orderItem = createOrderItem(1L, null, BOOK_1, 100,
                BOOK_1.getPrice(), false);
        Order order = createOrder(1L, USER_JOHN, Order.Status.PENDING,
                BigDecimal.valueOf(12500, 2), LocalDateTime.now(),
                USER_JOHN.getShippingAddress(), Set.of(orderItem), false);
        orderItem.setOrder(order);
        OrderItemDto expected = createOrderItemDto(1L, orderItem.getBook().getId(),
                orderItem.getQuantity());

        when(orderRepository.findByUserIdAndId(anyLong(), anyLong()))
                .thenReturn(Optional.of(order));
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(USER_JOHN));
        when(orderItemMapper.toDto(any(OrderItem.class)))
                .thenReturn(expected);

        OrderItemDto actual = orderService.getSpecificOrderItem(USER_JOHN.getEmail(), order.getId(),
                orderItem.getId());
        assertEquals(expected, actual);
        verify(orderRepository).findByUserIdAndId(anyLong(), anyLong());
        verify(userRepository).findByEmail(anyString());
    }
}
