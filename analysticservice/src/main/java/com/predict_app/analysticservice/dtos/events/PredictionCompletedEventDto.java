package com.predict_app.analysticservice.dtos.events;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionCompletedEventDto {

    // Prediction level metadata
    private UUID predictionId;
    private UUID customerId;
    private UUID employeeId;
    private String status;
    private Boolean resultLabel;
    private Double probability;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    // Snapshot of key customer attributes at prediction time
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
}
