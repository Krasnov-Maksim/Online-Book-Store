package mate.academy.bookstore.controller;

import static mate.academy.bookstore.config.DatabaseHelper.BOOK_1;
import static mate.academy.bookstore.config.DatabaseHelper.BOOK_2;
import static mate.academy.bookstore.config.DatabaseHelper.USER_JOHN;
import static mate.academy.bookstore.config.DatabaseHelper.createOrderItemDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.bookstore.dto.order.CreateOrderRequestDto;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.order.OrderStatusDto;
import mate.academy.bookstore.dto.orderitem.OrderItemDto;
import mate.academy.bookstore.model.Order;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerIntegrationTest {
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private WebApplicationContext applicationContext;

    @BeforeAll
    public void beforeAll() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
    }

    @BeforeEach
    public void setUp() {
        setupDatabase(dataSource);
    }

    @AfterEach
    public void afterEach() {
        teardown(dataSource);
    }

    @SneakyThrows
    private void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("sql/controller/order/remove-data-from-db.sql")
            );
        }
    }

    @SneakyThrows
    private void setupDatabase(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("sql/controller/order/add-data-to-db.sql")
            );
        }
    }

    @Test
    @DisplayName("Create order for user")
    void createOrder_ValidParams_Success() throws Exception {
        CreateOrderRequestDto createOrderRequestDto =
                new CreateOrderRequestDto(USER_JOHN.getShippingAddress());

        String jsonRequest = objectMapper.writeValueAsString(createOrderRequestDto);
        MvcResult mvcResult = mockMvc.perform(post("/api/orders")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(USER_JOHN.getEmail()).password(USER_JOHN.getPassword())
                                .roles("USER")
                        )
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        OrderItemDto orderItemDto = createOrderItemDto(1L, BOOK_1.getId(), 5);
        OrderDto expected = new OrderDto(3L, USER_JOHN.getId(), Set.of(orderItemDto),
                LocalDateTime.now(), BigDecimal.valueOf(500.0), Order.Status.PENDING,
                USER_JOHN.getShippingAddress());
        OrderDto actual = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(), OrderDto.class);
        assertNotNull(actual);
        assertEquals(expected.userId(), actual.userId());
        assertEquals(expected.status(), actual.status());
        assertEquals(expected.total(), actual.total());
        assertEquals(expected.shippingAddress(), actual.shippingAddress());
        assertEquals(expected.orderItems().size(), actual.orderItems().size());
    }

    @Test
    @DisplayName("Create order with wrong user name")
    void createOrder_InvalidAuthentication_NotFound() throws Exception {
        CreateOrderRequestDto createOrderRequestDto =
                new CreateOrderRequestDto(USER_JOHN.getShippingAddress());

        String jsonRequest = objectMapper.writeValueAsString(createOrderRequestDto);
        MvcResult mvcResult = mockMvc.perform(post("/api/orders")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("wrong-user-name").password(USER_JOHN.getPassword())
                                .roles("USER")
                        )
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();
    }

    @Test
    @DisplayName("Get all orders for user")
    void getAllOrders_ValidAuthentication_Success() throws Exception {
        OrderItemDto orderItemDto1 = createOrderItemDto(1L, BOOK_1.getId(), 1);
        OrderItemDto orderItemDto2 = createOrderItemDto(2L, BOOK_2.getId(), 2);
        OrderDto orderDto = new OrderDto(1L, USER_JOHN.getId(), Set.of(orderItemDto1,
                orderItemDto2), LocalDateTime.now(), BigDecimal.valueOf(50000, 2),
                Order.Status.PROCESSING, USER_JOHN.getShippingAddress());

        List<OrderDto> expected = List.of(orderDto);
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(user(USER_JOHN.getEmail()).password(USER_JOHN.getPassword())
                                        .roles("USER")
                                )
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();

        List<OrderDto> actual = objectMapper.readValue(jsonResponse,
                new TypeReference<>() {
                }
        );
        assertNotNull(actual);
        assertEquals(expected.get(0).userId(), actual.get(0).userId());
        assertEquals(expected.get(0).status(), actual.get(0).status());
        assertEquals(expected.get(0).total(), actual.get(0).total());
        assertEquals(expected.get(0).shippingAddress(), actual.get(0).shippingAddress());
        assertEquals(expected.get(0).orderItems().size(), actual.get(0).orderItems().size());
    }

    @Test
    @DisplayName("Get all items by order id")
    void getAllOrderItems_ValidParams_Success() throws Exception {
        OrderItemDto orderItemDto1 = createOrderItemDto(1L, BOOK_1.getId(), 1);
        OrderItemDto orderItemDto2 = createOrderItemDto(2L, BOOK_2.getId(), 2);
        List<OrderItemDto> expected = List.of(orderItemDto1, orderItemDto2);
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/orders/{orderId}/items", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", String.valueOf(pageable.getPageNumber()))
                                .param("size", String.valueOf(pageable.getPageSize()))
                                .with(user(USER_JOHN.getEmail()).password(USER_JOHN.getPassword())
                                        .roles("USER")
                                )
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();

        List<OrderItemDto> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<>() {
                }
        );
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get specific order item by order id and item id")
    void getSpecificOrderItem_ValidParams_Success() throws Exception {
        OrderItemDto expected = createOrderItemDto(1L, BOOK_1.getId(), 1);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/orders/{orderId}/items/{itemId}", 1L, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(user(USER_JOHN.getEmail()).password(USER_JOHN.getPassword())
                                        .roles("USER")
                                )
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        OrderItemDto actual = objectMapper.readValue(jsonResponse, OrderItemDto.class);
        assertEquals(expected, actual);
    }

    @Test
    void updateOrderStatus_ValidParams_Success() throws Exception {
        OrderItemDto orderItemDto1 = createOrderItemDto(1L, BOOK_1.getId(), 1);
        OrderItemDto orderItemDto2 = createOrderItemDto(2L, BOOK_2.getId(), 2);
        OrderDto expected = new OrderDto(1L, USER_JOHN.getId(), Set.of(orderItemDto1,
                orderItemDto2), LocalDateTime.now(), BigDecimal.valueOf(50000, 2),
                Order.Status.COMPLETED, USER_JOHN.getShippingAddress());

        OrderStatusDto orderStatusDto = new OrderStatusDto(Order.Status.COMPLETED);
        String jsonRequest = objectMapper.writeValueAsString(orderStatusDto);
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/orders/{orderId}", 1L)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(user(USER_JOHN.getEmail()).password(USER_JOHN.getPassword())
                                        .roles("ADMIN")
                                )
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        OrderDto actual = objectMapper.readValue(jsonResponse, OrderDto.class);
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("orderDate")
                .isEqualTo(expected);
    }
}
