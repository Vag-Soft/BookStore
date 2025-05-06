package com.vagsoft.bookstore.unit.services;

import com.vagsoft.bookstore.dto.BookReadDTO;
import com.vagsoft.bookstore.dto.BookUpdateDTO;
import com.vagsoft.bookstore.dto.BookWriteDTO;
import com.vagsoft.bookstore.errors.exceptions.BookNotFoundException;
import com.vagsoft.bookstore.mappers.BookMapper;
import com.vagsoft.bookstore.models.Book;
import com.vagsoft.bookstore.models.Genre;
import com.vagsoft.bookstore.repositories.BookRepository;
import com.vagsoft.bookstore.services.BookService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ActiveProfiles("test")
class BookServiceTest {
    @MockitoBean
    private BookRepository bookRepository;
    @Autowired
    private BookService bookService;
    @Autowired
    private BookMapper bookMapper;

    private List<Book> storedBooks;
    @BeforeEach
    void setUp() {
        storedBooks = new ArrayList<>();
        storedBooks.add(new Book(1, "title", "author", "description", 1, 1.0, 1, "isbn", new ArrayList<>()));
        storedBooks.add(new Book(2, "title2", "author2", "description2", 2, 2.0, 2, "isbn2", List.of(new Genre(1, "genre1"), new Genre(2, "genre2"))));
    }

    @Test
    @DisplayName("getBooks() - Success No Filters")
    void getBooksNoFilters() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Book> page = new PageImpl<>(storedBooks, pageable, 2);

        when(bookRepository.findBooks(null, null, null, null, null, null, pageable))
                .thenReturn(page);

        Page<BookReadDTO> result = bookService.getBooks(null, null, null, null, null, null, pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(bookMapper.BookToReadDto(storedBooks.get(0)), result.getContent().get(0));
        assertEquals(bookMapper.BookToReadDto(storedBooks.get(1)), result.getContent().get(1));
    }

    @Test
    @DisplayName("getBooks() - Success With Filters")
    void getBooksWithFilters() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Book> page = new PageImpl<>(List.of(storedBooks.getLast()), pageable, 2);

        when(bookRepository.findBooks("title", "genre1", "author", "description2", 1.0, 2.0, pageable))
                .thenReturn(page);

        Page<BookReadDTO> result = bookService.getBooks("title", "genre1", "author", "description2", 1.0, 2.0, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(bookMapper.BookToReadDto(storedBooks.get(1)), result.getContent().getFirst());
    }

    @Test
    @DisplayName("addBook() - Success")
    void addBook() {
        // Create a BookCreateDTO object with some sample data
        BookWriteDTO bookToAdd = new BookWriteDTO();
        bookToAdd.setTitle("New Title");
        bookToAdd.setAuthor("New Author");
        bookToAdd.setPrice(10.0);

        Book addedBook = new Book();
        addedBook.setId(3);
        addedBook.setTitle("New Title");
        addedBook.setAuthor("New Author");
        addedBook.setPrice(10.0);

        when(bookRepository.save(bookMapper.DtoToBook(bookToAdd))).thenReturn(addedBook);

        Optional<BookReadDTO> result = bookService.addBook(bookToAdd);

        assertFalse(result.isEmpty());
        assertEquals(addedBook.getId(), result.get().getId());
        assertEquals(addedBook.getTitle(), result.get().getTitle());
        assertEquals(addedBook.getAuthor(), result.get().getAuthor());
        assertEquals(addedBook.getDescription(), result.get().getDescription());
        assertEquals(addedBook.getPrice(), result.get().getPrice());
        assertEquals(addedBook.getAvailability(), result.get().getAvailability());
        assertEquals(addedBook.getIsbn(), result.get().getIsbn());
        assertTrue(result.get().getGenres().isEmpty());
    }

