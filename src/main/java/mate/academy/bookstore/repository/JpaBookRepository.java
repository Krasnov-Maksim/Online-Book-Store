package mate.academy.bookstore.repository;

import mate.academy.bookstore.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JpaBookRepository extends JpaRepository<Book, Long>,
        JpaSpecificationExecutor<Book> {
}
