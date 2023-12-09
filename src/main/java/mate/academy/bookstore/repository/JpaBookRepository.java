package mate.academy.bookstore.repository;

import java.util.List;
import java.util.Optional;
import mate.academy.bookstore.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaBookRepository extends JpaRepository<Book, Long> {
    @Override
    <S extends Book> S save(S entity);

    @Override
    List<Book> findAll();

    @Override
    Optional<Book> findById(Long id);
}
