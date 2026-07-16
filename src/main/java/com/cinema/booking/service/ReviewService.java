package com.cinema.booking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cinema.booking.entity.Movie;
import com.cinema.booking.entity.Review;
import com.cinema.booking.entity.User;
import com.cinema.booking.repository.MovieRepository;
import com.cinema.booking.repository.ReviewRepository;
import com.cinema.booking.repository.UserRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository,
                          MovieRepository movieRepository,
                          UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
    }

    public List<Review> getReviewsForMovie(UUID movieId) {
        return reviewRepository.findByMovieIdOrderByCreatedAtDesc(movieId);
    }

    public double getAverageRating(UUID movieId) {
        List<Review> reviews = getReviewsForMovie(movieId);
        if (reviews.isEmpty()) return 0;
        return reviews.stream().mapToInt(Review::getRating).average().orElse(0);
    }

    // Tạo mới nếu chưa từng đánh giá, hoặc cập nhật đè nếu đã từng đánh giá phim này (upsert)
    public Review submitReview(UUID movieId, UUID userId, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating phải từ 1 đến 5 sao");
        }

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Phim không tồn tại"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));

        Review review = reviewRepository.findByMovieIdAndUserId(movieId, userId)
                .orElseGet(Review::new);

        review.setMovie(movie);
        review.setUser(user);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }
}