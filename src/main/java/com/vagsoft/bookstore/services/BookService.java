package com.vagsoft.bookstore.services;

import java.util.Optional;

import com.vagsoft.bookstore.dto.bookDTOs.BookReadDTO;
import com.vagsoft.bookstore.dto.bookDTOs.BookUpdateDTO;
import com.vagsoft.bookstore.dto.bookDTOs.BookWriteDTO;
import com.vagsoft.bookstore.errors.exceptions.BookNotFoundException;
import com.vagsoft.bookstore.mappers.BookMapper;
import com.vagsoft.bookstore.models.entities.Book;
import com.vagsoft.bookstore.repositories.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service class for book operations */
@Service
public class BookService {
    private static final Logger log = LoggerFactory.getLogger(BookService.class);
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    /**
     * Retrieves a list of books filtered by the specified parameters
     *
     * @param title
     *            the title of the books search for (optional)
     * @param genre
     *            the genre of the books search for (optional)
     * @param author
     *            the author of the books search for (optional)
     * @param description
     *            the description of the books search for (optional)
     * @param minPrice
     *            the minimum price of the books search for (optional)
     * @param maxPrice
     *            the maximum price of the books search for (optional)
     * @param pageable
     *            the pagination information (optional)
     * @return a page of books
     */
    @Transactional(readOnly = true)
    public Page<BookReadDTO> getBooks(String title, String genre, String author, String description, Double minPrice,
            Double maxPrice, Pageable pageable) {
        return bookMapper.pageBookToPageDto(
                bookRepository.findBooks(title, genre, author, description, minPrice, maxPrice, pageable));
    }

    /**
     * Adds a new book
     *
     * @param bookWriteDTO
     *            the book to be added
     * @return the added book
     */
    @Transactional
    public Optional<BookReadDTO> addBook(BookWriteDTO bookWriteDTO) {
        Book bookToSave = bookMapper.DtoToBook(bookWriteDTO);

        Book savedBook = bookRepository.save(bookToSave);
        return Optional.of(bookMapper.BookToReadDto(savedBook));
    }

    /**
     * Retrieves a book by its ID
     *
     * @param bookID
     *            the ID of the book to be retrieved
     * @return the retrieved book
     */
    @Transactional(readOnly = true)
    public Optional<BookReadDTO> getBookByID(Integer bookID) {
        Optional<Book> foundBook = bookRepository.findById(bookID);
        return foundBook.map(bookMapper::BookToReadDto);
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
    @Transactional
    public Optional<BookReadDTO> updateBookByID(Integer bookID, BookUpdateDTO bookUpdateDTO) {

        Book foundBook = bookRepository.findById(bookID)
                .orElseThrow(() -> new BookNotFoundException("No book found with the given ID: " + bookID));

        bookMapper.updateBookFromDto(bookUpdateDTO, foundBook);

        Book updatedBook = bookRepository.save(foundBook);

        return Optional.of(bookMapper.BookToReadDto(updatedBook));
    }

    /**
     * Deletes a book by its ID
     *
     * @param bookID
     *            the ID of the book to be deleted
     */
    @Transactional
    public void deleteBookByID(Integer bookID) {
        bookRepository.deleteById(bookID);
    }
}
