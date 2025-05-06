package com.vagsoft.bookstore.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Genres")
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "bookID", nullable = false)
    private Book book;

    @Column(nullable = false)
    @NotBlank(message = "genre must not be blank")
    @Size(max = 31, message = "genre must be less than 32 characters")
    private String genre;

    public Genre(Integer id, String genre) {
        this.id = id;
        this.genre = genre;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "id=" + id +
                ", book={" +
                "id=" + book.getId() +
                ", title='" + book.getTitle() + '\'' +
                ", author='" + book.getAuthor() + '\'' +
                ", description='" + book.getDescription() + '\'' +
                ", pages=" + book.getPages() +
                ", price=" + book.getPrice() +
                ", availability=" + book.getAvailability() +
                ", isbn='" + book.getIsbn() + '\'' +
                "}" +
                ", genre='" + genre + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Genre genre1 = (Genre) o;
        return Objects.equals(id, genre1.id) && Objects.equals(book.getId(), genre1.book.getId()) && Objects.equals(genre, genre1.genre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, book.getId(), genre);
    }
}
