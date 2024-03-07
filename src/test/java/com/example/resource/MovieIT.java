package com.example.resource;

import com.example.dto.MovieDto;
import com.example.dto.Movies;
import com.example.entity.Movie;
import com.example.repository.MovieRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class MovieResourceTestIT {
    //https://java.testcontainers.org/modules/docker_compose/#adding-this-module-to-your-project-dependencies

    @Inject
    MovieRepository movieRepository;

    @Container
    public static ComposeContainer environment =
        new ComposeContainer(new File("src/test/resources/compose-test.yml"))
            .withExposedService("wildfly", 8080, Wait.forHttp("/api/movies")
                .forStatusCode(200))
            .withLocalCompose(true);

    static String host;
    static int port;
    static Movies movies;

    @BeforeAll
    static void beforeAll() {
        host = environment.getServiceHost("wildfly", 8080);
        port = environment.getServicePort("wildfly", 8080);
    }

    @BeforeEach
    void before() {
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.baseURI = "http://" + host + "/api";
        RestAssured.port = port;
    }


    //GET
    @Test
    @DisplayName("given movies is empty when calling get movies then return an empty list")
    void givenMoviesIsEmptyWhenCallingGetMoviesThenReturnAnEmptyList() {

        Movies movies = RestAssured.get("/movies").then()
            .extract()
            .as(Movies.class);
        movies.movieDtos().clear();
        assertEquals(List.of(),movies.movieDtos());
    }

    @Test
    @DisplayName("request read should return status200")
    void requestReadShouldReturnStatus200() {
        movies = RestAssured.get("/movies").then()
            .statusCode(200)
            .extract()
            .as(Movies.class);

    }

    @Test
    @DisplayName("Request for create response Status code 201")
    void requestForCreateResponseStatusCode201() {

        String requestBody = "{"
            + "\"director\": \"frank Zappa\","
            + "\"genre\": \"Horror\","
            + "\"rating\": 3.3,"
            + "\"releaseYear\": 1985,"
            + "\"title\": \"Friday the 13:th\""
            + "}";

        RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestBody)
            .when()
            .post("/movies")
            .then()
            .statusCode(201);
    }

    @Test
    @DisplayName("Request for create response Status code 201 and validate attributes of the movie created")
    void requestForCreateResponseStatusCode201AndValidateAttributesOfTheMovieCreated() {

        UUID uuid = UUID.randomUUID();
        Movie movie = createMovie(uuid, "frank Zappa", "Horror", 3.3f, 1985, "Friday the 13:th");
        String requestBody = convertToJson(movie);

        RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestBody)
            .when()
            .post("/movies")
            .then()
            .statusCode(201);

        MovieDto addedMovieDto = RestAssured
            .get("/movies/" + movie.getUuid())
            .then()
            .statusCode(200) // Validate that the status code is 200 OK
            .extract()
            .as(MovieDto.class);

        Movie addedMovie = MovieDto.map(addedMovieDto);

        assertEquals("frank Zappa", addedMovie.getDirector());
        assertEquals("Horror", addedMovie.getGenre());
        assertEquals(3.3, addedMovie.getRating(), 0.01);
        assertEquals(1985, addedMovie.getReleaseYear());
        assertEquals("Friday the 13:th", addedMovie.getTitle());
    }

    @Test
    @DisplayName("shouldUpdateMovieAndReturnUpdatedMovieDetails")
    void shouldUpdateMovieAndReturnUpdatedMovieDetails() {
        UUID uuid = UUID.randomUUID();
        Movie movie = createMovie(uuid, "frank Zappa", "Horror", 3.3f, 1985, "Friday the 13:th");
        String requestBody = convertToJson(movie);

        RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestBody)
            .when()
            .post("/movies")
            .then()
            .statusCode(201);

        String requestBody2 = "{"
            + "\"director\": \"Updated Director\","
            + "\"genre\": \"Updated Genre\","
            + "\"rating\": 4.5,"
            + "\"releaseYear\": 2000,"
            + "\"title\": \"Updated Title\""
            + "}";

        RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestBody2)
            .when()
            .put("/movies/" + movie.getUuid())
            .then()
            .statusCode(200)
            .body(equalTo("Successfully updated"));

        MovieDto updatedMovieDto = RestAssured
            .get("/movies/" + movie.getUuid())
            .then()
            .statusCode(200)
            .extract()
            .as(MovieDto.class);

        Movie updatedMovie = MovieDto.map(updatedMovieDto);

        assertEquals(uuid, updatedMovie.getUuid());
        assertEquals("Updated Director", updatedMovie.getDirector());
        assertEquals("Updated Genre", updatedMovie.getGenre());
        assertEquals(4.5, updatedMovie.getRating(), 0.01); // Compare floating point numbers with tolerance
        assertEquals(2000, updatedMovie.getReleaseYear());
        assertEquals("Updated Title", updatedMovie.getTitle());
    }


    @Test
    @DisplayName("shouldReturnNotFoundWhenUpdatingNonExistingMovie")
    void shouldReturnNotFoundWhenUpdatingNonExistingMovie() {
        UUID uuid = UUID.randomUUID();
        String requestBody = "{"
            + "\"director\": \"Updated Director\","
            + "\"genre\": \"Updated Genre\","
            + "\"rating\": 4.5,"
            + "\"releaseYear\": 2000,"
            + "\"title\": \"Updated Title\""
            + "}";

        RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestBody)
            .when()
            .put("/movies/" + uuid)
            .then()
            .statusCode(404)
            .body(equalTo("No movie found with UUID " + uuid));

    }

    @Test
    @DisplayName("Delete should return status 200")
    void deleteShouldReturnStatus200() {

        UUID uuid = UUID.randomUUID();
        Movie movie = createMovie(uuid, "frank Zappa", "Horror", 3.3f, 1985, "Friday the 13:th");
        String requestBody = convertToJson(movie);

        RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestBody).when()
            .post("/movies/")
            .then()
            .statusCode(201);

        RestAssured.given()
            .when()
            .delete("/movies/" + movie.getUuid())
            .then()
            .statusCode(200 )
            .body(equalTo("Successfully deleted"));

    }

    @Test
    @DisplayName("Delete without uuid should return 405")
    void deleteWithoutUuidShouldReturn405() {
        UUID uuid = UUID.randomUUID();
        Movie movie = createMovie(uuid, "frank Zappa", "Horror", 3.3f, 1985, "Friday the 13:th");
        String requestBody = convertToJson(movie);

        RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestBody).when()
            .post("/movies/")
            .then()
            .statusCode(201);

        RestAssured.given()
            .when()
            .delete("/movies/" )
            .then()
            .statusCode(405 );

    }

    @Test
    @DisplayName("Delete with wrong uuid should return 404")
    void deleteWithWrongUuidShouldReturn404() {
        UUID uuid = UUID.randomUUID();
        RestAssured.given()
            .when()
            .delete("/movies/" + uuid)
            .then()
            .statusCode(404 )
            .body(equalTo("No movie found with UUID " + uuid));
    }


    @NotNull
    private static Movie createMovie(UUID uuid, String director, String genre, float rating, int year, String title) {
        Movie movie = new Movie();
        movie.setUuid(uuid);
        movie.setDirector(director);
        movie.setGenre(genre);
        movie.setRating(rating);
        movie.setReleaseYear(year);
        movie.setTitle(title);
        return movie;
    }

    private static String convertToJson(Movie movie) {
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = null;
        try {
            requestBody = objectMapper.writeValueAsString(movie);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return requestBody;
    }


}




