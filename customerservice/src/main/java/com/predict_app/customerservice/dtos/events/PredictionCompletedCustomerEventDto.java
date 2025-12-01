package com.predict_app.customerservice.dtos.events;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionCompletedCustomerEventDto {
    private UUID predictionId;
    private UUID customerId;
    private Boolean resultLabel;
}
