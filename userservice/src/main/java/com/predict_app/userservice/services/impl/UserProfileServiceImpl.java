package com.predict_app.userservice.services.impl;

import com.predict_app.userservice.dtos.UserProfileRequestDto;
import com.predict_app.userservice.dtos.UserProfileResponseDto;
import com.predict_app.userservice.entities.UserProfile;
import com.predict_app.userservice.exceptions.ResourceNotFoundException;
import com.predict_app.userservice.mappers.UserProfileMapper;
import com.predict_app.userservice.repositories.UserProfileRepository;
import com.predict_app.userservice.services.UserProfileService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    UserProfileServiceImpl(UserProfileRepository userProfileRepository, UserProfileMapper userProfileMapper){
        this.userProfileRepository = userProfileRepository;
        this.userProfileMapper = userProfileMapper;
    }

    @Override
    public UserProfileResponseDto createProfile(UserProfileRequestDto request) {
        System.out.println("Request userId: " + request.getUserId());

        // Sử dụng mapper để chuyển đổi từ DTO sang Entity
        UserProfile profile = userProfileMapper.toEntity(request);

        System.out.println("Profile userId before save: " + profile.getUserId());

        // onCreate() sẽ được tự động gọi bởi JPA @PrePersist
        userProfileRepository.save(profile);

        System.out.println("Profile userId after save: " + profile.getUserId());

        // Sử dụng mapper để chuyển đổi từ Entity sang Response DTO
        return userProfileMapper.toResponseDto(profile);
    }

    @Override
    public UserProfileResponseDto getProfileByUserId(UUID userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));
        return userProfileMapper.toResponseDto(profile);
    }

    @Override
    public UserProfileResponseDto updateProfile(UUID userId, UserProfileRequestDto request) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));

        // Sử dụng mapper để cập nhật Entity từ DTO
        userProfileMapper.updateEntityFromDto(profile, request);

        profile.onUpdate();
        userProfileRepository.save(profile);
        return userProfileMapper.toResponseDto(profile);
    }

}
