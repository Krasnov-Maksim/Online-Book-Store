package mate.academy.bookstore.controller;

import static mate.academy.bookstore.config.DatabaseHelper.NEW_USER_REGISTRATION_REQUEST_DTO;
import static mate.academy.bookstore.config.DatabaseHelper.NEW_USER_RESPONSE_DTO;
import static mate.academy.bookstore.config.DatabaseHelper.USER_JOHN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.bookstore.dto.user.UserLoginRequestDto;
import mate.academy.bookstore.dto.user.UserLoginResponseDto;
import mate.academy.bookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.bookstore.dto.user.UserResponseDto;
import mate.academy.bookstore.security.AuthenticationService;
import mate.academy.bookstore.security.JwtUtil;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerIntegrationTest {
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private JwtUtil jwtUtil;
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
        teardown(dataSource);
    }

    @BeforeEach
    public void setUp() {
        setupDatabase(dataSource);
    }

    @AfterEach
    public void afterEach() throws SQLException {
        teardown(dataSource);
    }

    private void teardown(DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/controller"
                    + "/authentication/remove-from-users_roles.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("sql/controller/authentication/remove-from-roles.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("sql/controller/authentication/remove-from-users.sql"));
        }
    }

    @SneakyThrows
    private void setupDatabase(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/controller"
                    + "/authentication/add-user-to-users-table.sql"));
        }
    }

    @Test
    @DisplayName("Register new user")
    void register_validRequest_Success() throws Exception {
        UserRegistrationRequestDto request = NEW_USER_REGISTRATION_REQUEST_DTO;
        String jsonRequest = objectMapper.writeValueAsString(request);
        MvcResult mvcResult = mockMvc.perform(post("/api/auth/registration")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        UserResponseDto expected = NEW_USER_RESPONSE_DTO;
        String jsonResponse = mvcResult.getResponse().getContentAsString();

        UserResponseDto actual = objectMapper.readValue(jsonResponse, UserResponseDto.class);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Login user with valid UserLoginRequestDto")
    void login_ValidUserLoginRequestDto_Success() throws Exception {
        UserLoginRequestDto loginRequestDto = new UserLoginRequestDto(USER_JOHN.getEmail(),
                USER_JOHN.getPassword());
        String expectedToken = authenticationService.authentication(loginRequestDto).token();
        String expectedEmail = jwtUtil.getUsername(expectedToken);
        String jsonRequest = objectMapper.writeValueAsString(loginRequestDto);
        MvcResult mvcResult = mockMvc.perform(post("/api/auth/login")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        String actualToken = objectMapper
                .readValue(jsonResponse, UserLoginResponseDto.class).token();
        String actualEmail = jwtUtil.getUsername(actualToken);
        assertEquals(expectedEmail, actualEmail);
    }
}
