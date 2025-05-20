package com.vagsoft.bookstore.validators;

import com.vagsoft.bookstore.annotations.UniqueGenresPerBook;
import com.vagsoft.bookstore.annotations.UniqueUserFavourite;
import com.vagsoft.bookstore.dto.FavouriteWriteDTO;
import com.vagsoft.bookstore.repositories.BookRepository;
import com.vagsoft.bookstore.repositories.FavouriteRepository;
import com.vagsoft.bookstore.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Validator class for {@link UniqueUserFavourite}
 */
public class UniqueUserFavouriteValidator implements ConstraintValidator<UniqueUserFavourite, FavouriteWriteDTO> {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private HttpServletRequest request;

    private final FavouriteRepository favouriteRepository;

    public UniqueUserFavouriteValidator(FavouriteRepository favouriteRepository) {
        this.favouriteRepository = favouriteRepository;
    }


    @Override
    public boolean isValid(FavouriteWriteDTO favouriteWriteDTO, ConstraintValidatorContext context) {
        Integer userID;
        if (request.getRequestURI().contains("/me")) {
            try {
                Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

                userID = Integer.valueOf(jwt.getClaimAsString("id"));
            } catch (Exception e) {
                throw new IllegalStateException("No JWT token found in authenticated request");
            }
        } else {
            userID = RequestUtils.getPathVariable(request, "userID", Integer.class);
        }

        return !favouriteRepository.existsByUserIDAndBook_Id(userID, favouriteWriteDTO.getBookID());
    }
}
