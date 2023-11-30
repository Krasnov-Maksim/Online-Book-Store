package mate.academy.bookstore;

import java.math.BigDecimal;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookStoreApplication {

    @Autowired(required = true)
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(BookStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book book = new Book();
            book.setAuthor("The Best Author ");
            book.setCoverImage("image.png");
            book.setDescription("book description..");
            book.setIsbn("uwrt-12345");
            book.setPrice(BigDecimal.valueOf(100.5));
            book.setTitle("Book Title");
            bookService.save(book);
            System.out.println("\n" + book);
        };
    }
}
