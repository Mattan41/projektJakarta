package com.example.repository;

import com.example.dto.MovieDto;
import com.example.entity.Movie;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

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
        entityManager.persist(movie);
        return movie;
    }

    public Movie findById(int id) {
        return entityManager.find(Movie.class, id);
    }

    public Movie findByUuid(UUID uuid) {
        return entityManager.createQuery("SELECT m FROM Movie m WHERE m.uuid = :uuid", Movie.class).
            setParameter("uuid", uuid).
            getSingleResult();
    }

    public Movie update(UUID uuid, Movie movie) {
        var entity = entityManager.find(Movie.class, uuid);
        entity.setUuid(movie.getUuid());
        movie.setTitle(movie.getTitle());
        movie.setDirector(movie.getDirector());
        movie.setReleaseYear(movie.getReleaseYear());
        movie.setRating(movie.getRating());
        movie.setGenre(movie.getGenre());
        return entity;
    }

}
