package com.example.projektjakarta;


import com.example.dto.Movies;

import org.junit.jupiter.api.*;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;



import java.io.File;

import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;



@Testcontainers
class MovieResourceTest {


        //https://java.testcontainers.org/modules/docker_compose/#adding-this-module-to-your-project-dependencies
        @Container
        public static ComposeContainer environment =
            new ComposeContainer(new File("src/test/resources/compose-test.yml"))
                .withExposedService("wildfly", 8080, Wait.forHttp("/api/movies")
                    .forStatusCode(200))
                .withLocalCompose(true);

        static String host;
        static int port;

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


//    @Test
//    @DisplayName("Test name")
//    void testName() {
//        Movies movies = RestAssured.get("/movies").then()
//            .statusCode(404)
//            .extract()
//            .as(Movies.class);
//        assertEquals(List.of(), movies.movieDtos());
//    }

    }
