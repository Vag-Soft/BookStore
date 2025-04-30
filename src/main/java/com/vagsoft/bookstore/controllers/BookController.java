package com.vagsoft.bookstore.controllers;

import com.vagsoft.bookstore.annotations.NullOrNotBlank;
import com.vagsoft.bookstore.dto.BookReadDTO;
import com.vagsoft.bookstore.dto.BookUpdateDTO;
import com.vagsoft.bookstore.dto.BookWriteDTO;
import com.vagsoft.bookstore.errors.exceptions.BookCreationException;
import com.vagsoft.bookstore.errors.exceptions.BookNotFoundException;
import com.vagsoft.bookstore.models.Book;
import com.vagsoft.bookstore.services.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.websocket.server.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for endpoints related to books
 */
@RestController
@RequestMapping(path = "/books")
@Validated
public class BookController {
    private static final Logger log = LoggerFactory.getLogger(BookController.class);
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Retrieves a list of books filtered by title, genre, author, description, minPrice and maxPrice
     *
     * @param title the title of the books search for (optional)
     * @param genre the genre of the books search for (optional)
     * @param author the author of the books search for (optional)
     * @param description the description of the books search for (optional)
     * @param minPrice the minimum price of the books search for (optional)
     * @param maxPrice the maximum price of the books search for (optional)
     * @param pageable the pagination information (optional)
     * @return a page of books
     */
    @GetMapping
    public ResponseEntity<Page<BookReadDTO>> getBooks(
            @RequestParam(name="title", required=false) @NullOrNotBlank String title,
            @RequestParam(name="genre", required=false) @NullOrNotBlank String genre,
            @RequestParam(name="author", required=false) @NullOrNotBlank String author,
            @RequestParam(name="description", required=false) @NullOrNotBlank String description,
            @RequestParam(name="minPrice", required=false) @Min(value=0, message="minPrice must be greater than 0" ) Double minPrice,
            @RequestParam(name="maxPrice", required=false) @Min(value =0, message="maxPrice must be greater than 0") Double maxPrice,
            Pageable pageable) {

        log.info("GET /books: title={}, genre={}, author={}, description={}, minPrice={}, maxPrice={}, pageable={}",
            title, genre, author, description, minPrice, maxPrice, pageable);

        return ResponseEntity.ok(bookService.getBooks(title, genre, author, description, minPrice, maxPrice, pageable));
    }

    /**
     * Adds a new book
     *
     * @param bookWriteDTO the book to be added
     * @return the added book
     */
    @PostMapping
    public ResponseEntity<BookReadDTO> addBook(@Valid @RequestBody BookWriteDTO bookWriteDTO) {
        log.info("POST /books: book={}", bookWriteDTO);

        Optional<BookReadDTO> savedBook = bookService.addBook(bookWriteDTO);
        return ResponseEntity.ok(savedBook.orElseThrow(() -> new BookCreationException("Book creation failed")));
    }

    /**
     * Retrieves a book by its ID
     *
     * @param bookID the ID of the book to be retrieved
     * @return the retrieved book
     */
    @GetMapping(path = "/{bookID}")
    public ResponseEntity<BookReadDTO> getBookByID(@PathVariable @Positive Integer bookID) {
        log.info("GET /books/{bookID}: bookID={}", bookID);

        Optional<BookReadDTO> foundBook = bookService.getBookByID(bookID);
        return ResponseEntity.ok(foundBook.orElseThrow(() -> new BookNotFoundException("No book found with the given ID")));
    }

    /**
     * Updates a book by its ID with the given book information
     *
     * @param bookID the ID of the book to be updated
     * @param bookUpdateDTO the new book information
     * @return the updated book
     */
    @PutMapping(path = "/{bookID}")
    public ResponseEntity<BookReadDTO> updateBookByID(@PathVariable @Positive Integer bookID, @Valid @RequestBody BookUpdateDTO bookUpdateDTO) {
        log.info("PUT /books/{bookID}: bookID={}, book={}", bookID, bookUpdateDTO);

        Optional<BookReadDTO> updatedBook = bookService.updateBookByID(bookID, bookUpdateDTO);
        return ResponseEntity.ok(updatedBook.orElseThrow(() -> new BookNotFoundException("No book found with the given ID")));
    }

    /**
     * Deletes a book by its ID
     *
     * @param bookID the ID of the book to be deleted
     * @return a ResponseEntity with no content
     */
    @DeleteMapping(path = "/{bookID}")
    public ResponseEntity<Void> deleteBookByID(@PathVariable @Positive Long bookID) {
        log.info("DELETE /books/{bookID}: bookID={}", bookID);

        Long deletedBooks = bookService.deleteBookByID(bookID);

        if(deletedBooks == 0) throw new BookNotFoundException("No book found with the given ID");

        return ResponseEntity.noContent().build();
    }
}
