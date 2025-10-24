package com.predict_app.userservice.dtos;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileRequestDto {
    private UUID userId;
    private String fullName;
    private String email;
    private String department;
    private String position;
    private LocalDate hireDate;
    private String phoneNumber;
    private String address;
    private Boolean isActive;
}