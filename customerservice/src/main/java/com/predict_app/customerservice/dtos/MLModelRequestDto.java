package com.predict_app.customerservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MLModelRequestDto {
    private Integer age;
    private Integer experience;
    private Double income;
    private Integer family;
    private Integer education;
    private Double mortgage;
    private Integer securitiesAccount;
    private Integer cdAccount;
    private Integer online;
    private Integer creditCard;
    private Double annCcAvg;
}
