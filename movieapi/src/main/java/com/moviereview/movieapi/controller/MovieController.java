package com.moviereview.movieapi.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import com.moviereview.movieapi.service.MovieService;

import com.moviereview.movieapi.dto.ClientMovieSearchResponseDto;
import com.moviereview.movieapi.dto.MovieDetailDto;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/search")
    public Mono<ClientMovieSearchResponseDto> searchMovies(@RequestParam("query") String query) {
        return movieService.searchMovies(query);
    }

    @GetMapping("/{movieId}")
    public Mono<MovieDetailDto> getMovieDetail(@PathVariable("movieId") String movieId) {
        return movieService.getMovieDetail(movieId);
    }
}