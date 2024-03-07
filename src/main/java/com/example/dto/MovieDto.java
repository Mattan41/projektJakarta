package com.example.dto;


import com.example.entity.Movie;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MovieDto(UUID uuid, @NotEmpty(message = "Title missing") String title, int releaseYear, @NotEmpty(message = "Director missing") String director, float rating, String genre) {

    public static MovieDto map(Movie movie){
        return new MovieDto(movie.getUuid(), movie.getTitle(), movie.getReleaseYear(), movie.getDirector(), movie.getRating(), movie.getGenre());
    }

    public static Movie map(MovieDto movieDto){
        var movie =  new Movie();
        movie.setUuid(movieDto.uuid);
        movie.setTitle(movieDto.title);
        movie.setDirector(movieDto.director);
        movie.setReleaseYear(movieDto.releaseYear);
        movie.setRating(movieDto.rating);
        movie.setGenre(movieDto.genre);
        return movie;
    }
}
