package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.test.StepVerifier;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class MoviesInfoControllerIntgTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;
    @Autowired
    WebTestClient webTestClient;

    private static String MOVIES_INFO_URL = "/v1/movieinfos";

    @BeforeEach
    void setUp() {
        var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        movieInfoRepository.saveAll(movieinfos)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void addMovieInfo() {

        var movieInfo = new MovieInfo(null, "Batman Begins Returns",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        webTestClient
                .post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert savedMovieInfo != null;
                    assert savedMovieInfo.getMovieInfoId() != null;
                });
    }

//    @Test
//    void getAllMovieInfo() {
//        webTestClient
//                .get()
//                .uri(MOVIES_INFO_URL)
//                .exchange()
//                .expectStatus()
//                .is2xxSuccessful()
//                .expectBodyList(MovieInfo.class)
//                .hasSize(3);
//    }

    @Test
    void getAllMovieInfoById() {
        var movieId = "abc";
        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/{id}", movieId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Dark Knight Rises");
//                .expectBody(MovieInfo.class)
//                .consumeWith(movieInfoEntityExchangeResult -> {
//                    var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
//                    assertNotNull(movieInfo);
//                });
    }

    @Test
    void updateMovieInfo() {

        var movieId = "abc";
        var movieInfo = new MovieInfo(null, "Batman goes home",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        webTestClient
                .put()
                .uri(MOVIES_INFO_URL +"/{id}", movieId)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var updatedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert updatedMovieInfo != null;
                    assert updatedMovieInfo.getMovieInfoId() != null;
                    assertEquals(updatedMovieInfo.getName(), "Batman goes home");
                });
    }

    @Test
    void deleteMovieInfoById() {
        var movieId = "abc";
        webTestClient
                .delete()
                .uri(MOVIES_INFO_URL + "/{id}", movieId)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody()
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assertNull(movieInfo);
                });
    }

    @Test
    void updateMovieInfo_notFound() {

        var movieId = "def";
        var movieInfo = new MovieInfo(null, "Batman goes home",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        webTestClient
                .put()
                .uri(MOVIES_INFO_URL +"/{id}", movieId)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void getAllMovieInfoById_notFound() {
        var movieId = "def";
        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/{id}", movieId)
                .exchange()
                .expectStatus()
                .isNotFound();

    }

    @Test
    void findByYear(){
        var movieInfoFlux = movieInfoRepository.findByYear(2005);
        StepVerifier.create(movieInfoFlux)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getMovieInfoByYear() {

        var uri = UriComponentsBuilder.fromUriString(MOVIES_INFO_URL)
                        .queryParam("year",2005)
                        .buildAndExpand().toUri();
        webTestClient
                .get()
                .uri(uri.toString())
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
    }

    @Test
    void getMovieInfoByName() {

        URI uri = UriComponentsBuilder.fromUriString(MOVIES_INFO_URL)
                .queryParam("name","Batman Begins")
                .buildAndExpand().toUri();
        webTestClient
                .get()
                .uri(uri.toString())
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(0);
    }
}