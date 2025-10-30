package com.predict_app.predictionservice.dtos;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

import com.predict_app.predictionservice.enums.PredictionStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionResponseDto {
    private UUID predictionId;
    private UUID customerId;
    private UUID employeeId;
    private PredictionStatus status;
    private String predictionResult;
    private double confidence;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
