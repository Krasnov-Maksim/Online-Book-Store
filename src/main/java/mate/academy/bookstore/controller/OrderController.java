package mate.academy.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.order.CreateOrderRequestDto;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.order.OrderStatusDto;
import mate.academy.bookstore.dto.orderitem.OrderItemDto;
import mate.academy.bookstore.service.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for managing orders")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create a new order", description = "Create a new order")
    @PreAuthorize("hasRole('ROLE_USER')")
    public OrderDto createOrder(Authentication authentication,
                                @RequestBody @Valid CreateOrderRequestDto shippingAddress) {
        return orderService.createOrder(authentication.getName(), shippingAddress);
    }

    @GetMapping
    @Operation(summary = "Get all orders", description = "Get all user orders")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<OrderDto> getAllOrders(Authentication authentication, Pageable pageable) {
        return orderService.getAllOrders(authentication.getName(), pageable);
    }

    @GetMapping("/{orderId}/items")
    @Operation(summary = "Get order items by order id",
            description = "Retrieve all items from specific order")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<OrderItemDto> getAllOrderItems(Authentication authentication,
                                               @PathVariable Long orderId, Pageable pageable) {
        return orderService.getAllOrderItems(authentication.getName(), orderId, pageable);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Get a specific item in order",
            description = "Get a specific item in order")
    @PreAuthorize("hasRole('ROLE_USER')")
    public OrderItemDto getSpecificOrderItem(Authentication authentication,
                                             @PathVariable Long orderId,
                                             @PathVariable Long itemId) {
        return orderService.getSpecificOrderItem(authentication.getName(), orderId, itemId);
    }

    @PutMapping("/{orderId}")
    @Operation(summary = "Update order status", description = "Update order status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public OrderDto updateOrderStatus(Authentication authentication,
                                      @PathVariable Long orderId,
                                      @RequestBody OrderStatusDto statusDto) {
        return orderService.updateOrderStatus(authentication.getName(), orderId, statusDto);
    }
}
