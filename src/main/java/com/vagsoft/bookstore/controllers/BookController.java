package com.vagsoft.bookstore.controllers;

import java.util.Optional;

import com.vagsoft.bookstore.dto.bookDTOs.BookReadDTO;
import com.vagsoft.bookstore.dto.bookDTOs.BookUpdateDTO;
import com.vagsoft.bookstore.dto.bookDTOs.BookWriteDTO;
import com.vagsoft.bookstore.errors.exceptions.bookExceptions.BookCreationException;
import com.vagsoft.bookstore.errors.exceptions.bookExceptions.BookUpdateException;
import com.vagsoft.bookstore.repositories.BookRepository;
import com.vagsoft.bookstore.services.BookService;
import com.vagsoft.bookstore.validations.annotations.ExistsResource;
import com.vagsoft.bookstore.validations.annotations.IsAdmin;
import com.vagsoft.bookstore.validations.annotations.NullOrNotBlank;
import com.vagsoft.bookstore.validations.groups.BasicValidation;
import com.vagsoft.bookstore.validations.groups.ExtendedValidation;
import com.vagsoft.bookstore.validations.groups.OrderedValidation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/** REST controller for endpoints related to books */
@RestController
@RequestMapping(path = "/books")
@Validated(OrderedValidation.class)
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Retrieves a list of books filtered by the specified parameters
     *
     * @param title
     *            the title of the books to search for (optional)
     * @param genre
     *            the genre of the books to search for (optional)
     * @param author
     *            the author of the books to search for (optional)
     * @param description
     *            the description of the books to search for (optional)
     * @param minPrice
     *            the minimum price of the books to search for (optional)
     * @param maxPrice
     *            the maximum price of the books to search for (optional)
     * @param pageable
     *            the pagination information (optional)
     * @return a page of books
     */
    @GetMapping
    public ResponseEntity<Page<BookReadDTO>> getBooks(
            @RequestParam(name = "title", required = false) @Size(max = 61, message = "title must be less than 64 characters", groups = BasicValidation.class) @NullOrNotBlank(groups = BasicValidation.class) String title,
            @RequestParam(name = "genre", required = false) @Size(max = 31, message = "genre must be less than 32 characters", groups = BasicValidation.class) @NullOrNotBlank(groups = BasicValidation.class) String genre,
            @RequestParam(name = "author", required = false) @Size(max = 31, message = "author must be less than 32 characters", groups = BasicValidation.class) @NullOrNotBlank(groups = BasicValidation.class) String author,
            @RequestParam(name = "description", required = false) @NullOrNotBlank(groups = BasicValidation.class) String description,
            @RequestParam(name = "minPrice", required = false) @Min(value = 0, message = "minPrice must be equal or greater than 0", groups = BasicValidation.class) Double minPrice,
            @RequestParam(name = "maxPrice", required = false) @Min(value = 0, message = "maxPrice must be equal or greater than 0", groups = BasicValidation.class) Double maxPrice,
            Pageable pageable) {
        return ResponseEntity.ok(bookService.getBooks(title, genre, author, description, minPrice, maxPrice, pageable));
    }

    /**
     * Adds a new book
     *
     * @param bookWriteDTO
     *            the book to be added
     * @return the added book
     */
    @ApiResponse(responseCode = "201")
    @IsAdmin()
    @PostMapping
    public ResponseEntity<BookReadDTO> addBook(@Valid @RequestBody BookWriteDTO bookWriteDTO) {
        Optional<BookReadDTO> savedBook = bookService.addBook(bookWriteDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedBook.orElseThrow(() -> new BookCreationException("Book creation failed")));
    }

    /**
     * Retrieves a book by its ID
     *
     * @param bookID
     *            the ID of the book to be retrieved
     * @return the retrieved book
     */
    @GetMapping(path = "/{bookID}")
    public ResponseEntity<BookReadDTO> getBookByID(
            @PathVariable @Positive(groups = BasicValidation.class) @ExistsResource(repository = BookRepository.class, message = "Book with given ID does not exist", groups = ExtendedValidation.class) Integer bookID) {
        BookReadDTO foundBook = bookService.getBookByID(bookID);
        return ResponseEntity.ok(foundBook);
    }

    /**
     * Updates a book by its ID with the given book information
     *
     * @param bookID
     *            the ID of the book to be updated
     * @param bookUpdateDTO
     *            the new book information
     * @return the updated book
     */
    @IsAdmin
    @PutMapping(path = "/{bookID}")
    public ResponseEntity<BookReadDTO> updateBookByID(
            @PathVariable @Positive(groups = BasicValidation.class) @ExistsResource(repository = BookRepository.class, message = "Book with given ID does not exist", groups = ExtendedValidation.class) Integer bookID,
            @Valid @RequestBody BookUpdateDTO bookUpdateDTO) {
        Optional<BookReadDTO> updatedBook = bookService.updateBookByID(bookID, bookUpdateDTO);
        return ResponseEntity
                .ok(updatedBook.orElseThrow(() -> new BookUpdateException("Book with ID:" + bookID + "update failed")));
    }

    /**
     * Deletes a book by its ID
     *
     * @param bookID
     *            the ID of the book to be deleted
     * @return a ResponseEntity with no content
     */
    @ApiResponse(responseCode = "204")
    @IsAdmin
    @DeleteMapping(path = "/{bookID}")
    public ResponseEntity<Void> deleteBookByID(
            @PathVariable @Positive(groups = BasicValidation.class) @ExistsResource(repository = BookRepository.class, message = "Book with given ID does not exist", groups = ExtendedValidation.class) Integer bookID) {
        bookService.deleteBookByID(bookID);

        return ResponseEntity.noContent().build();
    }
}
