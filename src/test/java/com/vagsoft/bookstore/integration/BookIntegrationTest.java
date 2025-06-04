package com.vagsoft.bookstore.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vagsoft.bookstore.dto.bookDTOs.BookReadDTO;
import com.vagsoft.bookstore.dto.bookDTOs.BookUpdateDTO;
import com.vagsoft.bookstore.dto.bookDTOs.BookWriteDTO;
import com.vagsoft.bookstore.dto.genreDTOs.GenreDTO;
import com.vagsoft.bookstore.mappers.BookMapper;
import com.vagsoft.bookstore.models.entities.Book;
import com.vagsoft.bookstore.models.entities.Genre;
import com.vagsoft.bookstore.pagination.CustomPageImpl;
import com.vagsoft.bookstore.repositories.BookRepository;
import com.vagsoft.bookstore.services.BookService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ActiveProfiles("test")
public class BookIntegrationTest {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookService bookService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private TestRestTemplate client;

    Book book1, book2, book3;

    @BeforeEach
    void setUp() {
        book1 = Book.builder().title("The Lord of the Rings").author("J. R. R. Tolkien").description(
                "The Lord of the Rings is a series of three fantasy novels written by English author and scholar J. R. R. Tolkien.")
                .pages(1178).price(15.0).availability(5).isbn("978-0-395-36381-0").build();

        book2 = Book.builder().title("Harry Potter and the Philosopher's Stone").author("J. K. Rowling").description(
                "Harry Potter and the Philosopher's Stone is a fantasy novel written by British author J. K. Rowling.")
                .pages(223).price(20.0).availability(10).isbn("978-0-7-152-20664-5").build();

        book3 = Book.builder().title("Harry Potter and the Chamber of Secrets").author("J. K. Rowling").description(
                "Harry Potter and the Chamber of Secrets is a fantasy novel written by British author J. K. Rowling.")
                .pages(251).price(20.0).availability(2).isbn("978-0-7-152-20665-2").build();

        Genre genre1 = Genre.builder().book(book1).genre("Fantasy").build();
        Genre genre2 = Genre.builder().book(book1).genre("Adventure").build();

        Genre genre3 = Genre.builder().book(book2).genre("Fantasy").build();
        Genre genre4 = Genre.builder().book(book2).genre("Young Adult").build();

        Genre genre5 = Genre.builder().book(book3).genre("Fantasy").build();
        Genre genre6 = Genre.builder().book(book3).genre("Young Adult").build();

        book1.setGenres(List.of(genre1, genre2));
        book2.setGenres(List.of(genre3, genre4));
        book3.setGenres(List.of(genre5, genre6));

        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);
    }

    @AfterEach
    void tearDown() {
        bookRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /books - Success No Filters")
    void getBooksNoFilters() {
        URI uri = UriComponentsBuilder.fromUriString("/books").build().encode().toUri();

        ParameterizedTypeReference<CustomPageImpl<BookReadDTO>> classType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<CustomPageImpl<BookReadDTO>> response = client.exchange(uri, HttpMethod.GET, null, classType);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().getContent().size());

        BookReadDTO firstBook = response.getBody().getContent().get(0);
        assertEquals(bookMapper.bookToReadDto(book1), firstBook);

        BookReadDTO secondBook = response.getBody().getContent().get(1);
        assertEquals(bookMapper.bookToReadDto(book2), secondBook);
    }

    @Test
    @DisplayName("GET /books - Success With Filters")
    void getBooksWithFilters() {
        URI uri = UriComponentsBuilder.fromUriString("/books").queryParam("page", 0).queryParam("size", 20)
                .queryParam("title", "harry").queryParam("author", "J. K. Rowling").queryParam("genre", "fantasy")
                .queryParam("description", "novel").build().encode().toUri();

        ParameterizedTypeReference<CustomPageImpl<BookReadDTO>> classType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<CustomPageImpl<BookReadDTO>> response = client.exchange(uri, HttpMethod.GET, null, classType);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        CustomPageImpl<BookReadDTO> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.getContent().size());

        BookReadDTO firstBook = response.getBody().getContent().get(0);
        assertEquals(bookMapper.bookToReadDto(book2), firstBook);

        BookReadDTO secondBook = response.getBody().getContent().get(1);
        assertEquals(bookMapper.bookToReadDto(book3), secondBook);
    }

    @Test
    @DisplayName("POST /books - Success")
    void addBook() throws Exception {
        BookWriteDTO newBook = new BookWriteDTO("title3", "author3", "description3", 3, 3.0, 3, "isbn3",
                List.of(new GenreDTO("genre1")));

        String newBookString = objectMapper.writeValueAsString(newBook);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(newBookString, headers);

        ResponseEntity<BookReadDTO> response = client.postForEntity("/books", request, BookReadDTO.class);

        assertEquals(HttpStatusCode.valueOf(201), response.getStatusCode());

        BookReadDTO savedBook = response.getBody();
        assertNotNull(savedBook);
        assertEquals(book3.getId() + 1, savedBook.getId());
        assertEquals("title3", savedBook.getTitle());
        assertEquals("author3", savedBook.getAuthor());
        assertEquals(3.0, savedBook.getPrice());
        assertEquals(1, savedBook.getGenres().size());
        assertEquals("genre1", savedBook.getGenres().getFirst().getGenre());

        BookReadDTO foundBook = bookService.getBookByID(book3.getId() + 1);
        assertNotNull(foundBook);
        assertEquals(book3.getId() + 1, foundBook.getId());
        assertEquals("title3", foundBook.getTitle());
        assertEquals("author3", foundBook.getAuthor());
        assertEquals(3.0, foundBook.getPrice());
        assertEquals(1, foundBook.getGenres().size());
        assertEquals("genre1", foundBook.getGenres().getFirst().getGenre());
    }

    @Test
    @DisplayName("GET /books/{bookID} - Success")
    void getBookByIDFound() {
        ResponseEntity<BookReadDTO> response = client.getForEntity("/books/" + book1.getId(), BookReadDTO.class);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        BookReadDTO book = response.getBody();
        assertEquals(bookMapper.bookToReadDto(book1), book);
    }

    @Test
    @DisplayName("GET /books/999 - Not Found")
    void getBookByIDNotFound() {
        ResponseEntity<BookReadDTO> response = client.getForEntity("/books/999", BookReadDTO.class);

        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    @DisplayName("GET /books/-1 - Invalid ID")
    void getBookByIDInvalid() {
        ResponseEntity<BookReadDTO> response = client.getForEntity("/books/-1", BookReadDTO.class);

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }

    @Test
    @DisplayName("PUT /books/{bookID} - Success")
    void updateBookByIDFound() throws Exception {
        BookUpdateDTO bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.setTitle("title10");
        bookUpdateDTO.setGenres(List.of(new GenreDTO("genre2")));
        String updateBookString = objectMapper.writeValueAsString(bookUpdateDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(updateBookString, headers);

        ResponseEntity<BookReadDTO> response = client.exchange("/books/" + book1.getId(), HttpMethod.PUT, request,
                BookReadDTO.class);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        BookReadDTO updatedBook = response.getBody();
        assertNotNull(updatedBook);
        assertEquals(book1.getId(), updatedBook.getId());
        assertEquals("title10", updatedBook.getTitle());
        assertEquals("J. R. R. Tolkien", updatedBook.getAuthor());
        assertEquals(1, updatedBook.getGenres().size());
        assertEquals(book3.getGenres().getLast().getId() + 1, updatedBook.getGenres().getFirst().getId());
        assertEquals("genre2", updatedBook.getGenres().getFirst().getGenre());

        BookReadDTO foundBook = bookService.getBookByID(book1.getId());
        assertNotNull(foundBook);
        assertEquals(book1.getId(), foundBook.getId());
        assertEquals("title10", foundBook.getTitle());
        assertEquals("J. R. R. Tolkien", foundBook.getAuthor());
        assertEquals(1, foundBook.getGenres().size());
        assertEquals(book3.getGenres().getLast().getId() + 1, foundBook.getGenres().getFirst().getId());
        assertEquals("genre2", foundBook.getGenres().getFirst().getGenre());
    }

    @Test
    @DisplayName("PUT /books/999 - Not Found")
    void updateBookByIDNotFound() throws Exception {
        BookUpdateDTO bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.setTitle("title10");
        bookUpdateDTO.setGenres(List.of(new GenreDTO("genre2")));
        String updateBookString = objectMapper.writeValueAsString(bookUpdateDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(updateBookString, headers);

        ResponseEntity<BookReadDTO> response = client.exchange("/books/999", HttpMethod.PUT, request,
                BookReadDTO.class);

        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());

        assertFalse(bookRepository.existsById(999));
    }

    @Test
    @DisplayName("PUT /books/-1 - Invalid ID")
    void updateBookByIDInvalid() throws Exception {
        BookUpdateDTO bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.setTitle("title10");
        bookUpdateDTO.setGenres(List.of(new GenreDTO("genre2")));
        String updateBookString = objectMapper.writeValueAsString(bookUpdateDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(updateBookString, headers);

        ResponseEntity<BookReadDTO> response = client.exchange("/books/-1", HttpMethod.PUT, request, BookReadDTO.class);

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());

        assertFalse(bookRepository.existsById(-1));
    }

    @Test
    @DisplayName("DELETE /books/{bookID} - Success")
    void deleteBookByIDFound() {
        ResponseEntity<Void> response = client.exchange("/books/" + book1.getId(), HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());

        assertFalse(bookRepository.existsById(book1.getId()));
    }

    @Test
    @DisplayName("DELETE /books/999 - Not Found")
    void deleteBookByIDNotFound() {
        ResponseEntity<Void> response = client.exchange("/books/999", HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());

        assertFalse(bookRepository.existsById(999));
    }

    @Test
    @DisplayName("DELETE /books/-1 - Invalid ID")
    void deleteBookByIDInvalid() {
        ResponseEntity<Void> response = client.exchange("/books/-1", HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());

        assertFalse(bookRepository.existsById(-1));
    }
}
