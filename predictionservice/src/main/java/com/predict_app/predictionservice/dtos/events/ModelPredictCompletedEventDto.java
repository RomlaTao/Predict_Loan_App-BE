package com.predict_app.predictionservice.dtos.events;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelPredictCompletedEventDto {
    private UUID predictionId;
    private UUID customerId;

    private PredictionResultDto result;
    private LocalDateTime predictedAt;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PredictionResultDto {
        private String label;          // ví dụ: "approve" hoặc "reject"
        private Double probability;    // xác suất mô hình
        private String modelVersion;   // phiên bản mô hình đang dùng
        private Long inferenceTimeMs;  // thời gian tính toán
    }
}
