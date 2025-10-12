package com.predict_app.userservice.mappers;

import com.predict_app.userservice.dtos.UserProfileRequestDto;
import com.predict_app.userservice.dtos.UserProfileResponseDto;
import com.predict_app.userservice.entities.UserProfile;

/**
 * Mapper interface để chuyển đổi giữa UserProfile Entity và DTOs
 */
public interface UserProfileMapper {

    /**
     * Chuyển đổi từ UserProfile Entity sang UserProfileResponseDto
     * @param userProfile Entity cần chuyển đổi
     * @return UserProfileResponseDto
     */
    UserProfileResponseDto toResponseDto(UserProfile userProfile);

    /**
     * Chuyển đổi từ UserProfileRequestDto sang UserProfile Entity
     * @param requestDto DTO cần chuyển đổi
     * @return UserProfile Entity
     */
    UserProfile toEntity(UserProfileRequestDto requestDto);

    /**
     * Cập nhật UserProfile Entity từ UserProfileRequestDto
     * @param userProfile Entity cần cập nhật
     * @param requestDto DTO chứa dữ liệu mới
     */
    void updateEntityFromDto(UserProfile userProfile, UserProfileRequestDto requestDto);
}
