package com.reactivespring.client;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class MovieInfoRestClient {

    private WebClient webClient;

    public MovieInfoRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Value("${restClient.moviesInfoUrl}")
    private String moviesInfoUrl;

    public Mono<MovieInfo> getMovieInfobyId(String movieId){
        var url = moviesInfoUrl.concat("/{Id}");

        return webClient
                .get()
                .uri(url,movieId)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
                    log.error("Status code is :{}",clientResponse.statusCode().value());
                    if(clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(new MoviesInfoClientException(
                                "There is no Movie Info with id: "+ movieId, clientResponse.statusCode().value()));
                    }
                    return  clientResponse.bodyToMono(String.class)
                            .flatMap(errorMessage -> Mono.error(new MoviesInfoClientException(errorMessage, clientResponse.statusCode().value()))
                            );
                })
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
                    log.error("Status code is :{}",clientResponse.statusCode().value());
                    return  clientResponse.bodyToMono(String.class)
                            .flatMap(errorMessage -> Mono.error(new MoviesInfoServerException("Internal Server Exception in MovieInfo Service"))
                            );
                })
                .bodyToMono(MovieInfo.class)
                .log();
    }


}
