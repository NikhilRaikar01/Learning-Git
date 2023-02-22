package com.reactivespring.controller;

import com.reactivespring.client.MovieInfoRestClient;
import com.reactivespring.client.ReviewRestClient;
import com.reactivespring.domain.Movie;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/movies")
public class MovieServiceController {

    MovieInfoRestClient movieInfoRestClient;
    ReviewRestClient reviewRestClient;

    public MovieServiceController(MovieInfoRestClient movieInfoRestClient, ReviewRestClient reviewRestClient) {
        this.reviewRestClient = reviewRestClient;
        this.movieInfoRestClient = movieInfoRestClient;
    }

    @GetMapping("/{id}")
    public Mono<Movie> getMovieByID(@PathVariable("id") String movieId){
        return movieInfoRestClient.getMovieInfobyId(movieId)
                .flatMap(movieInfo -> {
                    var reviewInfoMono = reviewRestClient.getReviewByMovieID(movieId)
                            .collectList();
                    return reviewInfoMono.map( reviews -> new Movie(movieInfo, reviews));
                });
    }
}
