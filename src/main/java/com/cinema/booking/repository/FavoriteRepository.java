package com.cinema.booking.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cinema.booking.entity.Favorite;
import com.cinema.booking.entity.FavoriteId;

public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {
    List<Favorite> findByUserId(UUID userId);
    Optional<Favorite> findByUserIdAndMovieId(UUID userId, UUID movieId);
    boolean existsByUserIdAndMovieId(UUID userId, UUID movieId);
}