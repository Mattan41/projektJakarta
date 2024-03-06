package com.example.resource;

import com.example.dto.Movies;

import io.restassured.RestAssured;
import jakarta.ws.rs.core.MediaType;
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


import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
class MovieResourceTestIT {
        //https://java.testcontainers.org/modules/docker_compose/#adding-this-module-to-your-project-dependencies
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
            RestAssured.baseURI = "http://" + host + "/api";
            RestAssured.port = port;
        }




    @Test
    @DisplayName("given movies is empty when calling get movies then return an empty list")
    void givenMoviesIsEmptyWhenCallingGetMoviesThenReturnAnEmptyList() {

        Movies movies = RestAssured.get("/movies").then()
            .extract()
            .as(Movies.class);

        assertEquals(List.of(), movies.movieDtos());
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


}




