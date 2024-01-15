package mate.academy.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.book.BookDtoWithoutCategoryId;
import mate.academy.bookstore.model.Category;
import mate.academy.bookstore.service.BookService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Category management", description = "Endpoints for managing categories")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/categories")
public class CategoryController {
    private final BookService bookService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get all categories", description = "Get a list of all categories")
    @Parameter(name = "page", description = "page index, default value = 0")
    @Parameter(name = "size", description = "elements per page, default value = 20")
    @Parameter(name = "sort", description = "sort criteria", example = "sort=name,Desc")
    @GetMapping
    List<Category> getAll(Pageable pageable) {
        return null; // FIXME:
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{id}/books")
    @Operation(summary = "Get all books by category id",
            description = "Get all books which have specified category"
    )
    @Parameter(name = "id", description = "category id, default value = 0")
    public List<BookDtoWithoutCategoryId> getListOfBooksByCategoryId(@PathVariable Long id,
                                                                     Pageable pageable) {
        return bookService.getListOfBooksByCategoryId(id, pageable);
    }
}
