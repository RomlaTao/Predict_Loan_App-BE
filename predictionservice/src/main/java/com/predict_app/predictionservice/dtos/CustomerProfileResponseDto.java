package com.predict_app.predictionservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProfileResponseDto {
    private UUID customerId;
    private String fullName;
    private String email;
    private Integer age;
    private Integer experience;
    private Double income;
    private Integer family;
    private Integer education;
    private Double mortgage;
    private Boolean securitiesAccount;
    private Boolean cdAccount;
    private Boolean online;
    private Boolean creditCard;
    private Double ccAvg;
    private Boolean personalLoan;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
