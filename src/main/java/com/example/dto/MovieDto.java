package com.example.dto;


import com.example.entity.Movie;

public record MovieDto(String title, int releaseYear, String director, float rating, String genre) {

    public static MovieDto map(Movie movie){
        return new MovieDto(movie.getTitle(),movie.getReleaseYear(),movie.getDirector(), movie.getRating(),movie.getGenre());
    }

    public static Movie map(MovieDto movieDto){
        var movie =  new Movie();
        movie.setTitle(movieDto.title);
        movie.setDirector(movieDto.director);
        movie.setReleaseYear(movieDto.releaseYear);
        movie.setRating(movieDto.rating);
        movie.setGenre(movieDto.genre);
        return movie;
    }
}
