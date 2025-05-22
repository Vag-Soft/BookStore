package com.vagsoft.bookstore.mappers;

import com.vagsoft.bookstore.dto.FavouriteReadDTO;
import com.vagsoft.bookstore.dto.FavouriteWriteDTO;
import com.vagsoft.bookstore.models.entities.Favourite;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

/**
 * Mapper class for converting Favourite entities and DTOs
 */
@Mapper(componentModel = "spring")
public interface FavouriteMapper {
    Favourite dtoToFavourite(FavouriteWriteDTO favouriteWriteDTO);

    FavouriteReadDTO favouriteToReadDto(Favourite favourite);

    /**
     * Converts a list of Favourite entities to a list of FavouriteReadDTOs
     *
     * @param favourites the list of Favourite entities to be converted
     * @return the list of converted FavouriteReadDTOs
     */
    List<FavouriteReadDTO> listFavouriteToListDto(List<Favourite> favourites);

    /**
     * Converts a page of Favourite entities to a page of FavouriteReadDTOs
     *
     * @param page the page of Favourite entities to be converted
     * @return the page of converted FavouriteReadDTOs
     */
    default Page<FavouriteReadDTO> pageBookToPageDto(Page<Favourite> page) {
        return new PageImpl<>(listFavouriteToListDto(page.getContent()), page.getPageable(), page.getTotalElements());
    }
}
