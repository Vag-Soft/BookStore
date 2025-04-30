package com.vagsoft.bookstore.errors.advices;

import com.vagsoft.bookstore.errors.exceptions.BookCreationException;
import com.vagsoft.bookstore.errors.exceptions.BookNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * Global controller advice that handles exceptions
 */
@RestControllerAdvice
public class GlobalControllerAdvice {
    private static final Logger log = LoggerFactory.getLogger(GlobalControllerAdvice.class);

    /**
     * Handles argument exceptions
     *
     * @param ex exception to handle of type {@link ConstraintViolationException}, {@link MethodArgumentTypeMismatchException}, {@link IllegalArgumentException}
     * @return a {@link ProblemDetail} with the error details
     */
    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentTypeMismatchException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleArgumentExceptions(Exception ex) {
        log.error("ArgumentException", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Invalid request parameters");
        return problemDetail;
    }

    /**
     * Handles validation exceptions
     *
     * @param ex the {@link MethodArgumentNotValidException} to handle
     * @return a {@link ProblemDetail} with the error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException", ex);

        String errorMessage = ex.getFieldErrors()
                        .stream()
                        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                        .collect(Collectors.joining("; "));

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errorMessage);
        problemDetail.setTitle("Invalid request body");
        return problemDetail;
    }

    /**
     * Handles book creation exceptions
     *
     * @param ex the {@link BookCreationException} to handle
     * @return a {@link ProblemDetail} with the error details
     */
    @ExceptionHandler(BookCreationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleBookCreationException(BookCreationException ex) {
        log.error("BookCreationException", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problemDetail.setTitle("Book creation failed");
        return problemDetail;
    }

    /**
     * Handles book not found exceptions
     *
     * @param ex the {@link BookNotFoundException} to handle
     * @return a {@link ProblemDetail} with the error details
     */
    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleBookNotFoundException(BookNotFoundException ex) {
        log.error("BookNotFoundException", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Book not found");
        return problemDetail;
    }

    /**
     * Handles other exceptions
     *
     * @param ex the {@link Exception} to handle
     * @return a {@link ProblemDetail} with the error details
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleException(Exception ex) {
        log.error("Exception", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problemDetail.setTitle("Internal server error");
        return problemDetail;
    }
}
