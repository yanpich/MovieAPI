package com.movieflix.movieapi.service;

import com.movieflix.movieapi.dto.MovieDto;
import com.movieflix.movieapi.dto.MoviePageResponse;
import com.movieflix.movieapi.entity.Movie;
import com.movieflix.movieapi.exceptions.MovieNotFoundException;
import com.movieflix.movieapi.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final FileService fileService;

    @Value("${project.poster:/default/path}")
    private String posterPath;
    @Value("${base.url}")
    private String baseUrl;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }

    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {

        // 1. upload the file
        String uploadFileName = fileService.uploadFile(path, file);

        // 2. set the value of field 'poster' as filename
        movieDto.setPoster(uploadFileName);

        // 3. map dto to Movie object - WITHOUT THE ID!
        Movie movie = new Movie(
                movieDto.getTitle(),        // Don't pass the ID
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );

        // 4. save the movie object -> saved movie object
        Movie savedMovie = movieRepository.save(movie);

        // 5. generate the posterUrl
        String posterUrl = baseUrl + "/file/" + uploadFileName;

        // 6. map movie object to DTO object and return it
        MovieDto response = new MovieDto(
                savedMovie.getMovieId(),    // Use the generated ID
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getMovieCast(),
                savedMovie.getReleaseYear(),
                savedMovie.getPoster(),
                posterUrl
        );
        return response;
    }

    @Override
    public MovieDto getMovie(Integer movieId) {
        //1. check the data in DB and if exists, fetch the data of given ID
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie not found!" + movieId));

        //2. generate posterUrl
        String posterUrl = baseUrl + "/file/" + movie.getPoster();

        //3. map to MovieDto object and return it
        MovieDto response = new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );
        return response;
    }
    @Override
    public List<MovieDto> getAllMovies() {
        //1. fetch all data from db
        List<Movie> movies = movieRepository.findAll();

        List<MovieDto> movieDtos = new ArrayList<>();

        //2. iterate through the list , generate posterUrl for each movie obj and map to MovieDto obj
        for (Movie movie : movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto movieDto = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(movieDto);
        }
        return movieDtos;
    }
    @Override
    public MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException {
        // 1. check if movie object exists with given movieId
        Movie existingMovie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found!" + movieId));

        // 2. handle file upload if new file is provided
        String fileName = existingMovie.getPoster();
        if (file != null && !file.isEmpty()) {
            // Delete old file
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            // Upload new file
            fileName = fileService.uploadFile(path, file);
        }

        // 3. Update the existing movie object (don't create a new one)
        existingMovie.setTitle(movieDto.getTitle());
        existingMovie.setDirector(movieDto.getDirector());
        existingMovie.setStudio(movieDto.getStudio());
        existingMovie.setMovieCast(movieDto.getMovieCast());
        existingMovie.setReleaseYear(movieDto.getReleaseYear());
        existingMovie.setPoster(fileName);

        // 4. save the updated movie object
        Movie updatedMovie = movieRepository.save(existingMovie);
        // 5. generate posterUrl
        String posterUrl = baseUrl + "/file/" + fileName;
        // 6. map to MovieDto and return it
        return new MovieDto(
                updatedMovie.getMovieId(),
                updatedMovie.getTitle(),
                updatedMovie.getDirector(),
                updatedMovie.getStudio(),
                updatedMovie.getMovieCast(),
                updatedMovie.getReleaseYear(),
                updatedMovie.getPoster(),
                posterUrl
        );
    }
    @Override
    public String deleteMovie(Integer movieId) throws IOException {
        // 1. check if movie object exists in DB
        Movie mv = movieRepository.findById(movieId)
                .orElseThrow(() ->  new MovieNotFoundException("Movie not found with id = " + movieId));
        Integer id = mv.getMovieId();
        // 2. delete the file associated with this object
        Files.deleteIfExists(Paths.get(path + File.separator + mv.getPoster()));
        // 3. delete the movie object
        movieRepository.delete(mv);
        return "Movie deleted with id = " + id;
    }
    @Override
    public MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {

        List<Movie>  movies = moviesPages.getContent();

        List<MovieDto> movieDtos = new ArrayList<>();

        // 2. iterate through the list, generate posterUrl for each movie obj, and map to MovieDto obj
        for (Movie movie : movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto movieDto = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(movieDto);
        }
        return  new MoviePageResponse(movieDtos, pageNumber, pageSize,
                moviesPages.getTotalElements(),
                moviesPages.getTotalPages(),
                moviesPages.isLast()
                );
    }
    @Override
    public MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String dir) {
    }

}
