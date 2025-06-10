package com.vagsoft.bookstore.services;

import java.util.Optional;

import com.vagsoft.bookstore.dto.bookDTOs.BookReadDTO;
import com.vagsoft.bookstore.dto.bookDTOs.BookUpdateDTO;
import com.vagsoft.bookstore.dto.bookDTOs.BookWriteDTO;
import com.vagsoft.bookstore.errors.exceptions.bookExceptions.BookNotFoundException;
import com.vagsoft.bookstore.mappers.BookMapper;
import com.vagsoft.bookstore.models.entities.Book;
import com.vagsoft.bookstore.repositories.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service class for book operations. */
@Service
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookService(final BookRepository bookRepository, final BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    /**
     * Retrieves a list of books filtered by the specified parameters.
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
    @Transactional(readOnly = true)
    public Page<BookReadDTO> getBooks(final String title, final String genre, final String author,
            final String description, final Double minPrice, final Double maxPrice, final Pageable pageable) {
        return bookMapper.pageBookToPageDto(
                bookRepository.findBooks(title, genre, author, description, minPrice, maxPrice, pageable));
    }

    /**
     * Adds a new book.
     *
     * @param bookWriteDTO
     *            the book to be added
     * @return the added book
     */
    @Transactional
    public Optional<BookReadDTO> addBook(final BookWriteDTO bookWriteDTO) {
        Book bookToSave = bookMapper.dtoToBook(bookWriteDTO);

        Book savedBook = bookRepository.save(bookToSave);
        return Optional.of(bookMapper.bookToReadDto(savedBook));
    }

    /**
     * Retrieves a book by its ID.
     *
     * @param bookID
     *            the ID of the book to be retrieved
     * @return the retrieved book
     */
    @Transactional(readOnly = true)
    public BookReadDTO getBookByID(final Integer bookID) {
        Book foundBook = bookRepository.getReferenceById(bookID);
        return bookMapper.bookToReadDto(foundBook);
    }

    /**
     * Updates a book by its ID with the given book information.
     *
     * @param bookID
     *            the ID of the book to be updated
     * @param bookUpdateDTO
     *            the new book information
     * @return the updated book
     */
    @Transactional
    public Optional<BookReadDTO> updateBookByID(final Integer bookID, final BookUpdateDTO bookUpdateDTO) {

        Book foundBook = bookRepository.getReferenceById(bookID);

        bookMapper.updateBookFromDto(bookUpdateDTO, foundBook);

        Book updatedBook = bookRepository.save(foundBook);

        return Optional.of(bookMapper.bookToReadDto(updatedBook));
    }

    /**
     * Deletes a book by its ID.
     *
     * @param bookID
     *            the ID of the book to be deleted
     */
    @Transactional
    public void deleteBookByID(final Integer bookID) {
        bookRepository.deleteById(bookID);
    }

    /**
     * Checks if a book has enough stock and deletes the requested quantity from the
     * stock.
     *
     * @param bookID
     *            the ID of the book to check
     * @param quantity
     *            the quantity of the book
     */
    @Transactional
    public void requestBooks(final Integer bookID, final Integer quantity) {
        Book book = bookRepository.findById(bookID)
                .orElseThrow(() -> new BookNotFoundException("No book found with the given ID: " + bookID));

        if (book.getAvailability() >= quantity) {
            book.setAvailability(book.getAvailability() - quantity);
        } else {
            throw new IllegalArgumentException("Not enough stock for book with ID: " + bookID);
        }
    }

    /**
     * Returns books to the stock by increasing the availability of the book.
     *
     * @param bookID
     *            the ID of the book to return
     * @param quantity
     *            the quantity of the book to return
     */
    @Transactional
    public void returnBooks(final Integer bookID, final Integer quantity) {
        Book book = bookRepository.findById(bookID)
                .orElseThrow(() -> new BookNotFoundException("No book found with the given ID: " + bookID));

        book.setAvailability(book.getAvailability() + quantity);
    }
}
