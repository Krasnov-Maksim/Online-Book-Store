package mate.academy.bookstore;

import java.math.BigDecimal;
import mate.academy.bookstore.dto.BookDto;
import mate.academy.bookstore.dto.CreateBookRequestDto;
import mate.academy.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookStoreApplication {

    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(BookStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto(
                    "Book Title", "The Best Author", "uwrt-12345",
                    BigDecimal.valueOf(100.5), "book description..", "image.png");
            BookDto bookDto = bookService.save(createBookRequestDto);
            System.out.println("\n" + bookDto);
        };
    }

}
