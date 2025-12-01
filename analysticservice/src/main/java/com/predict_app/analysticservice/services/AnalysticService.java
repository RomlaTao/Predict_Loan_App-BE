package com.predict_app.analysticservice.services;

import com.predict_app.analysticservice.dtos.AnalysticResponseDto;
import com.predict_app.analysticservice.dtos.AnalysticStatDto;
import com.predict_app.analysticservice.enums.PredictionStatus;

import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

public interface AnalysticService {
    AnalysticResponseDto getAnalysticDataById(UUID predictionId);
    List<AnalysticResponseDto> getAnalysticsDataByCustomerId(UUID customerId);
    List<AnalysticResponseDto> getAnalysticsDataByEmployeeId(UUID employeeId);
    List<AnalysticResponseDto> getAnalysticsDataByCustomerIdAndEmployeeId(UUID customerId, UUID employeeId);
    List<AnalysticResponseDto> getAnalysticsDataByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<AnalysticResponseDto> getAnalysticsDataByResultLabel(Boolean resultLabel);
    List<AnalysticResponseDto> getAnalysticsDataByProbabilityRange(Double minProbability, Double maxProbability);
    List<AnalysticResponseDto> getAnalysticsDataByPredictionStatus(PredictionStatus predictionStatus);
    AnalysticStatDto getAnalysticStatDataOverview();
    AnalysticStatDto getAnalysticStatDataByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
