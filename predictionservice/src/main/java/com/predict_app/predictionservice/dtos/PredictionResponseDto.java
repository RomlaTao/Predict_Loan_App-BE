package com.predict_app.predictionservice.dtos;

import com.predict_app.predictionservice.enums.PredictionStatus;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

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
    private String inputData;
    private String predictionResult;
    private Double confidence;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
}
