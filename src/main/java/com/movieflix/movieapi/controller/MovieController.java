package com.movieflix.movieapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieflix.movieapi.dto.MovieDto;
import com.movieflix.movieapi.dto.MoviePageResponse;
import com.movieflix.movieapi.service.MovieService;
import com.movieflix.movieapi.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movie")
public class MovieController {
    private MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping("/add-movie")
    public ResponseEntity<MovieDto> addMovie(@RequestPart MultipartFile file,
                                             @RequestPart String movieDto) throws IOException {
    MovieDto dto =  convertToMovieDto(movieDto);
    return new ResponseEntity<>(movieService.addMovie(dto, file), HttpStatus.CREATED);

    }

    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieDto> updateMovieHandler(@PathVariable Integer movieId,
                                                       @RequestPart(required = false) MultipartFile file,
                                                       @RequestPart String movieDtoObj) throws IOException {
        if (file == null || file.isEmpty()) file = null;
        MovieDto movieDto = convertToMovieDto(movieDtoObj);
        return ResponseEntity.ok(movieService.updateMovie(movieId, movieDto, file));
    }

    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<String> deleteMovieHandler(@PathVariable Integer movieId) throws IOException {
        return ResponseEntity.ok(movieService.deleteMovie(movieId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MovieDto>> getAllMoviesHandler() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }
    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDto> getAllMoviesByIdHandler(@PathVariable Integer movieId) {
        return ResponseEntity.ok(movieService.getMovie(movieId));
    }
    @GetMapping("/allMoviesPage")
    public ResponseEntity<MoviePageResponse> getMoviesWithPagination(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize
    ){
        return ResponseEntity.ok(movieService.getAllMoviesWithPagination(pageNumber, pageSize));
    }
    @GetMapping("/allMoviesPageSort")
    public ResponseEntity<MoviePageResponse> getMoviesWithPaginationSorting(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR, required = false) String sortDir
    ){
        return ResponseEntity.ok(movieService.getAllMoviesWithPaginationAndSorting(pageNumber, pageSize, sortBy, sortDir));
    }






    private MovieDto convertToMovieDto(String movieDtoObj) throws JsonProcessingException {
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(movieDtoObj, MovieDto.class);
    }
}
