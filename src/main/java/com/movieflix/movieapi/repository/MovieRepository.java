package com.movieflix.movieapi.repository;

import com.movieflix.movieapi.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Integer> {
    // free CURD Operations
}
