package com.predict_app.customerservice.dtos.events;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionCompletedEventDto {
    private UUID predictionId;
    private UUID customerId;
    private Boolean resultLabel;
    private Double probability;
    private LocalDateTime completedAt;
}
