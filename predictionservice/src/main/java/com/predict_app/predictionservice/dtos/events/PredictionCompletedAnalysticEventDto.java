package com.predict_app.predictionservice.dtos.events;

import com.predict_app.predictionservice.enums.PredictionStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionCompletedAnalysticEventDto {

    // Prediction level metadata
    private UUID predictionId;
    private UUID customerId;
    private UUID employeeId;
    private PredictionStatus status;
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