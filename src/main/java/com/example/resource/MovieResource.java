package com.example.resource;

import com.example.dto.MovieDto;
import com.example.dto.Movies;
import com.example.entity.Movie;
import com.example.repository.MovieRepository;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Path("/movies")
public class MovieResource {

    @Context
    UriInfo uriInfo;
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
    public MovieDto one(@PathParam("uuid") UUID uuid) {
        Optional<Movie> m = movieRepository.getByUuid(uuid);
        if (m.isPresent()) {
            Movie movie = m.get();
            return MovieDto.map(movie);
        } else throw new NotFoundException("No movie found with UUID " + uuid);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(@Valid MovieDto movieDto) {
        var movie = movieRepository.add(MovieDto.map(movieDto));
        return Response.created(URI.create(uriInfo.getAbsolutePath().toString() + "/" + movie.getUuid())).build();
    }

    @DELETE
    @Path("/{uuid}")
    public Response delete(@PathParam("uuid") UUID uuid) {
        if (movieRepository.getByUuid(uuid).isPresent()) {
            movieRepository.deleteByUuid(uuid);
            return Response.ok("Successfully deleted").build();
        } else if (!movieRepository.getByUuid(uuid).isPresent()) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("No movie found with UUID " + uuid)
                .build();
        } else return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity("Failed to delete movie with UUID: " + uuid)
            .build();
    }

    @PUT
    @Path("/{uuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateOne(@PathParam("uuid") UUID uuid, MovieDto movie) {
        Optional<Movie> m = movieRepository.getByUuid(uuid);
        if (m.isPresent()) {
            movieRepository.replace(uuid, MovieDto.map(movie));
            return Response.ok("Successfully updated").build();
        } else if (!m.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("No movie found with UUID " + uuid)
                .build();
        } else return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity("Failed to update movie with UUID: " + uuid)
            .build();
    }

}
