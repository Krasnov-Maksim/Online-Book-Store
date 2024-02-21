package mate.academy.bookstore.controller;

import static mate.academy.bookstore.config.DatabaseHelper.BOOK_1;
import static mate.academy.bookstore.config.DatabaseHelper.INVALID_ID;
import static mate.academy.bookstore.config.DatabaseHelper.USER_JOHN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.bookstore.dto.cartitem.CartItemDto;
import mate.academy.bookstore.dto.cartitem.CartItemQuantityDto;
import mate.academy.bookstore.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.bookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.bookstore.model.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
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
class ShoppingCartControllerIntegrationTest {
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private WebApplicationContext applicationContext;

    @BeforeAll
    public void beforeAll() throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown();
    }

    @BeforeEach
    public void beforeEach() {
        setupDatabase(dataSource);
    }

    @AfterEach
    public void afterEach() throws SQLException {
        teardown();
    }

    private void teardown() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("sql/controller/shoppingcart/remove-data-from-db.sql")
            );
        }
    }

    @SneakyThrows
    private void setupDatabase(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("sql/controller/shoppingcart/add-data-to-db.sql")
            );
        }
    }

    @Test
    @DisplayName("Add item to shopping cart")
    void addItemToShoppingCart_ValidCreateBookRequestDto_Success() throws Exception {
        //Given
        CartItemDto cartItemDto = new CartItemDto(1L, BOOK_1.getId(), BOOK_1.getTitle(), 105);
        ShoppingCartDto expected = new ShoppingCartDto(1L, USER_JOHN.getId(), Set.of(cartItemDto));
        CreateCartItemRequestDto createCartItemRequestDto =
                new CreateCartItemRequestDto(BOOK_1.getId(), 100);
        String jsonRequest = objectMapper.writeValueAsString(createCartItemRequestDto);
        //When
        MvcResult mvcResult = mockMvc.perform(
                        post("/api/cart")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(user(USER_JOHN.getEmail()).password(USER_JOHN.getPassword())
                                        .roles(Role.RoleName.ROLE_USER.getShortName())
                                )
                )
                .andExpect(status().isOk())
                .andReturn();
        //Then
        ShoppingCartDto actual = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(), ShoppingCartDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Add item to shopping cart by wrong user name")
    void addItemToShoppingCart_InvalidAuthentication_NotFound() throws Exception {
        CreateCartItemRequestDto createCartItemRequestDto =
                new CreateCartItemRequestDto(BOOK_1.getId(), 100);
        String jsonRequest = objectMapper.writeValueAsString(createCartItemRequestDto);
        mockMvc.perform(
                        post("/api/cart")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(user("wrong-user-name")
                                        .roles(Role.RoleName.ROLE_USER.getShortName())
                                )
                )
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @DisplayName("Delete item by id from shopping cart")
    void deleteItemFromShoppingCart_ValidParams_Success() throws Exception {
        mockMvc.perform(
                        delete("/api/cart/cart-items/{cartItemId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(user(USER_JOHN.getEmail()).password(USER_JOHN.getPassword())
                                        .roles(Role.RoleName.ROLE_USER.getShortName())
                                )
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("Update item quantity by id")
    void updateItemQuantity_ValidParams_Success() throws Exception {
        Integer newQuantity = 77;
        CartItemQuantityDto cartItemQuantityDto = new CartItemQuantityDto(newQuantity);
        String jsonRequest = objectMapper.writeValueAsString(cartItemQuantityDto);
        CartItemDto expected = new CartItemDto(1L, BOOK_1.getId(), BOOK_1.getTitle(), newQuantity);
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/cart/cart-items/{cartItemId}", 1L)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(user(USER_JOHN.getEmail()).password(USER_JOHN.getPassword())
                                        .roles(Role.RoleName.ROLE_USER.getShortName())
                                )
                )
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        CartItemDto actual = objectMapper.readValue(jsonResponse, CartItemDto.class);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update by invalid item id, item not found")
    void updateItemQuantity_InvalidCartItemId_ItemNotFound() throws Exception {
        CartItemQuantityDto cartItemQuantityDto = new CartItemQuantityDto(77);
        String jsonRequest = objectMapper.writeValueAsString(cartItemQuantityDto);
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/cart/cart-items/{cartItemId}", INVALID_ID)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(user(USER_JOHN.getEmail()).password(USER_JOHN.getPassword())
                                        .roles(Role.RoleName.ROLE_USER.getShortName())
                                )
                )
                .andExpect(status().isNotFound())
                .andReturn();
    }
}
