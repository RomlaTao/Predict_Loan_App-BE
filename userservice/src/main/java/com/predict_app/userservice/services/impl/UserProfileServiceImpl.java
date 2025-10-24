package com.predict_app.userservice.services.impl;

import com.predict_app.userservice.dtos.UserProfileRequestDto;
import com.predict_app.userservice.dtos.UserProfileResponseDto;
import com.predict_app.userservice.entities.UserProfile;
import com.predict_app.userservice.exceptions.ResourceNotFoundException;
import com.predict_app.userservice.repositories.UserProfileRepository;
import com.predict_app.userservice.services.UserProfileService;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    UserProfileServiceImpl(UserProfileRepository userProfileRepository){
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public UserProfileResponseDto createProfile(UserProfileRequestDto request) {
        System.out.println("Request userId: " + request.getUserId());

        // Manual mapping từ DTO sang Entity
        UserProfile profile = mapToEntity(request);

        System.out.println("Profile userId before save: " + profile.getUserId());

        // onCreate() sẽ được tự động gọi bởi JPA @PrePersist
        userProfileRepository.save(profile);

        System.out.println("Profile userId after save: " + profile.getUserId());

        // Sử dụng mapper để chuyển đổi từ Entity sang Response DTO
        return mapToResponseDto(profile);
    }

    @Override
    public UserProfileResponseDto getProfileByUserId(UUID userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));
        return mapToResponseDto(profile);
    }

    @Override
    public UserProfileResponseDto updateProfile(UUID userId, UserProfileRequestDto request) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));

        // Manual update Entity từ DTO
        updateEntityFromDto(profile, request);

        profile.onUpdate();
        userProfileRepository.save(profile);
        return mapToResponseDto(profile);
    }

    @Override
    public List<UserProfileResponseDto> getAllProfiles() {
        List<UserProfile> profiles = userProfileRepository.findAll();
        return profiles.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

    }


    private UserProfile mapToEntity(UserProfileRequestDto request) {
        return UserProfile.builder()
                .userId(request.getUserId())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .department(request.getDepartment())
                .position(request.getPosition())
                .hireDate(request.getHireDate())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .isActive(request.getIsActive())
                .build();
    }

    private void updateEntityFromDto(UserProfile profile, UserProfileRequestDto request) {
        profile.setFullName(request.getFullName());
        profile.setEmail(request.getEmail());
        profile.setDepartment(request.getDepartment());
        profile.setPosition(request.getPosition());
        profile.setHireDate(request.getHireDate());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setAddress(request.getAddress());
        profile.setIsActive(request.getIsActive());
    }

    private UserProfileResponseDto mapToResponseDto(UserProfile profile) {
        return UserProfileResponseDto.builder()
                .userId(profile.getUserId())
                .fullName(profile.getFullName())
                .email(profile.getEmail())
                .department(profile.getDepartment())
                .position(profile.getPosition())
                .hireDate(profile.getHireDate())
                .phoneNumber(profile.getPhoneNumber())
                .address(profile.getAddress())
                .isActive(profile.getIsActive())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
