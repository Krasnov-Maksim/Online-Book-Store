package mate.academy.bookstore.repository.book;

import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.book.BookSearchParametersDto;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.repository.SpecificationBuilder;
import mate.academy.bookstore.repository.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder
        implements SpecificationBuilder<Book, BookSearchParametersDto> {
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParameters) {
        Specification<Book> specification = Specification.where(null);
        if (searchParameters.authors() != null && searchParameters.authors().length > 0) {
            specification = specification
                    .and(bookSpecificationProviderManager.getSpecificationProvider("author")
                            .getSpecification(searchParameters.authors()));
        }
        if (searchParameters.titles() != null && searchParameters.titles().length > 0) {
            specification = specification
                    .and(bookSpecificationProviderManager.getSpecificationProvider("title")
                            .getSpecification(searchParameters.titles()));
        }
        return specification;
    }
}
