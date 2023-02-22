package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import com.reactivespring.service.MovieInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.Null;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
public class MoviesInfoControllerUnitTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    MovieInfoService movieInfoServiceMock;

    private static String MOVIES_INFO_URL = "/v1/movieinfos/";

    @Test
    void getAllMovieInfos(){
        var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

            when(movieInfoServiceMock.getAllMovieInfo()).thenReturn(Flux.fromIterable(movieinfos));

            webTestClient
                    .get()
                    .uri(MOVIES_INFO_URL)
                    .exchange()
                    .expectStatus()
                    .is2xxSuccessful()
                    .expectBodyList(MovieInfo.class)
                    .hasSize(3);
    }

    @Test
    void getAllMovieInfoById(){
        var movieinfos = new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));

        var movieId = "abc";
        when(movieInfoServiceMock.getAllMovieInfoById(movieId)).thenReturn(Mono.just(movieinfos));


        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "{id}", movieId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Dark Knight Rises");
    }

    @Test
    void addMoviesInfo(){
        var movieInfo = new MovieInfo(null, "Dark Knight Rises",
                2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));

        when(movieInfoServiceMock.addMovieInfo(isA(MovieInfo.class))).thenReturn(Mono.just(
                new MovieInfo("mockId", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"))
        ));

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
                    assertEquals ("mockId", savedMovieInfo.getMovieInfoId());
                });
    }

    @Test
    void updateMovieInfo() {

        var movieId = "abc";
        var movieInfo = new MovieInfo(null, "Batman goes home",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        when(movieInfoServiceMock.updateMovieInfoById(isA(MovieInfo.class),isA(String.class))).thenReturn(Mono.just(
                new MovieInfo(movieId, "Batman goes home",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"))
        ));

        webTestClient
                .put()
                .uri(MOVIES_INFO_URL +"{id}", movieId)
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
        when(movieInfoServiceMock.deleteMovieInfoById(isA(String.class))).thenReturn(Mono.empty());
        webTestClient
                .delete()
                .uri(MOVIES_INFO_URL + "{id}", movieId)
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
    void addMoviesInfo_validation(){
        var movieInfo = new MovieInfo(null, "",
                -2012, List.of(""), LocalDate.parse("2012-07-20"));


        webTestClient
                .post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                    var response = stringEntityExchangeResult.getResponseBody();
                    assert response!=null;
                    var errorMessage = "movieInfod.year must be positive,movieInfos.cast must be present,movieInfos.name must be present";
                    assertEquals(errorMessage, response);
                });
//                .expectBody(MovieInfo.class)
//                .consumeWith(movieInfoEntityExchangeResult -> {
//                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
//                    assert savedMovieInfo != null;
//                    assertEquals ("mockId", savedMovieInfo.getMovieInfoId());
//                });
    }

}
