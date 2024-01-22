package mate.academy.bookstore.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.order.CreateOrderRequestDto;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.order.OrderStatusDto;
import mate.academy.bookstore.dto.orderitem.OrderItemDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.OrderItemMapper;
import mate.academy.bookstore.mapper.OrderMapper;
import mate.academy.bookstore.model.Order;
import mate.academy.bookstore.model.OrderItem;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.model.User;
import mate.academy.bookstore.repository.order.OrderRepository;
import mate.academy.bookstore.repository.orderitem.OrderItemRepository;
import mate.academy.bookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.bookstore.repository.user.UserRepository;
import mate.academy.bookstore.service.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;

    @Override
    public OrderDto createOrder(String email, CreateOrderRequestDto createOrderRequestDto) {
        User user = getUserByEmail(email);
        ShoppingCart shoppingCart = shoppingCartRepository.getShoppingCartByUserId(user.getId());
        Order order = createOrder(user, createOrderRequestDto.shippingAddress(), shoppingCart);
        Order savedOrder = orderRepository.save(order);
        orderItemRepository.saveAll(savedOrder.getOrderItems());
        shoppingCart.getCartItems().clear();
        shoppingCartRepository.save(shoppingCart);
        return orderMapper.toDto(savedOrder);
    }

    private Order createOrder(User user, String shippingAddress, ShoppingCart shoppingCart) {
        double total = shoppingCart.getCartItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getBook().getPrice().doubleValue())
                .sum();
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.Status.PENDING);
        order.setTotal(BigDecimal.valueOf(total));
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(shippingAddress);
        Set<OrderItem> orderItems = shoppingCart.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setBook(cartItem.getBook());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getBook().getPrice());
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .collect(Collectors.toSet());
        order.setOrderItems(orderItems);
        return order;
    }

    @Override
    public List<OrderDto> getAllOrders(String email, Pageable pageable) {
        return orderRepository.getAllByUserId(getUserByEmail(email).getId(), pageable).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public List<OrderItemDto> getAllOrderItems(String email, Long orderId, Pageable pageable) {
        return getOrderByIdAndUserEmail(orderId, email)
                .getOrderItems()
                .stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemDto getSpecificOrderItem(String email, Long orderId, Long itemId) {
        return getOrderByIdAndUserEmail(orderId, email)
                .getOrderItems()
                .stream()
                .filter((orderItem) -> (orderItem.getId().equals(itemId)))
                .findFirst()
                .map(orderItemMapper::toDto).orElseThrow(
                        () -> new EntityNotFoundException("Can't find item by id: " + itemId
                                + " for order with id: " + orderId));
    }

    @Override
    public OrderDto updateOrderStatus(String email, Long orderId, OrderStatusDto statusDto) {
        // admin can search order just by id -> to get order for any user.
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Can't find order by id: " + orderId));
        order.setStatus(statusDto.status());
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDto(savedOrder);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("Can't find user by email: " + email));
    }

    private Order getOrderByIdAndUserEmail(Long orderId, String email) {
        Optional<Order> optionalWithOrder =
                orderRepository.findByUserIdAndId(getUserByEmail(email).getId(), orderId);
        return optionalWithOrder.orElseThrow(
                () -> new EntityNotFoundException("Can't find order by id: " + orderId
                        + " for current user."));
    }
}
