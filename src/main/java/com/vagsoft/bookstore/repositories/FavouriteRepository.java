package com.vagsoft.bookstore.repositories;

import com.vagsoft.bookstore.models.entities.Favourite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository interface for accessing favourites data. */
@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, Integer> {
    /**
     * Finds a page of favourites for a specific user.
     *
     * @param userID
     *            the ID of the user whose favourites are to be retrieved
     * @param pageable
     *            the pagination information (optional)
     * @return a page of favourites for the specified user
     */
    Page<Favourite> findFavouritesByUser_Id(Integer userID, Pageable pageable);

    /**
     * Checks if a favourite book exists for a specific user.
     *
     * @param userID
     *            the ID of the user
     * @param bookID
     *            the ID of the book
     * @return true if the favourite exists, false otherwise
     */
    boolean existsByUser_IdAndBook_Id(Integer userID, Integer bookID);

    /**
     * Deletes a favourite book for a specific user.
     *
     * @param userID
     *            the ID of the user
     * @param bookID
     *            the ID of the book
     */
    void deleteByUser_IdAndBook_Id(Integer userID, Integer bookID);
}
