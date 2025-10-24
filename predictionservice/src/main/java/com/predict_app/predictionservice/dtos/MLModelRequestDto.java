package com.predict_app.predictionservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MLModelRequestDto {
    
    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 100, message = "Age must be at most 100")
    private Integer age;
    
    @NotNull(message = "Experience is required")
    @Min(value = 0, message = "Experience must be at least 0")
    @Max(value = 50, message = "Experience must be at most 50")
    private Integer experience;
    
    @NotNull(message = "Income is required")
    @Positive(message = "Income must be positive")
    private Double income;
    
    @NotNull(message = "Family is required")
    @Min(value = 1, message = "Family must be at least 1")
    @Max(value = 10, message = "Family must be at most 10")
    private Integer family;
    
    @NotNull(message = "Education is required")
    @Min(value = 1, message = "Education must be 1, 2, or 3")
    @Max(value = 3, message = "Education must be 1, 2, or 3")
    private Integer education;
    
    @NotNull(message = "Mortgage is required")
    @Min(value = 0, message = "Mortgage must be non-negative")
    private Double mortgage;
    
    @NotNull(message = "SecuritiesAccount is required")
    @Min(value = 0, message = "SecuritiesAccount must be 0 or 1")
    @Max(value = 1, message = "SecuritiesAccount must be 0 or 1")
    private Integer securitiesAccount;
    
    @NotNull(message = "CdAccount is required")
    @Min(value = 0, message = "CdAccount must be 0 or 1")
    @Max(value = 1, message = "CdAccount must be 0 or 1")
    private Integer cdAccount;
    
    @NotNull(message = "Online is required")
    @Min(value = 0, message = "Online must be 0 or 1")
    @Max(value = 1, message = "Online must be 0 or 1")
    private Integer online;
    
    @NotNull(message = "CreditCard is required")
    @Min(value = 0, message = "CreditCard must be 0 or 1")
    @Max(value = 1, message = "CreditCard must be 0 or 1")
    private Integer creditCard;
    
    @NotNull(message = "annCcAvg is required")
    @Min(value = 0, message = "annCcAvg must be non-negative")
    private Double annCcAvg;
}