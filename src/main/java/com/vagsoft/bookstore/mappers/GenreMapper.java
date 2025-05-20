package com.vagsoft.bookstore.mappers;

import com.vagsoft.bookstore.dto.GenreDTO;
import com.vagsoft.bookstore.models.entities.Genre;
import org.mapstruct.Mapper;
/**
 * Mapper class for converting Genre entities and DTOs
 */
@Mapper(componentModel = "spring")
public interface GenreMapper {

    /**
     * Converts a GenreDTO to a Genre entity.
     *
     * @param genreDTO the GenreDTO to be converted
     * @return the converted Genre entity
     */
    Genre DtoToGenre(GenreDTO genreDTO);

    /**
     * Converts a Genre entity to a GenreDTO.
     *
     * @param genre the Genre entity to be converted
     * @return the converted GenreDTO
     */
    GenreDTO GenreToDto(Genre genre);
}
