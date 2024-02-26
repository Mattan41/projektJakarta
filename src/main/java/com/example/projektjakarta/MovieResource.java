package com.example.projektjakarta;

import com.example.dto.MovieDto;
import com.example.dto.Movies;
import com.example.repository.MovieRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.time.LocalDateTime;

@Path("/movies")
public class MovieResource {

    private MovieRepository movieRepository;

    public MovieResource() {
    }

    @Inject
    public MovieResource(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Movies all() {
        return new Movies(
            movieRepository.getAll().stream().map(MovieDto::map).toList(),
            LocalDateTime.now());
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(MovieDto movieDto){

        var m = movieRepository.add(MovieDto.map(movieDto));

        return Response.created(
                //Ask Jakarta application server for hostname and url path
                URI.create("http://localhost:8080/api/movies/" + m.getId()))
            .build();
    }
}
