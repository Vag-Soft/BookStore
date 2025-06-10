package com.vagsoft.bookstore.models.entities;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String genre;

    public Genre(final Integer id, final String genre) {
        this.id = id;
        this.genre = genre;
    }

    @Override
    public String toString() {
        return "Genre{" + "id=" + id + ", book={" + "id=" + book.getId() + ", title='" + book.getTitle() + '\''
                + ", author='" + book.getAuthor() + '\'' + ", description='" + book.getDescription() + '\'' + ", pages="
                + book.getPages() + ", price=" + book.getPrice() + ", availability=" + book.getAvailability()
                + ", isbn='" + book.getIsbn() + '\'' + "}" + ", genre='" + genre + '\'' + '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Genre genre1 = (Genre) o;
        return Objects.equals(id, genre1.id) && Objects.equals(book.getId(), genre1.book.getId())
                && Objects.equals(genre, genre1.genre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, book.getId(), genre);
    }
}
