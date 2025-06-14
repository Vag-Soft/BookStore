package com.vagsoft.bookstore.dto.favouriteDTOs;

import com.vagsoft.bookstore.dto.bookDTOs.BookReadDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavouriteReadDTO {
    private BookReadDTO book;
}