    @Test
    @DisplayName("getBookByID(1) - Success")
    void getBookByIDFound() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(storedBooks.getFirst()));

        Optional<BookReadDTO> result = bookService.getBookByID(1);

        assertFalse(result.isEmpty());
        assertEquals(storedBooks.getFirst().getId(), result.get().getId());
        assertEquals(storedBooks.getFirst().getTitle(), result.get().getTitle());
        assertEquals(storedBooks.getFirst().getAuthor(), result.get().getAuthor());
        assertEquals(storedBooks.getFirst().getDescription(), result.get().getDescription());
        assertEquals(storedBooks.getFirst().getPrice(), result.get().getPrice());
        assertEquals(storedBooks.getFirst().getAvailability(), result.get().getAvailability());
        assertEquals(storedBooks.getFirst().getIsbn(), result.get().getIsbn());
        assertTrue(result.get().getGenres().isEmpty());
    }

    @Test
    @DisplayName("getBookByID(999) - Not Found")
    void getBookByIDNotFound() {
        when(bookRepository.findById(999)).thenReturn(Optional.empty());

        Optional<BookReadDTO> result = bookService.getBookByID(999);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getBookByID(-1) - Invalid ID")
    void getBookByIDInvalid() {
        Optional<BookReadDTO> result = bookService.getBookByID(-1);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("updateBookByID(1) - Success")
    void updateBookByIDFound() {
        BookUpdateDTO updateBookDTO = new BookUpdateDTO();
        updateBookDTO.setTitle("New Title");

        when(bookRepository.findById(1)).thenReturn(Optional.of(storedBooks.getFirst()));

        Book updatedBook = storedBooks.getFirst();
        updatedBook.setTitle(updateBookDTO.getTitle());
        when(bookRepository.save(updatedBook)).thenReturn(updatedBook);

        Optional<BookReadDTO> result = bookService.updateBookByID(1, updateBookDTO);

        assertFalse(result.isEmpty());
        assertEquals(storedBooks.getFirst().getId(), result.get().getId());
        assertEquals(storedBooks.getFirst().getTitle(), result.get().getTitle());
        assertEquals(storedBooks.getFirst().getAuthor(), result.get().getAuthor());
        assertEquals(storedBooks.getFirst().getDescription(), result.get().getDescription());
        assertEquals(storedBooks.getFirst().getPrice(), result.get().getPrice());
        assertEquals(storedBooks.getFirst().getAvailability(), result.get().getAvailability());
        assertEquals(storedBooks.getFirst().getIsbn(), result.get().getIsbn());
        assertTrue(result.get().getGenres().isEmpty());
    }

    @Test
    @DisplayName("updateBookByID(999) - Not Found")
    void updateBookByIDNotFound() {
        BookUpdateDTO updateBookDTO = new BookUpdateDTO();
        updateBookDTO.setTitle("New Title");

        assertThrows(BookNotFoundException.class, () -> bookService.updateBookByID(999, updateBookDTO));
    }

    @Test
    @DisplayName("updateBookByID(-1) - Invalid ID")
    void updateBookByIDInvalid() {
        BookUpdateDTO updateBookDTO = new BookUpdateDTO();
        updateBookDTO.setTitle("New Title");

        assertThrows(BookNotFoundException.class, () -> bookService.updateBookByID(-1, updateBookDTO));
    }

    @Test
    @DisplayName("deletedBookByID(1) - Success")
    void deleteBookByIDFound() {
        when(bookRepository.deleteById(1L)).thenReturn(1L);

        Long result = bookService.deleteBookByID(1L);

        assertEquals(1, result);
    }

    @Test
    @DisplayName("deletedBookByID(999) - Not Found")
    void deleteBookByIDNotFound() {
        when(bookRepository.deleteById(999L)).thenReturn(0L);

        Long result = bookService.deleteBookByID(999L);

        assertEquals(0, result);
    }

    @Test
    @DisplayName("deletedBookByID(-1) - Invalid ID")
    void deleteBookByIDInvalid() {
        when(bookRepository.deleteById(-1L)).thenReturn(0L);

        Long result = bookService.deleteBookByID(-1L);

        assertEquals(0, result);
    }
}