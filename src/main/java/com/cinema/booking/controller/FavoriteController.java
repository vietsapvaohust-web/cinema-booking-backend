package com.cinema.booking.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.booking.entity.Movie;
import com.cinema.booking.service.FavoriteService;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    public record ToggleResponse(boolean favorited) {
    }

    @GetMapping
    public List<Movie> getMyFavorites(Authentication authentication) {
        return favoriteService.getFavoriteMovies(currentUserId(authentication));
    }

    @GetMapping("/{movieId}/status")
    public ToggleResponse getStatus(@PathVariable UUID movieId, Authentication authentication) {
        boolean isFav = favoriteService.isFavorite(currentUserId(authentication), movieId);
        return new ToggleResponse(isFav);
    }

    @PostMapping("/{movieId}/toggle")
    public ToggleResponse toggle(@PathVariable UUID movieId, Authentication authentication) {
        boolean nowFavorited = favoriteService.toggleFavorite(currentUserId(authentication), movieId);
        return new ToggleResponse(nowFavorited);
    }

    private UUID currentUserId(Authentication authentication) {
        return UUID.fromString((String) authentication.getPrincipal());
    }
}