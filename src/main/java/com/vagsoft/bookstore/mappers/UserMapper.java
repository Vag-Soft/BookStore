package com.vagsoft.bookstore.mappers;

import com.vagsoft.bookstore.dto.BookReadDTO;
import com.vagsoft.bookstore.dto.UserReadDTO;
import com.vagsoft.bookstore.dto.UserUpdateDTO;
import com.vagsoft.bookstore.models.Book;
import com.vagsoft.bookstore.models.User;
import org.mapstruct.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

/**
 * Mapper class for converting User entities and DTOs
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    /**
     * Converts a UserReadDTO to a User entity
     *
     * @param userReadDTO the UserReadDTO to be converted
     * @return the converted User entity
     */
    @Mapping(ignore = true, target = "userReadDTO.id")
    User DtoToUser (UserReadDTO userReadDTO);



    /**
     * Converts a User entity to a UserReadDTO
     *
     * @param user the User entity to be converted
     * @return the converted UserReadDTO
     */
    UserReadDTO UserToReadDto(User user);


    /**
     * Updates a User entity from a UserUpdateDTO, ignoring null values
     *
     * @param userUpdateDTO the UserUpdateDTO to update from
     * @param user the User entity to be updated
     */
    @Mapping(ignore = true, target = "userUpdateDTO.id")
    @Mapping(source = "userUpdateDTO.password", target = "user.hashPassword")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UserUpdateDTO userUpdateDTO, @MappingTarget User user);


    /**
     * Converts a list of User entities to a list of UserReadDTOs
     *
     * @param users the list of User entities to be converted
     * @return the list of converted UserReadDTOs
     */
    List<UserReadDTO> ListUserToListDto(List<User> users);

    /**
     * Converts a page of User entities to a page of UserReadDTOs
     *
     * @param page the page of User entities to be converted
     * @return the page of converted UserReadDTOs
     */
    default Page<UserReadDTO> PageUserToPageDto(Page<User> page) {
        return new PageImpl<>(ListUserToListDto(page.getContent()), page.getPageable(), page.getTotalElements());
    }
}
