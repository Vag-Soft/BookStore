package com.vagsoft.bookstore.services;

import java.util.Optional;

import com.vagsoft.bookstore.dto.favouriteDTOs.FavouriteReadDTO;
import com.vagsoft.bookstore.dto.favouriteDTOs.FavouriteWriteDTO;
import com.vagsoft.bookstore.mappers.FavouriteMapper;
import com.vagsoft.bookstore.models.entities.Favourite;
import com.vagsoft.bookstore.repositories.BookRepository;
import com.vagsoft.bookstore.repositories.FavouriteRepository;
import com.vagsoft.bookstore.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service class for handling business logic related to favourites. */
@Service
public class FavouriteService {
    private final FavouriteRepository favouriteRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final FavouriteMapper favouriteMapper;

    public FavouriteService(final FavouriteRepository favouriteRepository, final BookRepository bookRepository,
            final UserRepository userRepository, final FavouriteMapper favouriteMapper) {
        this.favouriteRepository = favouriteRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.favouriteMapper = favouriteMapper;
    }

    /**
     * Retrieves a list of favourites for a specific user.
     *
     * @param userID
     *            the ID of the user whose favourites are to be retrieved
     * @param pageable
     *            the pagination information (optional)
     * @return a page of favourites for the specified user
     */
    @Transactional(readOnly = true)
    public Page<FavouriteReadDTO> getFavouritesByUserID(final Integer userID, final Pageable pageable) {
        return favouriteMapper.pageBookToPageDto(favouriteRepository.findFavouritesByUser_Id(userID, pageable));
    }

    /**
     * Adds a new favourite book for a specific user.
     *
     * @param userID
     *            the ID of the user to whom the favourite belongs
     * @param favouriteWriteDTO
     *            the favourite book to be added
     * @return the created favourite
     */
    @Transactional
    public Optional<FavouriteReadDTO> addFavourite(final Integer userID, final FavouriteWriteDTO favouriteWriteDTO) {
        Favourite favouriteToSave = favouriteMapper.dtoToFavourite(favouriteWriteDTO);
        favouriteToSave.setUser(userRepository.getReferenceById(userID));
        favouriteToSave.setBook(bookRepository.getReferenceById(favouriteWriteDTO.getBookID()));

        Favourite savedFavourite = favouriteRepository.save(favouriteToSave);
        return Optional.of(favouriteMapper.favouriteToReadDto(savedFavourite));
    }

    /**
     * Deletes a favourite book for a specific user.
     *
     * @param userID
     *            the ID of the user whose favourite is to be deleted
     * @param bookID
     *            the ID of the favourite book to be deleted
     */
    @Transactional
    public void deleteFavourite(final Integer userID, final Integer bookID) {
        favouriteRepository.deleteByUser_IdAndBook_Id(userID, bookID);
    }
}
