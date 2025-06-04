package com.vagsoft.bookstore.unit.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.vagsoft.bookstore.dto.bookDTOs.BookReadDTO;
import com.vagsoft.bookstore.dto.bookDTOs.BookUpdateDTO;
import com.vagsoft.bookstore.dto.bookDTOs.BookWriteDTO;
import com.vagsoft.bookstore.errors.exceptions.BookNotFoundException;
import com.vagsoft.bookstore.mappers.BookMapper;
import com.vagsoft.bookstore.models.entities.Book;
import com.vagsoft.bookstore.models.entities.Genre;
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
        storedBooks.add(new Book(2, "title2", "author2", "description2", 2, 2.0, 2, "isbn2",
                List.of(new Genre(1, "genre1"), new Genre(2, "genre2"))));
    }

    @Test
    @DisplayName("getBooks() - Success No Filters")
    void getBooksNoFilters() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Book> page = new PageImpl<>(storedBooks, pageable, 2);

        when(bookRepository.findBooks(null, null, null, null, null, null, pageable)).thenReturn(page);

        Page<BookReadDTO> result = bookService.getBooks(null, null, null, null, null, null, pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(bookMapper.bookToReadDto(storedBooks.get(0)), result.getContent().get(0));
        assertEquals(bookMapper.bookToReadDto(storedBooks.get(1)), result.getContent().get(1));
    }

    @Test
    @DisplayName("getBooks() - Success With Filters")
    void getBooksWithFilters() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Book> page = new PageImpl<>(List.of(storedBooks.getLast()), pageable, 2);

        when(bookRepository.findBooks("title", "genre1", "author", "description2", 1.0, 2.0, pageable))
                .thenReturn(page);

        Page<BookReadDTO> result = bookService.getBooks("title", "genre1", "author", "description2", 1.0, 2.0,
                pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(bookMapper.bookToReadDto(storedBooks.get(1)), result.getContent().getFirst());
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

        when(bookRepository.save(bookMapper.dtoToBook(bookToAdd))).thenReturn(addedBook);

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
        when(bookRepository.existsById(1)).thenReturn(true);
        when(bookRepository.getReferenceById(1)).thenReturn(storedBooks.getFirst());

        BookReadDTO result = bookService.getBookByID(1);
        assertNotNull(result);
        assertEquals(storedBooks.getFirst().getId(), result.getId());
        assertEquals(storedBooks.getFirst().getTitle(), result.getTitle());
        assertEquals(storedBooks.getFirst().getAuthor(), result.getAuthor());
        assertEquals(storedBooks.getFirst().getDescription(), result.getDescription());
        assertEquals(storedBooks.getFirst().getPrice(), result.getPrice());
        assertEquals(storedBooks.getFirst().getAvailability(), result.getAvailability());
        assertEquals(storedBooks.getFirst().getIsbn(), result.getIsbn());
        assertTrue(result.getGenres().isEmpty());
    }

    @Test
    @DisplayName("updateBookByID(1) - Success")
    void updateBookByIDFound() {
        BookUpdateDTO updateBookDTO = new BookUpdateDTO();
        updateBookDTO.setTitle("New Title");

        when(bookRepository.existsById(1)).thenReturn(true);
        when(bookRepository.getReferenceById(1)).thenReturn(storedBooks.getFirst());

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
    @DisplayName("deleteBookByID(1) - Success")
    void deleteBookByIDFound() {
        when(bookRepository.existsById(1)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(1);

        bookService.deleteBookByID(1);

        verify(bookRepository).deleteById(1);
    }
}
