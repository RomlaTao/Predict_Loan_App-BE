package com.predict_app.userservice.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponseDto {
    private UUID userId;
    private String name;
    private int age;
    private int experience;
    private double income;
    private int family;
    private double ccAvg;
    private int education;
    private double mortgage;
    private boolean securitiesAccount;
    private boolean cdAccount;
    private boolean online;
    private boolean creditCard;
    private boolean personalLoan; // mục tiêu dự đoán: có chấp nhận khoản vay cá nhân hay không
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
