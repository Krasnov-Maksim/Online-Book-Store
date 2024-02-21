package mate.academy.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.book.BookSearchParametersDto;
import mate.academy.bookstore.dto.book.CreateBookRequestDto;
import mate.academy.bookstore.service.BookService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/books")
@Tag(name = "Book management", description = "Books Management Endpoints")
public class BookController {
    private final BookService bookService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get all books", description = "Get a list of all books")
    @Parameter(name = "page", description = "page index, default value = 0")
    @Parameter(name = "size", description = "elements per page, default value = 20")
    @Parameter(name = "sort", description = "sort criteria", example = "title,Desc")
    @GetMapping
    public List<BookDto> getAll(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get a book by id", description = "Get a book with specified id")
    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new book", description = "Create a new book")
    @PostMapping
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto bookDto) {
        return bookService.save(bookDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update a book by id", description = "Update a book with specified id")
    @PutMapping("/{id}")
    public BookDto updateById(@PathVariable Long id,
                              @RequestBody @Valid CreateBookRequestDto requestDto) {
        return bookService.updateById(id, requestDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a book by id",
            description = "Delete book with specified id")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bookService.deleteById(id);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Search book for specific parameters",
            description = "Get a list of books. Available search params: 'authors', 'titles'.")
    @GetMapping("/search")
    public List<BookDto> searchBooks(BookSearchParametersDto searchParameters, Pageable pageable) {
        return bookService.search(searchParameters, pageable);
    }
}
