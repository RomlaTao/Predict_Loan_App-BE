package com.predict_app.analysticservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.predict_app.analysticservice.enums.PredictionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Aggregated analytics payload returned to the dashboard so the UI can render
 * totals, charts and attribute distributions without additional processing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysticResponseDto {
    private UUID predictionId;
    private UUID customerId;
    private UUID employeeId; 
    private PredictionStatus predictionStatus; 
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
