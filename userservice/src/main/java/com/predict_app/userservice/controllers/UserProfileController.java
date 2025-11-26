package com.predict_app.userservice.controllers;

import com.predict_app.userservice.dtos.UserProfileRequestDto;
import com.predict_app.userservice.dtos.UserProfileResponseDto;
import com.predict_app.userservice.services.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users-profiles")
public class UserProfileController {

    private final UserProfileService userProfileService;

    UserProfileController(UserProfileService userProfileService){
        this.userProfileService = userProfileService;
    }

    @PostMapping
    public ResponseEntity<UserProfileResponseDto> createProfile(
            @RequestBody UserProfileRequestDto request,
            @RequestHeader("X-User-Role") String role) {
        if (role.equals("ROLE_ADMIN")) {
            return ResponseEntity.ok(userProfileService.createProfile(request));
        } else {
            throw new RuntimeException("You are not authorized to create a profile");
        }
    }

    // Route cụ thể '/me' phải đặt TRƯỚC route động '/{userId}' để tránh conflict
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDto> getCurrentProfile(@RequestHeader("X-User-Id") UUID userId){
        return ResponseEntity.ok(userProfileService.getProfileByUserId(userId));
    }

    // Route GET '/' phải đặt trước route động để tránh conflict
    @GetMapping
    public ResponseEntity<List<UserProfileResponseDto>> getAllProfiles(@RequestHeader("X-User-Role") String role) {
        if (role.equals("ROLE_ADMIN")) {
            return ResponseEntity.ok(userProfileService.getAllProfiles());
        } else {
            throw new RuntimeException("You are not authorized to get all profiles");
        }
    }

    // Route động '/{userId}' đặt sau các route cụ thể  
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponseDto> getProfile(@PathVariable UUID userId, @RequestHeader("X-User-Role") String role) {
        if (role.equals("ROLE_ADMIN")) {
            return ResponseEntity.ok(userProfileService.getProfileByUserId(userId));
        } else {
            throw new RuntimeException("You are not authorized to get this profile");
        }
    }

    @PutMapping("/{userId}")
        public ResponseEntity<UserProfileResponseDto> updateProfile(
            @PathVariable UUID userId,
            @RequestBody UserProfileRequestDto request,
            @RequestHeader("X-User-Id") UUID currentUserId) {
        return ResponseEntity.ok(userProfileService.updateProfile(userId, request, currentUserId));
    }
}
