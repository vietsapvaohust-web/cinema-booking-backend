package com.cinema.booking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cinema.booking.entity.Favorite;
import com.cinema.booking.entity.Movie;
import com.cinema.booking.entity.User;
import com.cinema.booking.repository.FavoriteRepository;
import com.cinema.booking.repository.MovieRepository;
import com.cinema.booking.repository.UserRepository;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    public FavoriteService(FavoriteRepository favoriteRepository,
                            MovieRepository movieRepository,
                            UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
    }

    public List<Movie> getFavoriteMovies(UUID userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(Favorite::getMovie)
                .toList();
    }

    public boolean isFavorite(UUID userId, UUID movieId) {
        return favoriteRepository.existsByUserIdAndMovieId(userId, movieId);
    }

    // Bật/tắt trạng thái yêu thích - nếu đã thích thì bỏ thích, chưa thích thì thêm vào
    public boolean toggleFavorite(UUID userId, UUID movieId) {
        var existing = favoriteRepository.findByUserIdAndMovieId(userId, movieId);

        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            return false; // giờ đã BỎ thích
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Phim không tồn tại"));

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setMovie(movie);
        favorite.setCreatedAt(LocalDateTime.now());
        favoriteRepository.save(favorite);

        return true; // giờ đã THÍCH
    }
}