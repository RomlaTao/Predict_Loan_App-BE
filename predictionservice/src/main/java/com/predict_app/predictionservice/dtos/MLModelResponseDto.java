package com.predict_app.predictionservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MLModelResponseDto {
    private Boolean prediction;
    private Double confidence;
    private Map<String, Double> probabilities;
    private String message;
    private LocalDateTime timestamp;
}