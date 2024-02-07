package mate.academy.bookstore.controller;

import static mate.academy.bookstore.config.DatabaseHelper.BOOK_1;
import static mate.academy.bookstore.config.DatabaseHelper.BOOK_2;
import static mate.academy.bookstore.config.DatabaseHelper.CATEGORY_1;
import static mate.academy.bookstore.config.DatabaseHelper.CATEGORY_2;
import static mate.academy.bookstore.config.DatabaseHelper.INVALID_CATEGORY_ID;
import static mate.academy.bookstore.config.DatabaseHelper.createBookDtoWithoutCategoryId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.bookstore.dto.book.BookDtoWithoutCategoryId;
import mate.academy.bookstore.dto.category.CategoryDto;
import mate.academy.bookstore.dto.category.CategoryDtoWithId;
import mate.academy.bookstore.dto.category.CreateCategoryRequestDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.service.CategoryService;
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
class CategoryControllerIntegrationTest {
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CategoryService categoryService;
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
                    new ClassPathResource("sql/controller/category/remove-from-book_category.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("sql/controller/category/remove-from-books.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("sql/controller/category/remove-from-categories.sql"));
        }
    }

    @SneakyThrows
    private void setupDatabase(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("sql/controller/category"
                            + "/add-category-to-categories-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("sql/controller/category/add-books-to-books-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("sql/controller/category/add-category-to-book.sql")
            );
        }
    }

    @Test
    @DisplayName("Create a new Category")
    @WithMockUser(roles = {"ADMIN"})
    void createCategory_ValidCategoryDto_Success() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto("New Category",
                "New description");
        CategoryDto expected = new CategoryDto(requestDto.name(), requestDto.description());
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult mvcResult = mockMvc.perform(
                        post("/api/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        CategoryDto actual = objectMapper.readValue(jsonResponse, CategoryDto.class);
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Get all categories from DB")
    @WithMockUser(roles = "USER")
    void getAll_WithPagination_ShouldReturnPageWithCategories() throws Exception {
        List<CategoryDtoWithId> expected = new ArrayList<>();
        expected.add(new CategoryDtoWithId(CATEGORY_1.getId(), CATEGORY_1.getName(),
                CATEGORY_1.getDescription()));
        expected.add(new CategoryDtoWithId(CATEGORY_2.getId(), CATEGORY_2.getName(),
                CATEGORY_2.getDescription()));
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        MvcResult mvcResult = mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", String.valueOf(
                                pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize())))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        List<CategoryDtoWithId> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<>() {
                }
        );
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get category with valid Id from DB")
    @WithMockUser(roles = "USER")
    void getCategoryById_ValidId_ShouldReturnCategory() throws Exception {
        CategoryDto expected = new CategoryDto(CATEGORY_1.getName(), CATEGORY_1.getDescription());
        MvcResult mvcResult = mockMvc.perform(
                        get("/api/categories/{id}", CATEGORY_1.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        CategoryDto actual = objectMapper.readValue(jsonResponse, CategoryDto.class);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get category with invalid Id throws exception")
    @WithMockUser(roles = "USER")
    void getBookById_invalidId_ShouldReturnException() throws Exception {
        mockMvc.perform(get("/api/categories/{id}", INVALID_CATEGORY_ID))
                .andExpect(status().isNotFound())
                .andReturn();
        assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(INVALID_CATEGORY_ID));
    }

    @Test
    @DisplayName("Delete with valid Id will delete category")
    @WithMockUser(roles = {"ADMIN"})
    void delete_ValidId_Success() throws Exception {
        mockMvc.perform(delete("/api/categories/{id}", CATEGORY_1.getId()))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @DisplayName("Update category with valid parameters")
    @WithMockUser(roles = {"ADMIN"})
    void updateCategory_WithValidIdAndRequestDto_Success() throws Exception {
        CreateCategoryRequestDto updateRequestDto = new CreateCategoryRequestDto("Updated Name",
                "Updated description");
        CategoryDto expected = new CategoryDto("Updated Name", "Updated description");
        String jsonRequest = objectMapper.writeValueAsString(updateRequestDto);

        MvcResult mvcResult = mockMvc.perform(
                        put("/api/categories/{id}", CATEGORY_1.getId())
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        CategoryDto actual = objectMapper.readValue(jsonResponse, CategoryDto.class);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Search books by category id")
    @WithMockUser(roles = {"USER"})
    void getBooksByCategoryId_ValidId_success() throws Exception {
        List<BookDtoWithoutCategoryId> expected = new ArrayList<>();
        expected.add(createBookDtoWithoutCategoryId(BOOK_1));
        expected.add(createBookDtoWithoutCategoryId(BOOK_2));
        MvcResult mvcResult = mockMvc.perform(get("/api/categories/{id}/books",
                        CATEGORY_1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        List<BookDtoWithoutCategoryId> actual = objectMapper.readValue(jsonResponse,
                new TypeReference<>() {
                }
        );
        assertNotNull(actual);
        assertTrue(actual.contains(expected.get(0)));
        assertTrue(actual.contains(expected.get(1)));
    }
}
