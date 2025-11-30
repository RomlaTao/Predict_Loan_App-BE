package com.predict_app.userservice.services;

import com.predict_app.userservice.dtos.UserProfileRequestDto;
import com.predict_app.userservice.dtos.UserProfileResponseDto;
import com.predict_app.userservice.dtos.events.UserCreatedEventDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UserProfileService {
    UserProfileResponseDto createProfile(UserProfileRequestDto request);
    UserProfileResponseDto createProfileDefault(UserCreatedEventDto userCreatedEvent);
    UserProfileResponseDto getProfileByUserId(UUID userId);
    List<UserProfileResponseDto> getAllProfiles();
    UserProfileResponseDto updateProfile(UUID userId, UserProfileRequestDto request, UUID currentUserId, String role);
    UserProfileResponseDto updateProfilePartially(UUID userId, Map<String, Object> updates, UUID currentUserId, String role);
}
