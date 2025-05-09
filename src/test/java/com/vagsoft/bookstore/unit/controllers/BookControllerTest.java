package com.vagsoft.bookstore.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vagsoft.bookstore.controllers.BookController;
import com.vagsoft.bookstore.dto.BookReadDTO;
import com.vagsoft.bookstore.dto.BookUpdateDTO;
import com.vagsoft.bookstore.dto.BookWriteDTO;
import com.vagsoft.bookstore.dto.GenreDTO;
import com.vagsoft.bookstore.repositories.BookRepository;
import com.vagsoft.bookstore.services.BookService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ActiveProfiles("test")
class BookControllerTest {
    @MockitoBean
    private BookService bookService;
    @MockitoBean
    private BookRepository bookRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private List<BookReadDTO> storedBooks;
    @BeforeEach
    void setUp() {
        storedBooks = new ArrayList<>();
        storedBooks.add(new BookReadDTO(1, "title", "author", "description", 1, 1.0, 1, "isbn", new ArrayList<>()));
        storedBooks.add(new BookReadDTO(2, "title2", "author2", "description2", 2, 2.0, 2, "isbn2", List.of(new GenreDTO(1, "genre1"), new GenreDTO(2, "genre2"))));
    }

    @Test
    @DisplayName("GET /books - Success No Filters")
    void getBooksNoFilters() throws Exception {
        Pageable pageable= PageRequest.of(0, 20);
        Page<BookReadDTO> page = new PageImpl<>(storedBooks, pageable, 2);

        when(bookService.getBooks(null, null, null, null, null, null, pageable)).thenReturn(page);

        mockMvc.perform(get("/books")
                            .param("page", "0")
                            .param("size", "20")
                            .accept("application/json"))

                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("title"))
                .andExpect(jsonPath("$.content[0].author").value("author"))
                .andExpect(jsonPath("$.content[0].description").value("description"))
                .andExpect(jsonPath("$.content[0].pages").value(1))
                .andExpect(jsonPath("$.content[0].price").value(1.0))
                .andExpect(jsonPath("$.content[0].availability").value(1))
                .andExpect(jsonPath("$.content[0].isbn").value("isbn"))
                .andExpect(jsonPath("$.content[0].genres").isEmpty())
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].title").value("title2"))
                .andExpect(jsonPath("$.content[1].author").value("author2"))
                .andExpect(jsonPath("$.content[1].description").value("description2"))
                .andExpect(jsonPath("$.content[1].pages").value(2))
                .andExpect(jsonPath("$.content[1].price").value(2.0))
                .andExpect(jsonPath("$.content[1].availability").value(2))
                .andExpect(jsonPath("$.content[1].isbn").value("isbn2"))
                .andExpect(jsonPath("$.content[1].genres").isArray())
                .andExpect(jsonPath("$.content[1].genres", hasSize(2)))
                .andExpect(jsonPath("$.content[1].genres[0].id").value(1))
                .andExpect(jsonPath("$.content[1].genres[0].genre").value("genre1"))
                .andExpect(jsonPath("$.content[1].genres[1].id").value(2))
                .andExpect(jsonPath("$.content[1].genres[1].genre").value("genre2"));
    }


    @Test
    @DisplayName("GET /books - Success With Filters and Pagination")
    void getBooksWithFilters() throws Exception {
        Pageable pageable= PageRequest.of(0, 1);
        Page<BookReadDTO> page = new PageImpl<>(storedBooks, pageable, 2);

        when(bookService.getBooks("title", "genre", "author", "desc",  0.0, 100.0, pageable)).thenReturn(page);

        mockMvc.perform(get("/books")
                        .param("title", "title")
                        .param("genre", "genre")
                        .param("author", "author")
                        .param("description", "desc")
                        .param("minPrice", "0.0")
                        .param("maxPrice", "100.0")
                        .param("page", "0")
                        .param("size", "1")
                        .accept("application/json"))

                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("title"))
                .andExpect(jsonPath("$.content[0].author").value("author"))
                .andExpect(jsonPath("$.content[0].description").value("description"))
                .andExpect(jsonPath("$.content[0].pages").value(1))
                .andExpect(jsonPath("$.content[0].price").value(1.0))
                .andExpect(jsonPath("$.content[0].availability").value(1))
                .andExpect(jsonPath("$.content[0].isbn").value("isbn"))
                .andExpect(jsonPath("$.content[0].genres").isEmpty())
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].title").value("title2"))
                .andExpect(jsonPath("$.content[1].author").value("author2"))
                .andExpect(jsonPath("$.content[1].description").value("description2"))
                .andExpect(jsonPath("$.content[1].pages").value(2))
                .andExpect(jsonPath("$.content[1].price").value(2.0))
                .andExpect(jsonPath("$.content[1].availability").value(2))
                .andExpect(jsonPath("$.content[1].isbn").value("isbn2"))
                .andExpect(jsonPath("$.content[1].genres").isArray())
                .andExpect(jsonPath("$.content[1].genres", hasSize(2)))
                .andExpect(jsonPath("$.content[1].genres[0].id").value(1))
                .andExpect(jsonPath("$.content[1].genres[0].genre").value("genre1"))
                .andExpect(jsonPath("$.content[1].genres[1].id").value(2))
                .andExpect(jsonPath("$.content[1].genres[1].genre").value("genre2"))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(1))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("POST /books - Success")
    void addBook() throws Exception {
        BookWriteDTO newBook = new BookWriteDTO("title3", "author3", "description3", 3, 3.0, 3, "isbn3", List.of(new GenreDTO( "genre1")));
        BookReadDTO savedBook = new BookReadDTO(3,"title3", "author3", "description3", 3, 3.0, 3, "isbn3", List.of(new GenreDTO(3, "genre1")));

        when(bookService.addBook(newBook)).thenReturn(Optional.of(savedBook));
        when(bookRepository.existsByIsbn("isbn3")).thenReturn(false);

        String newBookString = objectMapper.writeValueAsString(newBook);

        mockMvc.perform(post("/books").content(newBookString).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))

                .andExpect(jsonPath("$.id").value(savedBook.getId()))
                .andExpect(jsonPath("$.title").value(savedBook.getTitle()))
                .andExpect(jsonPath("$.author").value(savedBook.getAuthor()))
                .andExpect(jsonPath("$.description").value(savedBook.getDescription()))
                .andExpect(jsonPath("$.pages").value(savedBook.getPages()))
                .andExpect(jsonPath("$.price").value(savedBook.getPrice()))
                .andExpect(jsonPath("$.availability").value(savedBook.getAvailability()))
                .andExpect(jsonPath("$.isbn").value(savedBook.getIsbn()))
                .andExpect(jsonPath("$.genres").isArray())
                .andExpect(jsonPath("$.genres", hasSize(1)))
                .andExpect(jsonPath("$.genres[0].id").value(3))
                .andExpect(jsonPath("$.genres[0].genre").value("genre1"));
    }

    @Test
    @DisplayName("GET /books/1 - Success")
    void getBookByIDFound() throws Exception {
        BookReadDTO bookOutput = storedBooks.getFirst();
        when(bookService.getBookByID(1)).thenReturn(Optional.ofNullable(bookOutput));

        assertNotNull(bookOutput);
        mockMvc.perform(get("/books/{bookID}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))

                .andExpect(jsonPath("$.id").value(bookOutput.getId()))
                .andExpect(jsonPath("$.title").value(bookOutput.getTitle()))
                .andExpect(jsonPath("$.author").value(bookOutput.getAuthor()))
                .andExpect(jsonPath("$.description").value(bookOutput.getDescription()))
                .andExpect(jsonPath("$.pages").value(bookOutput.getPages()))
                .andExpect(jsonPath("$.price").value(bookOutput.getPrice()))
                .andExpect(jsonPath("$.availability").value(bookOutput.getAvailability()))
                .andExpect(jsonPath("$.isbn").value(bookOutput.getIsbn()))
                .andExpect(jsonPath("$.genres").value(bookOutput.getGenres()));

    }

    @Test
    @DisplayName("GET /books/999} - Not Found")
    void getBookByIDNotFound() throws Exception {
        when(bookService.getBookByID(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/books/{bookID}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /books/-1} - Invalid ID")
    void getBookByIDInvalid() throws Exception {
        mockMvc.perform(get("/books/{bookID}", -1))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /books/1 - Success")
    void updateBookByIDFound() throws Exception{

        BookUpdateDTO bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.setTitle("title10");
        bookUpdateDTO.setGenres(List.of(new GenreDTO(4, "genre2")));

        BookReadDTO bookOutput = storedBooks.getFirst();
        bookOutput.setTitle(bookUpdateDTO.getTitle());
        bookOutput.setGenres(bookUpdateDTO.getGenres());

        when(bookService.updateBookByID(1, bookUpdateDTO)).thenReturn(Optional.of(bookOutput));

        mockMvc.perform(put("/books/{bookID}", 1)
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(bookUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))

                .andExpect(jsonPath("$.id").value(bookOutput.getId()))
                .andExpect(jsonPath("$.title").value(bookUpdateDTO.getTitle()))
                .andExpect(jsonPath("$.author").value(bookOutput.getAuthor()))
                .andExpect(jsonPath("$.description").value(bookOutput.getDescription()))
                .andExpect(jsonPath("$.pages").value(bookOutput.getPages()))
                .andExpect(jsonPath("$.price").value(bookOutput.getPrice()))
                .andExpect(jsonPath("$.availability").value(bookOutput.getAvailability()))
                .andExpect(jsonPath("$.isbn").value(bookOutput.getIsbn()))
                .andExpect(jsonPath("$.genres").isArray())
                .andExpect(jsonPath("$.genres", hasSize(1)))
                .andExpect(jsonPath("$.genres[0].id").value(4))
                .andExpect(jsonPath("$.genres[0].genre").value("genre2"));
    }

    @Test
    @DisplayName("PUT /books/999 - Not Found")
    void updateBookByIDNotFound() throws Exception{
        BookUpdateDTO bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.setTitle("title10");
        bookUpdateDTO.setGenres(List.of(new GenreDTO(4, "genre2")));

        when(bookService.getBookByID(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/books/{bookID}", 999)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookUpdateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /books/-1 - Invalid ID")
    void updateBookByIDInvalid() throws Exception{
        BookUpdateDTO bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.setTitle("title10");
        bookUpdateDTO.setGenres(List.of(new GenreDTO(4, "genre2")));

        when(bookService.getBookByID(-1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/books/{bookID}", -1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookUpdateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /books/1 - Success")
    void deleteBookByIDFound() throws Exception {
        when(bookService.deleteBookByID(1L)).thenReturn(1L);

        mockMvc.perform(delete("/books/{bookID}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /books/999 - Not Found")
    void deleteBookByIDNotFound() throws Exception {
        when(bookService.deleteBookByID(999L)).thenReturn(0L);

        mockMvc.perform(delete("/books/{bookID}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /books/-1 - Invalid ID")
    void deleteBookByIDInvalid() throws Exception {
        mockMvc.perform(delete("/books/{bookID}", -1))
                .andExpect(status().isBadRequest());
    }
}