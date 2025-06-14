package com.vagsoft.bookstore.mappers;

import java.util.List;

import com.vagsoft.bookstore.dto.bookDTOs.BookReadDTO;
import com.vagsoft.bookstore.dto.bookDTOs.BookUpdateDTO;
import com.vagsoft.bookstore.dto.bookDTOs.BookWriteDTO;
import com.vagsoft.bookstore.dto.genreDTOs.GenreDTO;
import com.vagsoft.bookstore.models.entities.Book;
import com.vagsoft.bookstore.models.entities.Genre;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/** Mapper class for converting Book entities and DTOs. */
@Mapper(componentModel = "spring")
public interface BookMapper {
    /**
     * Converts a BookReadDTO to a Book entity.
     *
     * @param bookReadDTO
     *            the BookReadDTO to be converted
     * @return the converted Book entity
     */
    Book dtoToBook(BookReadDTO bookReadDTO);

    /**
     * Converts a BookWriteDTO to a Book entity.
     *
     * @param bookWriteDTO
     *            the BookWriteDTO to be converted
     * @return the converted Book entity
     */
    @Mapping(ignore = true, target = "bookUpdateDTO.id")
    Book dtoToBook(BookWriteDTO bookWriteDTO);

    /**
     * Converts a BookUpdateDTO to a Book entity.
     *
     * @param bookUpdateDTO
     *            the BookUpdateDTO to be converted
     * @return the converted Book entity
     */
    @Mapping(ignore = true, target = "bookUpdateDTO.id")
    Book dtoToBook(BookUpdateDTO bookUpdateDTO);

    /**
     * Converts a Book entity to a BookReadDTO.
     *
     * @param book
     *            the Book entity to be converted
     * @return the converted BookReadDTO
     */
    BookReadDTO bookToReadDto(Book book);

    /**
     * Converts a Book entity to a BookWriteDTO.
     *
     * @param book
     *            the Book entity to be converted
     * @return the converted BookWriteDTO
     */
    BookWriteDTO bookToWriteDto(Book book);

    /**
     * Converts a Book entity to a BookUpdateDTO.
     *
     * @param book
     *            the Book entity to be converted
     * @return the converted BookUpdateDTO
     */
    BookUpdateDTO bookToUpdateDto(Book book);

    /**
     * Updates a Book entity from a BookUpdateDTO, ignoring null values.
     *
     * @param bookUpdateDTO
     *            the BookUpdateDTO to update from
     * @param book
     *            the Book entity to be updated
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBookFromDto(BookUpdateDTO bookUpdateDTO, @MappingTarget Book book);

    /**
     * Links the books inside the genres of a Book entity with itself, after
     * mapping.
     *
     * @param dto
     *            the BookWriteDTO that was used in the mapping
     * @param book
     *            the Book entity that contains the genres
     */
    @AfterMapping
    default void linkGenres(BookWriteDTO dto, @MappingTarget Book book) {
        for (Genre genre : book.getGenres()) {
            genre.setBook(book);
        }
    }

    /**
     * Links the genre ids of the Book to the non-null genre ids of the
     * BookUpdateDTO, to be able to update the genres and avoid deletion.
     *
     * @param genreDto
     *            the BookUpdateDTO that was used in the mapping
     * @param book
     *            the Book entity that contains the genres
     */
    @BeforeMapping
    default void linkGenreIDs(BookUpdateDTO genreDto, @MappingTarget Book book) {
        for (GenreDTO dto : genreDto.getGenres()) {
            if (dto.getId() == null) {
                book.getGenres().stream().filter(g -> g.getGenre().equals(dto.getGenre())).findFirst()
                        .ifPresent(genre -> dto.setId(genre.getId()));
            }
        }
    }

    /**
     * Links the books inside the genres of a Book entity with itself, after
     * mapping.
     *
     * @param dto
     *            the BookUpdateDTO that was used in the mapping
     * @param book
     *            the Book entity that contains the genres
     */
    @AfterMapping
    default void linkGenreBooks(BookUpdateDTO dto, @MappingTarget Book book) {
        for (Genre genre : book.getGenres()) {
            genre.setBook(book);
        }
    }

    /**
     * Converts a list of Book entities to a list of BookReadDTOs.
     *
     * @param books
     *            the list of Book entities to be converted
     * @return the list of converted BookReadDTOs
     */
    List<BookReadDTO> listBookToListDto(List<Book> books);

    /**
     * Converts a page of Book entities to a page of BookReadDTOs.
     *
     * @param page
     *            the page of Book entities to be converted
     * @return the page of converted BookReadDTOs
     */
    default Page<BookReadDTO> pageBookToPageDto(Page<Book> page) {
        return new PageImpl<>(listBookToListDto(page.getContent()), page.getPageable(), page.getTotalElements());
    }
}
