package com.predict_app.predictionservice.dtos.events;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEnrichedEventDto {
    private UUID predictionId; // để PredictionService biết dữ liệu này thuộc request nào
    private CustomerDto customer;
    private LocalDateTime enrichedAt;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerDto {
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
}
