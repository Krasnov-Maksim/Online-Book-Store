package mate.academy.bookstore.repository.book;

import java.util.List;
import mate.academy.bookstore.model.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookRepository extends JpaRepository<Book, Long>,
        JpaSpecificationExecutor<Book> {
    List<Book> findAllByCategoryId(Long categoryId, Pageable pageable);
}
