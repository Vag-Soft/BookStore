package com.vagsoft.bookstore.validators;

import com.vagsoft.bookstore.annotations.UniqueUserFavourite;
import com.vagsoft.bookstore.dto.FavouriteWriteDTO;
import com.vagsoft.bookstore.repositories.FavouriteRepository;
import com.vagsoft.bookstore.utils.AuthUtils;
import com.vagsoft.bookstore.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * Validator class for {@link UniqueUserFavourite}
 */
public class UniqueUserFavouriteValidator implements ConstraintValidator<UniqueUserFavourite, FavouriteWriteDTO> {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private AuthUtils authUtils;

    private final FavouriteRepository favouriteRepository;

    public UniqueUserFavouriteValidator(FavouriteRepository favouriteRepository) {
        this.favouriteRepository = favouriteRepository;
    }


    @Override
    public boolean isValid(FavouriteWriteDTO favouriteWriteDTO, ConstraintValidatorContext context) {
        Integer userID;
        if (request.getRequestURI().contains("/me")) {
            try {
                userID = authUtils.getUserIdFromAuthentication();
            } catch (Exception e) {
                throw new IllegalArgumentException("No JWT token found in authenticated request");
            }
        } else {
            userID = RequestUtils.getPathVariable(request, "userID", Integer.class);
        }

        return !favouriteRepository.existsByUserIDAndBook_Id(userID, favouriteWriteDTO.getBookID());
    }
}
