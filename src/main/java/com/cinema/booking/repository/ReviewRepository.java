package com.cinema.booking.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cinema.booking.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByMovieIdOrderByCreatedAtDesc(UUID movieId);
    Optional<Review> findByMovieIdAndUserId(UUID movieId, UUID userId);
}