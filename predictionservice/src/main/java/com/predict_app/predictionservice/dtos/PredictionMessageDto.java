package com.predict_app.predictionservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionMessageDto {
    private String correlationId;
    private String requestId;
    private MLModelRequestDto request;
    private MLModelResponseDto response;
    private LocalDateTime timestamp;
    private String status; // PENDING, PROCESSING, COMPLETED, FAILED
}
