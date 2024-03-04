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
import java.util.Optional;
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


    public Optional<Movie> getByUuid(UUID uuid) {
        try {
            Movie movie = entityManager.createQuery("SELECT m FROM Movie m WHERE m.uuid = :uuid", Movie.class)
                .setParameter("uuid", uuid)
                .getSingleResult();
            return Optional.ofNullable(movie);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

//    public Movie findByUuid(UUID uuid) {
//        try {
//            return entityManager.createQuery("SELECT m FROM Movie m WHERE m.uuid = :uuid", Movie.class)
//                .setParameter("uuid", uuid)
//                .getSingleResult();
//        } catch (NoResultException e) {
//            return null;
//        }
//    }

    @Transactional
    public void replace(UUID uuid, Movie updatedMovie) {
        Movie movie = getByUuid(uuid).get();
        movie.setUuid(uuid);
        movie.setTitle(updatedMovie.getTitle());
        movie.setDirector(updatedMovie.getDirector());
        movie.setReleaseYear(updatedMovie.getReleaseYear());
        movie.setRating(updatedMovie.getRating());
        movie.setGenre(updatedMovie.getGenre());
        entityManager.persist(movie);
    }

    @Transactional
    public void deleteByUuid(UUID uuid) {
        Movie movie = getByUuid(uuid).get();
        entityManager.remove(movie);
    }
}
