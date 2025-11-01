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
    private String predictionResult;  // Null when status is PENDING
    private Double confidence;  // Changed to Double (nullable) - Null when status is PENDING
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;  // Null when status is PENDING
}
