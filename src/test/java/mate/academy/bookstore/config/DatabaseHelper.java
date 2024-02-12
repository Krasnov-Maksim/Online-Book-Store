package mate.academy.bookstore.config;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.book.BookDtoWithoutCategoryId;
import mate.academy.bookstore.dto.book.CreateBookRequestDto;
import mate.academy.bookstore.dto.category.CategoryDto;
import mate.academy.bookstore.dto.category.CategoryDtoWithId;
import mate.academy.bookstore.dto.category.CreateCategoryRequestDto;
import mate.academy.bookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.bookstore.dto.user.UserResponseDto;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.Category;
import mate.academy.bookstore.model.Role;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.model.User;

public class DatabaseHelper {
    public static final Category CATEGORY_1;
    public static final Category CATEGORY_2;
    public static final Long INVALID_ID = -1000L;
    public static final Book BOOK_1;
    public static final Book BOOK_2;
    public static final User USER_JOHN;
    public static final User NEW_USER;
    public static final UserRegistrationRequestDto JOHN_REGISTRATION_REQUEST_DTO;
    public static final UserResponseDto JOHN_RESPONSE_DTO;
    public static final UserRegistrationRequestDto NEW_USER_REGISTRATION_REQUEST_DTO;
    public static final UserResponseDto NEW_USER_RESPONSE_DTO;
    public static final CreateBookRequestDto CREATE_BOOK_1_REQUEST_DTO;
    public static final BookDto BOOK_1_DTO;
    public static final BookDto BOOK_2_DTO;
    public static final CategoryDtoWithId CATEGORY_1_DTO_WITH_ID;
    public static final CategoryDto CATEGORY_1_DTO;
    public static final CreateCategoryRequestDto CREATE_CATEGORY_1_REQUEST_DTO;
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
    private static final String JOHN_PASSWORD = "12345678";
    private static final String JOHN_SHIPPING_ADDRESS = "John Shipping Address";
    private static final Long NEW_USER_ID = 5L;
    private static final String NEW_USER_EMAIL = "new_user@test.com";
    private static final String NEW_USER_FIRSTNAME = "New";
    private static final String NEW_USER_LASTNAME = "Doe";
    private static final String NEW_USER_PASSWORD = "new_user1234";
    private static final String NEW_USER_SHIPPING_ADDRESS = "New user Shipping Address";

    static {
        CATEGORY_1 = createCategory(CATEGORY_ID_1, CATEGORY_NAME_1, CATEGORY_DESCRIPTION_1);
        CATEGORY_2 = createCategory(CATEGORY_ID_2, CATEGORY_NAME_2, CATEGORY_DESCRIPTION_2);
        BOOK_1 = createBook(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1, BOOK_ISBN_1,
                BOOK_PRICE_1, BOOK_DESCRIPTION_1, BOOK_IMAGE_1, Set.of(CATEGORY_1));
        BOOK_2 = createBook(BOOK_ID_2, BOOK_TITLE_2, BOOK_AUTHOR_2, BOOK_ISBN_2,
                BOOK_PRICE_2, BOOK_DESCRIPTION_2, BOOK_IMAGE_2, Set.of(CATEGORY_1));
        Role userRole = new Role();
        userRole.setId(1L);
        userRole.setName(Role.RoleName.ROLE_USER);
        USER_JOHN = createUser(JOHN_ID, JOHN_EMAIL, JOHN_FIRSTNAME, JOHN_LASTNAME, JOHN_PASSWORD,
                JOHN_SHIPPING_ADDRESS, Set.of(userRole));
        Role adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setName(Role.RoleName.ROLE_ADMIN);
        NEW_USER = createUser(NEW_USER_ID, NEW_USER_EMAIL, NEW_USER_FIRSTNAME, NEW_USER_LASTNAME,
                NEW_USER_PASSWORD, NEW_USER_SHIPPING_ADDRESS, Set.of(userRole, adminRole));
        JOHN_REGISTRATION_REQUEST_DTO = createUserRegistrationRequestDto(USER_JOHN);
        NEW_USER_REGISTRATION_REQUEST_DTO = createUserRegistrationRequestDto(NEW_USER);
        JOHN_RESPONSE_DTO = createUserResponseDto(USER_JOHN);
        NEW_USER_RESPONSE_DTO = createUserResponseDto(NEW_USER);
        CREATE_BOOK_1_REQUEST_DTO = createBookRequestDto(BOOK_1);
        BOOK_1_DTO = createBookDto(BOOK_1);
        BOOK_2_DTO = createBookDto(BOOK_2);
        CREATE_CATEGORY_1_REQUEST_DTO = createCategoryRequestDto(CATEGORY_1);
        CATEGORY_1_DTO_WITH_ID = createCategoryDtoWithId(CATEGORY_1);
        CATEGORY_1_DTO = createCategoryDto(CATEGORY_1);
    }

    public static BookDtoWithoutCategoryId createBookDtoWithoutCategoryId(Book book) {
        return new BookDtoWithoutCategoryId(book.getTitle(), book.getAuthor(), book.getIsbn(),
                book.getPrice(), book.getDescription(), book.getCoverImage());
    }

    public static CartItem createCartItem(Long id, ShoppingCart shoppingCart, Book book,
                                          int quantity, boolean isDeleted) {
        CartItem cartItem = new CartItem();
        cartItem.setId(id);
        cartItem.setBook(book);
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setQuantity(quantity);
        cartItem.setDeleted(isDeleted);
        return cartItem;
    }

    public static ShoppingCart createShoppingCart(Long id, User user, Set<CartItem> cartItems,
                                                  boolean isDeleted) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(id);
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(cartItems);
        shoppingCart.setDeleted(isDeleted);
        return shoppingCart;
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

    private static CreateBookRequestDto createBookRequestDto(Book book) {
        return new CreateBookRequestDto(book.getTitle(), book.getAuthor(), book.getIsbn(),
                book.getPrice(), book.getDescription(), book.getCoverImage());
    }

    private static BookDto createBookDto(Book book) {
        Set<Long> categoriesIds = book.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
        return new BookDto(book.getTitle(), book.getAuthor(), book.getIsbn(), book.getPrice(),
                book.getDescription(), book.getCoverImage(), categoriesIds);
    }

    private static CategoryDtoWithId createCategoryDtoWithId(Category category) {
        return new CategoryDtoWithId(category.getId(), category.getName(),
                category.getDescription());
    }

    private static CategoryDto createCategoryDto(Category category) {
        return new CategoryDto(category.getName(), category.getDescription());
    }

    private static CreateCategoryRequestDto createCategoryRequestDto(Category category) {
        return new CreateCategoryRequestDto(category.getName(), category.getDescription());
    }
}
