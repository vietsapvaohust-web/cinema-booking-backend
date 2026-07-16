package com.cinema.booking.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cinema.booking.entity.Movie;

public interface MovieRepository extends JpaRepository<Movie, UUID> {

    List<Movie> findByStatus(String status);

    @Query("SELECT m FROM Movie m WHERE m.status = 'SHOWING' " +
           "AND (:search IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:genre IS NULL OR m.genre = :genre)")
    List<Movie> searchShowingMovies(@Param("search") String search, @Param("genre") String genre);

    @Query("SELECT DISTINCT m.genre FROM Movie m WHERE m.genre IS NOT NULL ORDER BY m.genre")
    List<String> findDistinctGenres();
}