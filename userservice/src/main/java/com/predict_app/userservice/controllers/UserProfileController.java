package com.predict_app.userservice.controllers;

import com.predict_app.userservice.dtos.UserProfileRequestDto;
import com.predict_app.userservice.dtos.UserProfileResponseDto;
import com.predict_app.userservice.services.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-profiles")
public class UserProfileController {

    private final UserProfileService userProfileService;

    UserProfileController(UserProfileService userProfileService){
        this.userProfileService = userProfileService;
    }

    @PostMapping
    public ResponseEntity<UserProfileResponseDto> createProfile(@RequestBody UserProfileRequestDto request) {
        return ResponseEntity.ok(userProfileService.createProfile(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDto> getCurrentProfile(
            @RequestHeader("X-User-Id") UUID userId
    ) {
        return ResponseEntity.ok(userProfileService.getProfileByUserId(userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponseDto> getProfile(@PathVariable UUID userId) {
        return ResponseEntity.ok(userProfileService.getProfileByUserId(userId));
    }

    @GetMapping
    public ResponseEntity<List<UserProfileResponseDto>> getAllProfiles() {
        return ResponseEntity.ok(userProfileService.getAllProfiles());
    }

    @PutMapping("/{userId}")
        public ResponseEntity<UserProfileResponseDto> updateProfile(
            @PathVariable UUID userId,
            @RequestBody UserProfileRequestDto request) {
        return ResponseEntity.ok(userProfileService.updateProfile(userId, request));
    }
}
