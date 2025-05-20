package com.vagsoft.bookstore.services;

import com.vagsoft.bookstore.dto.FavouriteReadDTO;
import com.vagsoft.bookstore.dto.FavouriteWriteDTO;
import com.vagsoft.bookstore.errors.exceptions.BookNotFoundException;
import com.vagsoft.bookstore.errors.exceptions.FavouriteCreationException;
import com.vagsoft.bookstore.mappers.FavouriteMapper;
import com.vagsoft.bookstore.models.entities.Favourite;
import com.vagsoft.bookstore.repositories.BookRepository;
import com.vagsoft.bookstore.repositories.FavouriteRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service class for handling business logic related to favourites
 */
@Service
public class FavouriteService {
    private final FavouriteRepository favouriteRepository;
    private final BookRepository bookRepository;
    private final FavouriteMapper favouriteMapper;

    public FavouriteService(FavouriteRepository favouriteRepository, BookRepository bookRepository, FavouriteMapper favouriteMapper) {
        this.favouriteRepository = favouriteRepository;
        this.bookRepository = bookRepository;
        this.favouriteMapper = favouriteMapper;
    }

    /**
     * Retrieves a list of favourites for a specific user
     *
     * @param userID the ID of the user whose favourites are to be retrieved
     * @param pageable the pagination information (optional)
     * @return a page of favourites for the specified user
     */
    @Transactional(readOnly = true)
    public Page<FavouriteReadDTO> getFavouritesByUserID(Integer userID, Pageable pageable) {
        return favouriteMapper.PageBookToPageDto(favouriteRepository.findFavouritesByUserID(userID, pageable));
    }

    /**
     * Adds a new favourite book for a specific user
     *
     * @param userID the ID of the user to whom the favourite belongs
     * @param favouriteWriteDTO the favourite book to be added
     * @return the created favourite
     */
    @Transactional
    public Optional<FavouriteReadDTO> addFavourite(Integer userID, FavouriteWriteDTO favouriteWriteDTO) {
        Favourite favouriteToSave = favouriteMapper.DtoToFavourite(favouriteWriteDTO);
        favouriteToSave.setUserID(userID);
        favouriteToSave.setBook(bookRepository.findById(favouriteWriteDTO.getBookID()).orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + favouriteWriteDTO.getBookID())));

        Favourite savedFavourite = favouriteRepository.save(favouriteToSave);
        return Optional.of(favouriteMapper.FavouriteToReadDto(savedFavourite));
    }


    /**
     * Deletes a favourite book for a specific user
     *
     * @param userID the ID of the user whose favourite is to be deleted
     * @param bookID the ID of the favourite book to be deleted
     * @return a response entity with no content
     */
    @Transactional
    public Long deleteFavourite(Integer userID, Integer bookID) {
        return favouriteRepository.deleteByUserIDAndBook_Id(userID, bookID);
    }
}
