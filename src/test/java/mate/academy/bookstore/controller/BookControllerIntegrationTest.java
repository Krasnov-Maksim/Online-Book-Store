package mate.academy.bookstore.controller;

import static mate.academy.bookstore.config.DatabaseHelper.BOOK_1;
import static mate.academy.bookstore.config.DatabaseHelper.BOOK_1_DTO;
import static mate.academy.bookstore.config.DatabaseHelper.BOOK_2_DTO;
import static mate.academy.bookstore.config.DatabaseHelper.INVALID_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.book.CreateBookRequestDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.model.Category;
import mate.academy.bookstore.service.BookService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerIntegrationTest {
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BookService bookService;
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
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("sql/controller/book/remove-from-book_category.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("sql/controller/book/remove-from-books.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("sql/controller/book/remove-from-categories.sql"));
        }
    }

    @SneakyThrows
    private void setupDatabase(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("sql/controller/book"
                            + "/add-category-to-categories-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("sql/controller/book/add-books-to-books-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("sql/controller/book/add-category-to-book.sql")
            );
        }
    }

    @Test
    @DisplayName("Create a new Book")
    @WithMockUser(roles = {"ADMIN", "USER"})
    void createBook_ValidCreateBookRequestDto_Success() throws Exception {
        //Given
        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto(
                "New Title...",
                BOOK_1.getAuthor(),
                BOOK_1.getIsbn(),
                BOOK_1.getPrice(),
                BOOK_1.getDescription(),
                BOOK_1.getCoverImage()
        );
        BookDto expected = new BookDto(
                "New Title...",
                BOOK_1.getAuthor(),
                BOOK_1.getIsbn(),
                BOOK_1.getPrice(),
                BOOK_1.getDescription(),
                BOOK_1.getCoverImage(),
                Set.of()
        );
        String jsonRequest = objectMapper.writeValueAsString(createBookRequestDto);
        //When
        MvcResult mvcResult = mockMvc.perform(
                        post("/api/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        //Then
        BookDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                BookDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify getAll() represents all books from DB")
    @WithMockUser(roles = "USER")
    void getAll_WithPagination_ShouldReturnPageWithBooks() throws Exception {
        //Given
        List<BookDto> expected = new ArrayList<>();
        expected.add(BOOK_1_DTO);
        expected.add(BOOK_2_DTO);
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        //When
        MvcResult mvcResult = mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize())))
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        //Then
        List<BookDto> actual = objectMapper.readValue(jsonResponse,
                new TypeReference<>() {
                }
        );
        assertNotNull(actual);
        assertEquals(actual.get(0), expected.get(0));
        assertEquals(actual.get(0), expected.get(0));
    }

    @Test
    @DisplayName("Verify getBookById() with valid Id returns book from DB")
    @WithMockUser(roles = {"USER", "ADMIN"})
    void getBookById_ValidId_ShouldReturnBook() throws Exception {
        //Given
        BookDto expected = BOOK_1_DTO;
        // When
        MvcResult mvcResult = mockMvc.perform(
                        get("/api/books/{id}", BOOK_1.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        // Then
        BookDto actual = objectMapper.readValue(jsonResponse, BookDto.class);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify getBookById() with invalid Id throws exception")
    @WithMockUser(roles = {"USER", "ADMIN"})
    void getBookById_invalidId_shouldReturnException() throws Exception {
        mockMvc.perform(get("/api/books/{id}", INVALID_ID))
                .andExpect(status().isNotFound()).andReturn();
        assertThrows(EntityNotFoundException.class, () -> bookService.getBookById(INVALID_ID));
    }

    @Test
    @DisplayName("Verify delete() with valid will delete book")
    @WithMockUser(roles = "ADMIN")
    void delete_ValidId_Success() throws Exception {
        mockMvc.perform(delete("/api/books/{id}", BOOK_1.getId()))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @DisplayName("Verify search() with valid parameters will return book")
    @WithMockUser(roles = {"ADMIN", "USER"})
    void search_validSearchParameters_Success() throws Exception {
        List<BookDto> expected = new ArrayList<>();
        expected.add(BOOK_1_DTO);

        MvcResult mvcResult = mockMvc.perform(
                        get("/api/books/search?titles=Book 1&authors=Author 1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();

        List<BookDto> actual = objectMapper.readValue(jsonResponse,
                new TypeReference<>() {
                }
        );
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update book with valid parameters")
    @WithMockUser(roles = {"ADMIN", "USER"})
    void updateById_WithValidIdAndCreateBookRequestDto_Success() throws Exception {
        CreateBookRequestDto updateBookRequestDto = new CreateBookRequestDto(
                "Updated Title",
                "Updated Author",
                BOOK_1.getIsbn(),
                BigDecimal.valueOf(250),
                "Updated description",
                "updated_image.jpg");

        BookDto expected = new BookDto(
                updateBookRequestDto.title(),
                updateBookRequestDto.author(),
                updateBookRequestDto.isbn(),
                updateBookRequestDto.price(),
                updateBookRequestDto.description(),
                updateBookRequestDto.coverImage(),
                BOOK_1.getCategories().stream()
                        .map(Category::getId)
                        .collect(Collectors.toSet()));

        String jsonRequest = objectMapper.writeValueAsString(updateBookRequestDto);
        MvcResult mvcResult = mockMvc.perform(
                        put("/api/books/{id}", BOOK_1.getId())
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        BookDto actual = objectMapper.readValue(jsonResponse, BookDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}
