package com.predict_app.userservice.mappers.impl;

import com.predict_app.userservice.dtos.UserProfileRequestDto;
import com.predict_app.userservice.dtos.UserProfileResponseDto;
import com.predict_app.userservice.entities.UserProfile;
import com.predict_app.userservice.mappers.UserProfileMapper;
import org.springframework.stereotype.Component;

/**
 * Implementation của UserProfileMapper
 * Xử lý chuyển đổi giữa UserProfile Entity và DTOs
 */
@Component
public class UserProfileMapperImpl implements UserProfileMapper {

    @Override
    public UserProfileResponseDto toResponseDto(UserProfile userProfile) {
        if (userProfile == null) {
            return null;
        }

        return UserProfileResponseDto.builder()
                .userId(userProfile.getUserId())
                .name(userProfile.getName())
                .age(userProfile.getAge())
                .experience(userProfile.getExperience())
                .income(userProfile.getIncome())
                .family(userProfile.getFamily())
                .ccAvg(userProfile.getCcAvg())
                .education(userProfile.getEducation())
                .mortgage(userProfile.getMortgage())
                .securitiesAccount(userProfile.getSecuritiesAccount())
                .cdAccount(userProfile.getCdAccount())
                .online(userProfile.getOnline())
                .creditCard(userProfile.getCreditCard())
                .personalLoan(false) // Mặc định false, sẽ được cập nhật bởi ML model
                .createdAt(userProfile.getCreatedAt())
                .updatedAt(userProfile.getUpdatedAt())
                .build();
    }

    @Override
    public UserProfile toEntity(UserProfileRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }

        return UserProfile.builder()
                .userId(requestDto.getUserId())
                .name(requestDto.getName())
                .age(requestDto.getAge())
                .experience(requestDto.getExperience())
                .income(requestDto.getIncome())
                .family(requestDto.getFamily())
                .ccAvg(requestDto.getCcAvg())
                .education(requestDto.getEducation())
                .mortgage(requestDto.getMortgage())
                .securitiesAccount(requestDto.isSecuritiesAccount())
                .cdAccount(requestDto.isCdAccount())
                .online(requestDto.isOnline())
                .creditCard(requestDto.isCreditCard())
                .build();
    }

    @Override
    public void updateEntityFromDto(UserProfile userProfile, UserProfileRequestDto requestDto) {
        if (userProfile == null || requestDto == null) {
            return;
        }

        // Cập nhật các trường từ DTO vào Entity
        userProfile.setName(requestDto.getName());
        userProfile.setAge(requestDto.getAge());
        userProfile.setExperience(requestDto.getExperience());
        userProfile.setIncome(requestDto.getIncome());
        userProfile.setFamily(requestDto.getFamily());
        userProfile.setCcAvg(requestDto.getCcAvg());
        userProfile.setEducation(requestDto.getEducation());
        userProfile.setMortgage(requestDto.getMortgage());
        userProfile.setSecuritiesAccount(requestDto.isSecuritiesAccount());
        userProfile.setCdAccount(requestDto.isCdAccount());
        userProfile.setOnline(requestDto.isOnline());
        userProfile.setCreditCard(requestDto.isCreditCard());
        
        // updatedAt sẽ được tự động cập nhật bởi @PreUpdate
    }
}
