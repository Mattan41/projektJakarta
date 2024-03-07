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
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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

    @AfterEach
    void cleanup() {
        Movies movies = RestAssured.get("/movies").then()
                .extract()
                .as(Movies.class);

        for (MovieDto movieDto : movies.movieDtos()) {
            RestAssured.delete("/movies/" + movieDto.uuid())
                    .then()
                    .statusCode(200);
        }
    }

    //GET
    @Test
    @DisplayName("given movies is empty when calling get movies then return an empty list")
    void givenMoviesIsEmptyWhenCallingGetMoviesThenReturnAnEmptyList() {

        Movies movies = RestAssured.get("/movies").then()
                .extract()
                .as(Movies.class);
        movies.movieDtos().clear();
        assertEquals(List.of(), movies.movieDtos());
    }

    @Test
    @DisplayName("given three movies are present when calling get movies then return a list of three movies")
    void givenMoviesArePresentWhenCallingGetMoviesThenReturnListOfMovies() {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        UUID uuid3 = UUID.randomUUID();
        createMovie(uuid1, "Frank Zappa", "Horror", 3.3f, 1985, "Friday the 13:th");
        createMovie(uuid2, "Steven Spielberg", "Adventure", 4.5f, 1993, "Jurassic Park");
        createMovie(uuid3, "Christopher Nolan", "Sci-Fi", 4.7f, 2010, "Inception");

        Movies movies = RestAssured.get("/movies").then()
                .extract()
                .as(Movies.class);
        assertFalse(movies.movieDtos().isEmpty());
        assertEquals(3, movies.movieDtos().size());

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
    @DisplayName("given movie with UUID does not exist when calling get movie then throw NotFoundException")
    void givenMovieWithUUIDDoesNotExistWhenCallingGetMovieThenThrowNotFoundException() {
        UUID uuid = UUID.randomUUID(); // This UUID does not exist in the database

        RestAssured.get("/movies/" + uuid).then()
                .statusCode(404)
                .body(equalTo("No movie found with UUID " + uuid));
    }


    //POST
    @Test
    @DisplayName("Request for create response Status code 201 with body message")
    void requestForCreateResponseStatusCode201WithBodyMessage() {

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
                .statusCode(201)
                .body(equalTo("Successfully added Movie"));
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

    //PUT
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

    @ParameterizedTest
    @MethodSource("provideInvalidTitleData")
    @DisplayName("given movie with invalid title should return status 400 and Validation error message")
    void givenMovieWithInvalidTitleShouldReturnStatus400AndValidationErrorMessage(String title, String errorMessage) {
        String requestBody = getRequestBodyForMissingTitle(title);

        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .when()
                .post("/movies")
                .then()
                .statusCode(400)
                .body("title", equalTo("Validation Errors"))
                .body("errors.field", hasItem("title"))
                .body("errors.violationMessage", hasItems(errorMessage));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidDirectorData")
    @DisplayName("given movie with invalid director should return status 400 and Validation error message")
    void givenMovieWithInvalidDirectorShouldReturnStatus400AndValidationErrorMessage(String director, String errorMessage) {
        String requestBody = getRequestBodyForMissingDirector(director);

        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .when()
                .post("/movies")
                .then()
                .statusCode(400)
                .body("title", equalTo("Validation Errors"))
                .body("errors.field", hasItem("director"))
                .body("errors.violationMessage", hasItems(errorMessage));
    }

    //DELETE
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
                .statusCode(200)
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
                .delete("/movies/")
                .then()
                .statusCode(405);

    }

    @Test
    @DisplayName("Delete with wrong uuid should return 404")
    void deleteWithWrongUuidShouldReturn404() {
        UUID uuid = UUID.randomUUID();
        RestAssured.given()
                .when()
                .delete("/movies/" + uuid)
                .then()
                .statusCode(404)
                .body(equalTo("No movie found with UUID " + uuid));
    }


    //EXTRACTED METHODS
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

    @NotNull
    private static String getRequestBodyForMissingDirector(String director) {
        String requestBody;
        if (director == null) {
            requestBody = "{"
                    + "\"genre\": \"Horror\","
                    + "\"rating\": 3.3,"
                    + "\"releaseYear\": 1985,"
                    + "\"title\": \"Friday the 13:th\""
                    + "}";
        } else {
            requestBody = "{"
                    + "\"director\": \"" + director + "\","
                    + "\"genre\": \"Horror\","
                    + "\"rating\": 3.3,"
                    + "\"releaseYear\": 1985,"
                    + "\"title\": \"Friday the 13:th\""
                    + "}";
        }
        return requestBody;
    }

    @NotNull
    private static String getRequestBodyForMissingTitle(String title) {
        String requestBody;
        if (title == null) {
            requestBody = "{"
                    + "\"director\": \"frank Zappa\","
                    + "\"genre\": \"Horror\","
                    + "\"rating\": 3.3,"
                    + "\"releaseYear\": 1985"
                    + "}";
        } else {
            requestBody = "{"
                    + "\"director\": \"frank Zappa\","
                    + "\"genre\": \"Horror\","
                    + "\"rating\": 3.3,"
                    + "\"releaseYear\": 1985,"
                    + "\"title\": \"" + title + "\""
                    + "}";
        }
        return requestBody;
    }

    private static Stream<Arguments> provideInvalidDirectorData() {
        return Stream.of(
                Arguments.of("", "Director missing"),
                Arguments.of(null, "Director missing")
        );
    }

    private static Stream<Arguments> provideInvalidTitleData() {
        return Stream.of(
                Arguments.of("", "Title missing"),
                Arguments.of(null, "Title missing")
        );
    }


}




