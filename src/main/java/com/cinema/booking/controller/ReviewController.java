package com.cinema.booking.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.booking.entity.Review;
import com.cinema.booking.service.ReviewService;

@RestController
@RequestMapping("/api/movies/{movieId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    public record ReviewItem(UUID id, String userFullName, int rating, String comment, LocalDateTime createdAt) {
    }

    public record ReviewsResponse(List<ReviewItem> reviews, double averageRating, int totalReviews) {
    }

    public record SubmitReviewRequest(int rating, String comment) {
    }

    @GetMapping
    public ReviewsResponse getReviews(@PathVariable UUID movieId) {
        List<Review> reviews = reviewService.getReviewsForMovie(movieId);
        List<ReviewItem> items = reviews.stream()
                .map(r -> new ReviewItem(
                        r.getId(),
                        r.getUser().getFullName(),
                        r.getRating(),
                        r.getComment(),
                        r.getCreatedAt()
                ))
                .toList();

        return new ReviewsResponse(items, reviewService.getAverageRating(movieId), items.size());
    }

    @PostMapping
    public ReviewItem submitReview(@PathVariable UUID movieId,
                                     @RequestBody SubmitReviewRequest request,
                                     Authentication authentication) {
        UUID userId = UUID.fromString((String) authentication.getPrincipal());
        Review review = reviewService.submitReview(movieId, userId, request.rating(), request.comment());

        return new ReviewItem(
                review.getId(),
                review.getUser().getFullName(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}