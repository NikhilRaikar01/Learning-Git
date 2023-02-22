package com.reactivespring.router;

import com.reactivespring.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import javax.xml.validation.Validator;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ReviewRouter {

    @Bean
    public RouterFunction<ServerResponse> reviewsRoute(ReviewHandler reviewHandler){
        RouterFunctions.Builder route = route();
        route.nest(path("/v1/reviews"),builder -> {
                    builder.POST("", request -> reviewHandler.addReview(request));
                    builder.GET("",request -> reviewHandler.getReview(request));
                    builder.PUT("/{id}",request -> reviewHandler.updateReview(request));
                    builder.DELETE("/{id}",request -> reviewHandler.deleteReview(request));
                }
        );
        route.GET("/v1/hello", (request -> {
            return ServerResponse.ok().bodyValue("hello");
        }));
        return route
                .build();

    }

}
