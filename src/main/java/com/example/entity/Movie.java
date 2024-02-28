package com.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "movie", schema = "movies")
public class Movie implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "uuid", nullable = false,updatable = false)
    private UUID uuid;

    @Size(max = 100)
    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "releaseYear")
    private Integer releaseYear;

    @Size(max = 50)
    @Column(name = "director", length = 50)
    private String director;

    @Column(name = "rating")
    private Float rating;

    @Size(max = 20)
    @Column(name = "genre", length = 20)
    private String genre;



    public UUID getUuid(){
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return getUuid() != null && Objects.equals(getUuid(), movie.getUuid());
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }
}
