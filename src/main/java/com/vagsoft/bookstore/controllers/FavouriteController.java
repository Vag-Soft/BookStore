package com.vagsoft.bookstore.controllers;

import java.util.Optional;

import com.vagsoft.bookstore.dto.favouriteDTOs.FavouriteReadDTO;
import com.vagsoft.bookstore.dto.favouriteDTOs.FavouriteWriteDTO;
import com.vagsoft.bookstore.errors.exceptions.FavouriteCreationException;
import com.vagsoft.bookstore.repositories.FavouriteRepository;
import com.vagsoft.bookstore.repositories.UserRepository;
import com.vagsoft.bookstore.services.FavouriteService;
import com.vagsoft.bookstore.utils.AuthUtils;
import com.vagsoft.bookstore.validations.annotations.ExistsCompositeResource;
import com.vagsoft.bookstore.validations.annotations.ExistsResource;
import com.vagsoft.bookstore.validations.annotations.IsAdmin;
import com.vagsoft.bookstore.validations.annotations.UniqueCompositeFields;
import com.vagsoft.bookstore.validations.groups.BasicValidation;
import com.vagsoft.bookstore.validations.groups.ExtendedValidation;
import com.vagsoft.bookstore.validations.groups.OrderedValidation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/** REST controller for endpoints related to favourites */
@RestController
@RequestMapping(path = "/users")
@Validated(OrderedValidation.class)
public class FavouriteController {
    private static final Logger log = LoggerFactory.getLogger(FavouriteController.class);
    private final FavouriteService favouriteService;
    private final AuthUtils authUtils;

    public FavouriteController(FavouriteService favouriteService, AuthUtils authUtils) {
        this.favouriteService = favouriteService;
        this.authUtils = authUtils;
    }

    /**
     * Retrieves a list of favourites for a specific user
     *
     * @param userID
     *            the ID of the user whose favourites are to be retrieved
     * @param pageable
     *            the pagination information (optional)
     * @return a page of favourites for the specified user
     */
    @IsAdmin
    @GetMapping("/{userID}/favourites")
    public ResponseEntity<Page<FavouriteReadDTO>> getFavourites(
            @PathVariable @Positive(groups = BasicValidation.class) @ExistsResource(repository = UserRepository.class, message = "User with given ID does not exist", groups = ExtendedValidation.class) Integer userID,
            Pageable pageable) {
        log.info("GET /users/{}/favourites", userID);

        return ResponseEntity.ok(favouriteService.getFavouritesByUserID(userID, pageable));
    }

    /**
     * Adds a new favourite book for a specific user
     *
     * @param userID
     *            the ID of the user to whom the favourite belongs
     * @param favouriteWriteDTO
     *            the favourite book to be added
     * @return the created favourite
     */
    @ApiResponse(responseCode = "201")
    @IsAdmin
    @PostMapping("/{userID}/favourites")
    public ResponseEntity<FavouriteReadDTO> addFavourite(
            @PathVariable @Positive(groups = BasicValidation.class) @ExistsResource(repository = UserRepository.class, message = "User with given ID does not exist", groups = ExtendedValidation.class) Integer userID,
            @RequestBody @Valid @UniqueCompositeFields(repository = FavouriteRepository.class, methodName = "existsByUser_IdAndBook_Id", pathVariable = "userID", dtoClass = FavouriteWriteDTO.class, dtoFieldName = "bookID", message = "This book is already in the user's favourites", groups = ExtendedValidation.class) FavouriteWriteDTO favouriteWriteDTO) {
        log.info("POST /users/{}/favourites: favouriteWriteDTO={}", userID, favouriteWriteDTO);

        Optional<FavouriteReadDTO> savedFavourite = favouriteService.addFavourite(userID, favouriteWriteDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedFavourite.orElseThrow(() -> new FavouriteCreationException("Favourite creation failed")));
    }

    /**
     * Deletes a favourite book for a specific user
     *
     * @param userID
     *            the ID of the user whose favourite is to be deleted
     * @param bookID
     *            the ID of the favourite book to be deleted
     * @return a response entity with no content
     */
    @ApiResponse(responseCode = "204")
    @IsAdmin
    @DeleteMapping("/{userID}/favourites/{bookID}")
    public ResponseEntity<Void> deleteFavourite(@PathVariable @Positive(groups = BasicValidation.class) Integer userID,
            @PathVariable @Positive(groups = BasicValidation.class) @ExistsCompositeResource(repository = FavouriteRepository.class, methodName = "existsByUser_IdAndBook_Id", firstPathVariable = "userID", secondPathVariable = "bookID", message = "The book with the given ID is not in the given user's favourites", groups = ExtendedValidation.class) Integer bookID) {
        log.info("DELETE /users/{}/favourites/{}", userID, bookID);

        favouriteService.deleteFavourite(userID, bookID);

        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves the favourites of the currently authenticated user
     *
     * @param pageable
     *            the pagination information (optional)
     * @return a page of favourites for the authenticated user
     */
    @GetMapping("/me/favourites")
    public ResponseEntity<Page<FavouriteReadDTO>> getFavourites(Pageable pageable) {
        log.info("GET /users/me/favourites");

        Integer userID = authUtils.getUserIdFromAuthentication();

        return ResponseEntity.ok(favouriteService.getFavouritesByUserID(userID, pageable));
    }

    /**
     * Adds a new favourite book for the currently authenticated user
     *
     * @param favouriteWriteDTO
     *            the favourite book to be added
     * @return the created favourite
     */
    @ApiResponse(responseCode = "201")
    @PostMapping("/me/favourites")
    public ResponseEntity<FavouriteReadDTO> addFavourite(
            @RequestBody @UniqueCompositeFields(repository = FavouriteRepository.class, methodName = "existsByUser_IdAndBook_Id", usePathVariable = false, dtoClass = FavouriteWriteDTO.class, dtoFieldName = "bookID", message = "This book is already in your favourites", groups = ExtendedValidation.class) FavouriteWriteDTO favouriteWriteDTO) {
        log.info("POST /users/me/favourites: favouriteWriteDTO={}", favouriteWriteDTO);

        Integer userID = authUtils.getUserIdFromAuthentication();

        Optional<FavouriteReadDTO> savedFavourite = favouriteService.addFavourite(userID, favouriteWriteDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedFavourite.orElseThrow(() -> new FavouriteCreationException("Favourite creation failed")));
    }

    /**
     * Deletes a favourite book for the currently authenticated user
     *
     * @param bookID
     *            the ID of the favourite book to be deleted
     * @return a response entity with no content
     */
    @ApiResponse(responseCode = "204")
    @DeleteMapping("/me/favourites/{bookID}")
    public ResponseEntity<Void> deleteFavourite(
            @PathVariable @Positive(groups = BasicValidation.class) @ExistsCompositeResource(repository = FavouriteRepository.class, methodName = "existsByUser_IdAndBook_Id", useJWT = true, secondPathVariable = "bookID", message = "The book with the given ID is not in your favourites", groups = ExtendedValidation.class) Integer bookID) {
        log.info("DELETE /users/me/favourites/{}", bookID);

        Integer userID = authUtils.getUserIdFromAuthentication();

        favouriteService.deleteFavourite(userID, bookID);

        return ResponseEntity.noContent().build();
    }
}
