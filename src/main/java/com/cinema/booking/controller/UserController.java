package com.cinema.booking.controller;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.booking.entity.User;
import com.cinema.booking.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public record ProfileResponse(UUID id, String email, String fullName, String phone, String role) {
        static ProfileResponse fromEntity(User user) {
            return new ProfileResponse(user.getId(), user.getEmail(), user.getFullName(), user.getPhone(), user.getRole());
        }
    }

    public record UpdateProfileRequest(String fullName, String phone) {
    }

    public record ChangePasswordRequest(String currentPassword, String newPassword) {
    }

    @GetMapping("/me")
    public ProfileResponse getMyProfile(Authentication authentication) {
        UUID userId = currentUserId(authentication);
        return ProfileResponse.fromEntity(userService.getById(userId));
    }

    @PutMapping("/me")
    public ProfileResponse updateProfile(@RequestBody UpdateProfileRequest request, Authentication authentication) {
        UUID userId = currentUserId(authentication);
        User updated = userService.updateProfile(userId, request.fullName(), request.phone());
        return ProfileResponse.fromEntity(updated);
    }

    @PutMapping("/me/password")
    public void changePassword(@RequestBody ChangePasswordRequest request, Authentication authentication) {
        UUID userId = currentUserId(authentication);
        userService.changePassword(userId, request.currentPassword(), request.newPassword());
    }

    private UUID currentUserId(Authentication authentication) {
        return UUID.fromString((String) authentication.getPrincipal());
    }
}