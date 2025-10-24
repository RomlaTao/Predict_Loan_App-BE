package com.predict_app.customerservice.dtos;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerProfileRequestDto {
    private UUID customerId;
    private String fullName;
    private String email;
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
    private Boolean personalLoan;
}
