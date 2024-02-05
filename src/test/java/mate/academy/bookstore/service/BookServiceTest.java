package mate.academy.bookstore.service;

import static mate.academy.bookstore.config.DatabaseHelper.BOOK_1;
import static mate.academy.bookstore.config.DatabaseHelper.BOOK_1_DTO;
import static mate.academy.bookstore.config.DatabaseHelper.BOOK_1_DTO_WITHOUT_CATEGORY_ID;
import static mate.academy.bookstore.config.DatabaseHelper.BOOK_2;
import static mate.academy.bookstore.config.DatabaseHelper.CREATE_BOOK_1_REQUEST_DTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.book.BookDtoWithoutCategoryId;
import mate.academy.bookstore.dto.book.BookSearchParametersDto;
import mate.academy.bookstore.dto.book.CreateBookRequestDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.BookMapper;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.repository.book.BookRepository;
import mate.academy.bookstore.repository.book.BookSpecificationBuilder;
import mate.academy.bookstore.service.impl.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    private static final Long INVALID_BOOK_ID = -1000L;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;
    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("Verify save() method. Correct book returns after saving")
    void save_ValidCreateBookRequestDto_ShouldSaveBook() {
        //Given
        BookDto expected = BOOK_1_DTO;
        when(bookMapper.toModel(CREATE_BOOK_1_REQUEST_DTO)).thenReturn(BOOK_1);
        when(bookRepository.save(BOOK_1)).thenReturn(BOOK_1);
        when(bookMapper.toDto(BOOK_1)).thenReturn(expected);
        //When
        BookDto actual = bookService.save(CREATE_BOOK_1_REQUEST_DTO);
        //Then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify findAll() displays all books")
    void findAll_ValidPageable_ShouldReturnAllBooks() {
        //Given
        Pageable pageable = PageRequest.of(0, 10);
        BookDto bookDto = BOOK_1_DTO;
        List<BookDto> expected = new ArrayList<>();
        expected.add(bookDto);
        Book book = BOOK_1;
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        //When
        List<BookDto> actual = bookService.findAll(pageable);
        //Then
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify findById() displays right book")
    void getBookById_ValidBookId_ShouldFindBook() {
        //Given
        BookDto expected = BOOK_1_DTO;
        Book book = BOOK_1;
        when(bookRepository.findById(BOOK_1.getId())).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expected);
        //When
        BookDto actual = bookService.getBookById(BOOK_1.getId());
        //Then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify findById() displays right message when there is no such book")
    void getBookById_InvalidBookId_ShouldThrowEntityNotFoundException() {
        //Given
        when(bookRepository.findById(INVALID_BOOK_ID))
                .thenThrow(new EntityNotFoundException("Can't find book by id " + INVALID_BOOK_ID));
        //When
        EntityNotFoundException entityNotFoundException = assertThrows(
                EntityNotFoundException.class, () -> bookService.getBookById(INVALID_BOOK_ID));
        //Then
        assertEquals("Can't find book by id " + INVALID_BOOK_ID,
                entityNotFoundException.getMessage());
        assertEquals(EntityNotFoundException.class, entityNotFoundException.getClass());
    }

    @Test
    @DisplayName("Verify deleteById() deletes book by id")
    void deleteById_ValidBookId_ShouldDeleteBook() {
        doNothing().when(bookRepository).deleteById(anyLong());
        bookService.deleteById(BOOK_1.getId());
        verify(bookRepository, times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName("Verify search() searches books by input parameters")
    void search_ValidSearchParameters_ShouldReturnListOfBooks() {
        //Given
        String[] authorParams = {BOOK_1.getAuthor(), BOOK_2.getAuthor()};
        String[] titleParams = {BOOK_1.getTitle(), BOOK_2.getTitle()};
        BookSearchParametersDto searchParameters =
                new BookSearchParametersDto(authorParams, titleParams);

        Specification<Book> specification = bookSpecificationBuilder.build(searchParameters);
        List<Book> mockBooks = new ArrayList<>();
        mockBooks.add(BOOK_1);
        mockBooks.add(BOOK_2);
        Pageable pageable = PageRequest.of(0, 10);
        when(bookRepository.findAll(specification, pageable))
                .thenReturn(new PageImpl<>(mockBooks));
        List<BookDto> expected = mockBooks.stream().map(bookMapper::toDto).toList();
        //When
        List<BookDto> actual = bookService.search(searchParameters, pageable);
        //Then
        assertEquals(2, actual.size());
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findAll(specification, pageable);
    }

    @Test
    @DisplayName("Verify update() updated books with valid ID and input parameters")
    void updateById_ValidIdAndRequestParameters_ShouldUpdateBook() {
        //Given
        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto("New name",
                "New Author", "978-92-95055-02-5", BigDecimal.valueOf(77700, 2),
                "New description to update the book", "new_image.jpg");
        Book updatedBook = new Book();
        updatedBook.setId(BOOK_1.getId());
        updatedBook.setTitle(createBookRequestDto.title());
        updatedBook.setAuthor(createBookRequestDto.author());
        updatedBook.setPrice(createBookRequestDto.price());
        updatedBook.setDescription(createBookRequestDto.description());
        updatedBook.setCoverImage(createBookRequestDto.coverImage());
        updatedBook.setIsbn(createBookRequestDto.isbn());
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(BOOK_1));
        when(bookMapper.toModel(any(CreateBookRequestDto.class))).thenReturn(updatedBook);
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);
        BookDto expected = bookMapper.toDto(updatedBook);
        //When
        BookDto actual = bookService.updateById(BOOK_1.getId(), createBookRequestDto);
        //Then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify update() throws exception for invalid ID")
    void updateById_InvalidId_ShouldThrowEntityNotFoundException() {
        //Given
        when(bookRepository.findById(INVALID_BOOK_ID))
                .thenThrow(new EntityNotFoundException("Can't find book by id " + INVALID_BOOK_ID));
        //When
        EntityNotFoundException entityNotFoundException = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.updateById(INVALID_BOOK_ID, CREATE_BOOK_1_REQUEST_DTO));
        //Then
        assertEquals("Can't find book by id " + INVALID_BOOK_ID,
                entityNotFoundException.getMessage());
        assertEquals(EntityNotFoundException.class, entityNotFoundException.getClass());
    }

    @Test
    void getBooksByCategoryId_validCategoryId_ShouldReturnListOfBooks() {
        //Given
        List<Book> bookList = new ArrayList<>();
        bookList.add(BOOK_1);
        List<BookDtoWithoutCategoryId> expected = List.of(BOOK_1_DTO_WITHOUT_CATEGORY_ID);
        when(bookRepository.findAllByCategoriesId(anyLong(), any(Pageable.class)))
                .thenReturn(bookList);
        when(bookMapper.toDtoWithoutCategories(any(Book.class))).thenReturn(expected.get(0));
        Pageable pageable = PageRequest.of(0, 10);
        //When
        List<BookDtoWithoutCategoryId> actual = bookService.getBooksByCategoryId(BOOK_1.getId(),
                pageable);
        //Then
        assertEquals(expected, actual);
    }
}
