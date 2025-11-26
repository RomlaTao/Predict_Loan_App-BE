package com.predict_app.predictionservice.dtos;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionRequestDto {
    private UUID customerId;
    private UUID employeeId;
}
