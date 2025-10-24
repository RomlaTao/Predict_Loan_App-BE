package com.predict_app.userservice.dtos;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponseDto {
    private UUID userId;
    private String fullName;        
    private String email;
    private String department;
    private String position;
    private LocalDate hireDate;
    private String phoneNumber;
    private String address;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
