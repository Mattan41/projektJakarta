package com.example.dto;

import com.example.dto.MovieDto;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class movieDtoTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testValidMovieDto() {
        MovieDto movie = new MovieDto(UUID.randomUUID(), "Hello World!", 2000, "Bill Gates", 4.2f, "Drama");

        var violations = validator.validate(movie);

        assertEquals(0, violations.size());
    }

    @Test
    void testInvalidMovieDtoDueToMissingTitle() {
        MovieDto movie = new MovieDto(UUID.randomUUID(), "", 2000, "Bill Gates", 4.2f, "Drama");

        var violations = validator.validate(movie);

        assertEquals(1, violations.size());
    }

    @Test
    void testInvalidMovieDtoDueToMissingDirector() {
        MovieDto movie = new MovieDto(UUID.randomUUID(), "Hello World!", 2000, "", 4.2f, "Drama");

        var violations = validator.validate(movie);

        assertEquals(1, violations.size());
    }

    @Test
    void testInvalidMovieDtoDueToMissingTitleAndDirector() {
        MovieDto movie = new MovieDto(UUID.randomUUID(), "", 2000, "", 4.2f, "Drama");

        var violations = validator.validate(movie);

        assertEquals(2, violations.size());
    }

}
