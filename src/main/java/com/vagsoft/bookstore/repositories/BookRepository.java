package com.vagsoft.bookstore.repositories;

import com.vagsoft.bookstore.models.entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/** Repository interface for accessing book data */
@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
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
    @Query("""
            SELECT b
            FROM Book b
            WHERE (:title IS NULL OR b.title ILIKE %:title%)
            AND (:author IS NULL OR b.author ILIKE %:author%)
            AND (:description IS NULL OR b.description ILIKE %:description%)
            AND (:minPrice IS NULL OR b.price >= :minPrice)
            AND (:maxPrice IS NULL OR b.price <= :maxPrice)
            AND (:genre IS NULL OR b.id IN (SELECT g.book.id FROM Genre g WHERE g.genre ILIKE %:genre%))
            """)
    public Page<Book> findBooks(String title, String genre, String author, String description, Double minPrice,
            Double maxPrice, Pageable pageable);

    /**
     * Deletes a book by its ID
     *
     * @param bookID
     *            the ID of the book to be deleted
     * @return the number of books deleted (should be 1)
     */
    public Long deleteById(Long bookID);

    /**
     * Checks if a book with the given ISBN exists in the database
     *
     * @param isbn
     *            the ISBN of the book to be checked
     * @return true if the book exists, false otherwise
     */
    public boolean existsByIsbn(String isbn);

    /**
     * Checks if a book with the given ISBN exists in the database, excluding the
     * book with the specified ID
     *
     * @param isbn
     *            the ISBN of the book to be checked
     * @param bookID
     *            the ID of the book to be excluded from the check
     * @return true if the book exists, false otherwise
     */
    public boolean existsByIsbnAndIdNot(String isbn, Integer bookID);
}
