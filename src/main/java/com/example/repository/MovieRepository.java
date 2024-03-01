package com.example.repository;

import com.example.entity.Movie;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class MovieRepository implements Serializable {

    @PersistenceContext(unitName = "mysql")
    EntityManager entityManager;

    public List<Movie> getAll() {
        return entityManager
            .createQuery("select m from Movie m", Movie.class)
            .getResultList();
    }

    @Transactional
    public Movie add(Movie movie) {
        UUID uuid = UUID.randomUUID();
        movie.setUuid(uuid);
        entityManager.merge(movie);
        return movie;
    }

    public Movie findById(int id) {
        return entityManager.find(Movie.class, id);
    }

    public Movie findByUuid(UUID uuid) {
        try {
            return entityManager.createQuery("SELECT m FROM Movie m WHERE m.uuid = :uuid", Movie.class)
                .setParameter("uuid", uuid)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Transactional
    public void replace(UUID uuid, Movie updatedMovie) {
        Movie movie = findByUuid(uuid);
        movie.setUuid(uuid);
        movie.setTitle(updatedMovie.getTitle());
        movie.setDirector(updatedMovie.getDirector());
        movie.setReleaseYear(updatedMovie.getReleaseYear());
        movie.setRating(updatedMovie.getRating());
        movie.setGenre(updatedMovie.getGenre());
        entityManager.persist(movie);
    }

    @Transactional
    public Response deleteByUuid(UUID uuid) {
        Movie movie = findByUuid(uuid);
        if (movie != null) {
            entityManager.remove(movie);
            return Response.ok("Movie successfully deleted").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("No movie found with UUID " + uuid)
                .build();
        }
    }
}
