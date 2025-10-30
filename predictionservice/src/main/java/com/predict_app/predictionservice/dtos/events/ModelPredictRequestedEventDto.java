package com.predict_app.predictionservice.dtos.events;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelPredictRequestedEventDto {
    private UUID predictionId;
    private UUID customerId;
    private ModelInputDto input;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelInputDto {
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
}
