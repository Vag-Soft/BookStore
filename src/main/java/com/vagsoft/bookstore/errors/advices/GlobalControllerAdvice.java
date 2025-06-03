package com.vagsoft.bookstore.errors.advices;

import java.util.stream.Collectors;

import com.vagsoft.bookstore.errors.exceptions.ResourceCreationException;
import com.vagsoft.bookstore.errors.exceptions.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** Global controller advice that handles exceptions */
@RestControllerAdvice(basePackages = "com.vagsoft.bookstore")
public class GlobalControllerAdvice {
    private static final Logger log = LoggerFactory.getLogger(GlobalControllerAdvice.class);

    /**
     * Handles argument exceptions
     *
     * @param ex
     *            exception to handle of type {@link ConstraintViolationException},
     *            {@link IllegalArgumentException}, {@link ValidationException}
     * @return a {@link ProblemDetail} with the error details
     */
    @ExceptionHandler({ConstraintViolationException.class, IllegalArgumentException.class, ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleArgumentExceptions(Exception ex) {
        log.error("ArgumentException", ex);

        if (ex.getCause() instanceof ResourceNotFoundException) {
            return handleResourceNotFoundException((ResourceNotFoundException) ex.getCause()); // TODO: handle this case
                                                                                               // more gracefully
        }

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Invalid request parameters");
        return problemDetail;
    }

    /**
     * Handles validation exceptions
     *
     * @param ex
     *            the {@link MethodArgumentNotValidException} to handle
     * @return a {@link ProblemDetail} with the error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException", ex);

        String errorMessage = ex.getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errorMessage);
        problemDetail.setTitle("Invalid request body");
        return problemDetail;
    }

    /**
     * Handles invalid request body exceptions
     *
     * @param ex
     *            the {@link HttpMessageNotReadableException} to handle
     * @return a {@link ProblemDetail} with the error details
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("HttpMessageNotReadableException", ex);

        String errorMessage = "Invalid request body fields";

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errorMessage);
        problemDetail.setTitle("Invalid request body");
        return problemDetail;
    }

    /**
     * Handles type mismatch exceptions
     *
     * @param ex
     *            the {@link MethodArgumentTypeMismatchException} to handle
     * @return a {@link ProblemDetail} with the error details
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.error("TypeMismatchException", ex);

        String errorMessage = "'" + ex.getPropertyName() + "' has invalid type";

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errorMessage);
        problemDetail.setTitle("Invalid request parameters");
        return problemDetail;
    }

    /**
     * Handles resource creation exceptions
     *
     * @param ex
     *            the {@link ResourceCreationException} to handle
     * @return a {@link ProblemDetail} with the error details
     */
    @ExceptionHandler(ResourceCreationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleResourceCreationException(ResourceCreationException ex) {
        log.error("ResourceCreationException", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage());
        problemDetail.setTitle("Resource creation failed");
        return problemDetail;
    }

    /**
     * Handles resource not found exceptions
     *
     * @param ex
     *            the {@link ResourceNotFoundException} to handle
     * @return a {@link ProblemDetail} with the error details
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("ResourceNotFoundException", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Resource not found");
        return problemDetail;
    }

    /**
     * Handles data integrity exceptions
     *
     * @param ex
     *            the {@link DataIntegrityViolationException} to handle
     * @return a {@link ProblemDetail} with the error details
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleException(DataIntegrityViolationException ex) {
        log.error("DataIntegrityViolationException", ex);

        String errorMessage = "Data integrity violation";

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, errorMessage);
        problemDetail.setTitle("Internal server error");
        return problemDetail;
    }

    /**
     * Handles ObjectOptimisticLockingFailureException exceptions
     *
     * @param ex
     *            the {@link ObjectOptimisticLockingFailureException} to handle
     * @return a {@link ProblemDetail} with the error details
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException ex) {
        log.error("ObjectOptimisticLockingFailureException", ex);

        String errorMessage = "Resource with ID: " + ex.getIdentifier() + " does not exist";

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errorMessage);
        problemDetail.setTitle("Internal server error");
        return problemDetail;
    }

    /**
     * Handles bad credentials exceptions
     *
     * @param ex
     *            the {@link BadCredentialsException} to handle
     * @return a {@link ProblemDetail} with the error details
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ProblemDetail handleBadCredentialsException(BadCredentialsException ex) {
        log.error("BadCredentialsException", ex);

        String errorMessage = "Invalid username or password";

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, errorMessage);
        problemDetail.setTitle("Unauthorized");
        return problemDetail;
    }

    /**
     * Handles authorization denied exceptions
     *
     * @param ex
     *            the {@link AuthorizationDeniedException} to handle
     * @return a {@link ProblemDetail} with the error details
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ProblemDetail handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        log.error("AuthorizationDeniedException", ex);

        String errorMessage = "You do not have permission to access this resource";

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, errorMessage);
        problemDetail.setTitle("Access Denied");
        return problemDetail;
    }

    /**
     * Handles other exceptions
     *
     * @param ex
     *            the {@link Exception} to handle
     * @return a {@link ProblemDetail} with the error details
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleException(Exception ex) {
        log.error("Exception", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage());
        problemDetail.setTitle("Internal server error");
        return problemDetail;
    }
}
