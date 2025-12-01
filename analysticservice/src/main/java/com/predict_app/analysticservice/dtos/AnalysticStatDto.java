package com.predict_app.analysticservice.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysticStatDto {
    private Long totalPredictions;
    private Long totalAcceptedPredictions;
    private Long totalRejectedPredictions;
}
