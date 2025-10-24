package com.predict_app.userservice.services;

import com.predict_app.userservice.dtos.UserProfileRequestDto;
import com.predict_app.userservice.dtos.UserProfileResponseDto;

import java.util.List;
import java.util.UUID;

public interface UserProfileService {
    UserProfileResponseDto createProfile(UserProfileRequestDto request);
    UserProfileResponseDto getProfileByUserId(UUID userId);
    List<UserProfileResponseDto> getAllProfiles();
    UserProfileResponseDto updateProfile(UUID userId, UserProfileRequestDto request);
}
