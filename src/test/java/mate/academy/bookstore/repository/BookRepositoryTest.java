package mate.academy.bookstore.repository;

import static mate.academy.bookstore.config.DatabaseHelper.BOOK_1;
import static mate.academy.bookstore.config.DatabaseHelper.CATEGORY_1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import jakarta.persistence.criteria.Predicate;
import java.util.List;
import java.util.Optional;
import mate.academy.bookstore.dto.book.BookSearchParametersDto;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.repository.book.BookRepository;
import mate.academy.bookstore.repository.book.BookSpecificationBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {
    private static final Long EXPECTED_LIST_SIZE = 3L;
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 3;
    @Autowired
    private BookRepository bookRepository;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;

    @Test
    @DisplayName("Find all books by valid category id")
    @Sql(scripts = {
            "classpath:sql/repository/book/before/add-books-to-books-table.sql",
            "classpath:sql/repository/book/before/add-category-to-categories-table.sql",
            "classpath:sql/repository/book/before/add-category-to-book.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:sql/repository/book/after/remove-from-book_category.sql",
            "classpath:sql/repository/book/after/remove-from-books.sql",
            "classpath:sql/repository/book/after/remove-from-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoryId_ValidCategoryId_ShouldReturnBooksByCategory() {
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        List<Book> actual = bookRepository
                .findAllByCategoriesId(CATEGORY_1.getId(), pageable);
        assertFalse(actual.isEmpty());
        assertEquals(EXPECTED_LIST_SIZE, actual.size());
        assertEquals("Book 1", actual.get(0).getTitle());
        assertEquals("Author 1", actual.get(0).getAuthor());
    }

    @Test
    @DisplayName("Find all books with pagination")
    @Sql(scripts = {
            "classpath:sql/repository/book/before/add-books-to-books-table.sql",
            "classpath:sql/repository/book/before/add-category-to-categories-table.sql",
            "classpath:sql/repository/book/before/add-category-to-book.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:sql/repository/book/after/remove-from-book_category.sql",
            "classpath:sql/repository/book/after/remove-from-books.sql",
            "classpath:sql/repository/book/after/remove-from-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_WithPagination_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Page<Book> actual = bookRepository.findAll(pageable);
        assertFalse(actual.isEmpty());
        assertEquals(PAGE_SIZE, actual.getContent().size());
    }

    @Test
    @DisplayName("Find book by valid id")
    @Sql(scripts = {
            "classpath:sql/repository/book/before/add-books-to-books-table.sql",
            "classpath:sql/repository/book/before/add-category-to-categories-table.sql",
            "classpath:sql/repository/book/before/add-category-to-book.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:sql/repository/book/after/remove-from-book_category.sql",
            "classpath:sql/repository/book/after/remove-from-books.sql",
            "classpath:sql/repository/book/after/remove-from-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findById_ValidId_ShouldReturnBook() {
        Book expected = BOOK_1;
        Optional<Book> actual = bookRepository.findById(BOOK_1.getId());
        assertFalse(actual.isEmpty());
        assertEquals(expected, actual.get());
    }

    @Test
    @DisplayName("Find all books by search parameters")
    @Sql(scripts = {
            "classpath:sql/repository/book/before/add-books-to-books-table.sql",
            "classpath:sql/repository/book/before/add-category-to-categories-table.sql",
            "classpath:sql/repository/book/before/add-category-to-book.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:sql/repository/book/after/remove-from-book_category.sql",
            "classpath:sql/repository/book/after/remove-from-books.sql",
            "classpath:sql/repository/book/after/remove-from-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_WithSpecification_Success() {
        BookSearchParametersDto bookSearchParameters = new BookSearchParametersDto(
                new String[]{"Author 1"}, new String[]{"%Book%"});
        Specification<Book> spec = (root, query, builder) -> {
            Predicate predicate = builder.conjunction();
            predicate = builder.and(predicate, builder.equal(root.get("author"), "Author 1"));
            predicate = builder.and(predicate, builder.like(root.get("title"), "%Book%"));
            return predicate;
        };
        when(bookSpecificationBuilder.build(bookSearchParameters)).thenReturn(spec);
        List<Book> expected = List.of(BOOK_1);
        List<Book> actual = bookRepository.findAll(spec);
        assertEquals(expected, actual);
    }
}
