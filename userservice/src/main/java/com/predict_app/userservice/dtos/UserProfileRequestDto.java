package com.predict_app.userservice.dtos;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileRequestDto {
    private UUID userId;
    private String name;
    private int age;
    private int experience;          // số năm kinh nghiệm
    private double income;           // thu nhập hằng năm
    private int family;              // số thành viên gia đình
    private double ccAvg;            // chi tiêu trung bình hàng tháng bằng thẻ tín dụng
    private int education;           // 1=Undergrad, 2=Graduate, 3=Doctoral
    private double mortgage;         // khoản vay thế chấp (nghìn USD)
    private boolean securitiesAccount;
    private boolean cdAccount;
    private boolean online;
    private boolean creditCard;
}