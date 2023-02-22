package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.exceptionHandler.GlobalExceptionHandler;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.ReviewReactiveRepository;
import com.reactivespring.router.ReviewRouter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.Null;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class, GlobalExceptionHandler.class})
@AutoConfigureWebTestClient
public class ReviewsUnitTest {

    @MockBean
    private ReviewReactiveRepository reviewReactiveRepository;

    @Autowired
    private WebTestClient webTestClient;

    private static final String REVIEW_URL = "/v1/reviews";

    @Test
    void addReview() {
        var review = new Review(null, 1L, "Awesome Movie", 9.0);

        when(reviewReactiveRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));

        webTestClient
                .post()
                .uri(REVIEW_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedReview = movieInfoEntityExchangeResult.getResponseBody();
                    assert savedReview != null;
                    assert savedReview.getReviewId() != null;
                });
    }

    @Test
    void getAllReviews() {
        var review = List.of(
                new Review(null, 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie1", 9.0),
                new Review("1", 2L, "Excellent Movie", 8.0));

        when(reviewReactiveRepository.findAll())
                .thenReturn(Flux.fromIterable(review));

        webTestClient
                .get()
                .uri(REVIEW_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(3);
    }

    @Test
    void updateReview() {

        var reviewId = "1";
        var review = new Review("1", 1L, "Awesome Movie Updated", 9.0);


        when(reviewReactiveRepository.findById(isA(String.class)))
                .thenReturn(Mono.just(new Review("1", 1L, "Awesome Movie", 9.0)));

        when(reviewReactiveRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(new Review("1", 1L, "Awesome Movie Updated", 9.0)));

        webTestClient
                .put()
                .uri(REVIEW_URL +"/{id}", reviewId)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var updatedReview = reviewEntityExchangeResult.getResponseBody();
                    assert updatedReview != null;
                    assert updatedReview.getMovieInfoId() != null;
                    assertEquals(updatedReview.getComment(), "Awesome Movie Updated");
                });
    }

    @Test
    void deleteReviewById() {
        var reviewId = "1";

        when(reviewReactiveRepository.findById(isA(String.class)))
                .thenReturn(Mono.just(new Review("1", 1L, "Awesome Movie", 9.0)));

        when(reviewReactiveRepository.delete(isA(Review.class)))
                .thenReturn(Mono.empty());


        webTestClient
                .delete()
                .uri(REVIEW_URL + "/{id}", reviewId)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody()
                .consumeWith(reviewEntityExchangeResult -> {
                    var review = reviewEntityExchangeResult.getResponseBody();
                    assertNull(review);
                });
    }

    @Test
    void addReview_validate() {
        var review = new Review(null, null, "Awesome Movie", -9.0);

        when(reviewReactiveRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));

        webTestClient
                .post()
                .uri(REVIEW_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("review.MovieInfoId : Must not be Null,review.negative : rating is negative and please pass a non-negative value");
    }


}
