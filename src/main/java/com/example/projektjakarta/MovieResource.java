package com.example.projektjakarta;

import com.example.dto.MovieDto;
import com.example.dto.Movies;
import com.example.entity.Movie;
import com.example.repository.MovieRepository;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{uuid}")
    public Response one(@PathParam("uuid") UUID uuid) {
        Movie movie = movieRepository.findByUuid(uuid);
        if (movie != null) {
            return Response.ok(MovieDto.map(movie)).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("No movie found with UUID " + uuid)
                .build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@Valid MovieDto movieDto) {
        var movie = movieRepository.add(MovieDto.map(movieDto));
        return Response.created(URI.create("movies/" + movie.getUuid().toString())).build();
    }

    @DELETE
    @Path("/{uuid}")
    public Response delete(@PathParam("uuid") UUID uuid) {
        Response movieDeleteResponse = movieRepository.deleteByUuid(uuid);
        if (movieDeleteResponse.getStatus() == Response.Status.OK.getStatusCode()) {
            return Response.ok("Movie successfully deleted").build();
        } else if (movieDeleteResponse.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("No movie found with UUID " + uuid)
                .build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity("Failed to delete movie with UUID: " + uuid)
            .build();
    }
}


