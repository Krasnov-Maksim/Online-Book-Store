package mate.academy.bookstore.config;

import java.math.BigDecimal;
import java.util.Set;
import mate.academy.bookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.bookstore.dto.user.UserResponseDto;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.model.Category;
import mate.academy.bookstore.model.Role;
import mate.academy.bookstore.model.User;

public class DatabaseHelper {
    public static final Category CATEGORY_1;
    public static final Category CATEGORY_2;
    public static final Book BOOK_1;
    public static final Book BOOK_2;
    public static final User USER_JOHN;
    public static final UserRegistrationRequestDto JOHN_REGISTRATION_REQUEST_DTO;
    public static final UserResponseDto JOHN_RESPONSE_DTO;
    private static final Long BOOK_ID_1 = 1L;
    private static final String BOOK_AUTHOR_1 = "Author 1";
    private static final String BOOK_TITLE_1 = "Book 1";
    private static final String BOOK_ISBN_1 = "978-3-16-148410-0";
    private static final String BOOK_DESCRIPTION_1 = "Description for Book 1";
    private static final String BOOK_IMAGE_1 = "image1.jpg";
    private static final BigDecimal BOOK_PRICE_1 = BigDecimal.valueOf(10000, 2);
    private static final Long BOOK_ID_2 = 2L;
    private static final String BOOK_AUTHOR_2 = "Author 2";
    private static final String BOOK_TITLE_2 = "Book 2";
    private static final String BOOK_ISBN_2 = "978-1-4028-9462-6";
    private static final String BOOK_DESCRIPTION_2 = "Description for Book 2";
    private static final String BOOK_IMAGE_2 = "image2.jpg";
    private static final BigDecimal BOOK_PRICE_2 = BigDecimal.valueOf(20000, 2);
    private static final Long CATEGORY_ID_1 = 1L;
    private static final String CATEGORY_NAME_1 = "Category 1";
    private static final String CATEGORY_DESCRIPTION_1 = "Category 1 description";
    private static final Long CATEGORY_ID_2 = 2L;
    private static final String CATEGORY_NAME_2 = "Category 2";
    private static final String CATEGORY_DESCRIPTION_2 = "Category 2 description";
    private static final Long JOHN_ID = 1L;
    private static final String JOHN_EMAIL = "john@test.com";
    private static final String JOHN_FIRSTNAME = "John";
    private static final String JOHN_LASTNAME = "Doe";
    private static final String JOHN_PASSWORD = "john1234";
    private static final String JOHN_SHIPPING_ADDRESS = "John Shipping Address";

    static {
        CATEGORY_1 = createCategory(CATEGORY_ID_1, CATEGORY_NAME_1, CATEGORY_DESCRIPTION_1);
        CATEGORY_2 = createCategory(CATEGORY_ID_2, CATEGORY_NAME_2, CATEGORY_DESCRIPTION_2);
        BOOK_1 = createBook(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1, BOOK_ISBN_1,
                BOOK_PRICE_1, BOOK_DESCRIPTION_1, BOOK_IMAGE_1, Set.of(CATEGORY_1));
        BOOK_2 = createBook(BOOK_ID_2, BOOK_TITLE_2, BOOK_AUTHOR_2, BOOK_ISBN_2,
                BOOK_PRICE_2, BOOK_DESCRIPTION_2, BOOK_IMAGE_2, Set.of(CATEGORY_1));
        Role johnRole = new Role();
        johnRole.setName(Role.RoleName.ROLE_USER);
        USER_JOHN = createUser(JOHN_ID, JOHN_EMAIL, JOHN_FIRSTNAME, JOHN_LASTNAME, JOHN_PASSWORD,
                JOHN_SHIPPING_ADDRESS, Set.of(johnRole));
        JOHN_REGISTRATION_REQUEST_DTO = createUserRegistrationRequestDto(USER_JOHN);
        JOHN_RESPONSE_DTO = createUserResponseDto(USER_JOHN);
    }

    private static User createUser(Long userId, String userEmail, String userFirstname,
                                   String userLastname, String userPassword,
                                   String shippingAddress, Set<Role> roles) {
        User user = new User();
        user.setId(userId);
        user.setEmail(userEmail);
        user.setFirstName(userFirstname);
        user.setLastName(userLastname);
        user.setPassword(userPassword);
        user.setShippingAddress(shippingAddress);
        user.setRoles(roles);
        return user;
    }

    private static Book createBook(Long id, String title, String author, String isbn,
                                   BigDecimal price, String description, String coverImage,
                                   Set<Category> categories) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setPrice(price);
        book.setDescription(description);
        book.setCoverImage(coverImage);
        book.setCategories(categories);
        return book;
    }

    private static Category createCategory(Long id, String name, String description) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setDescription(description);
        return category;
    }

    private static UserRegistrationRequestDto createUserRegistrationRequestDto(User user) {
        return new UserRegistrationRequestDto(user.getEmail(), user.getPassword(),
                user.getPassword(), user.getFirstName(), user.getLastName(),
                user.getShippingAddress());
    }

    private static UserResponseDto createUserResponseDto(User user) {
        return new UserResponseDto(user.getId(), user.getEmail(), user.getFirstName(),
                user.getLastName(), user.getShippingAddress());
    }
}
