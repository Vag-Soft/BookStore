package com.vagsoft.bookstore.pagination;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Custom implementation of the PageImpl class
 *
 * @param <T>
 *            the type of the elements in the page
 */
public class CustomPageImpl<T> extends PageImpl<T> {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CustomPageImpl(@JsonProperty("content") List<T> content, @JsonProperty("number") int number,
            @JsonProperty("size") int size, @JsonProperty("totalElements") Long totalElements,
            @JsonProperty("pageable") JsonNode pageable, @JsonProperty("last") boolean last,
            @JsonProperty("totalPages") int totalPages, @JsonProperty("sort") JsonNode sort,
            @JsonProperty("numberOfElements") int numberOfElements) {
        super(content != null ? content : new ArrayList<>(), PageRequest.of(Math.max(0, number), Math.max(1, size)),
                totalElements != null ? totalElements : 0);
    }

    public CustomPageImpl(List<T> content, Pageable pageable, long total) {
        super(content != null ? content : new ArrayList<>(), pageable, total);
    }

    public CustomPageImpl(List<T> content) {
        super(content != null ? content : new ArrayList<>());
    }

    public CustomPageImpl() {
        super(new ArrayList<>());
    }
}
