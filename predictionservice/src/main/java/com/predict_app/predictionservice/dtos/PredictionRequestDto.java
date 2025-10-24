package com.predict_app.predictionservice.dtos;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionRequestDto {
    private UUID customerId;
    private UUID employeeId;
    private Integer age;
    private Integer experience;
    private Double income;
    private Integer family;
    private Double ccAvg;
    private Integer education;
    private Double mortgage;
    private Boolean securitiesAccount;
    private Boolean cdAccount;
    private Boolean online;
    private Boolean creditCard;
}
