package mate.academy.bookstore.repository;

import mate.academy.bookstore.model.Book;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationProvider<T> {
    String getKey();

    Specification<Book> getSpecification(String[] params);
}
