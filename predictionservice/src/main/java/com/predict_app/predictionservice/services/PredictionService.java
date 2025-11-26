package com.predict_app.predictionservice.services;

import com.predict_app.predictionservice.dtos.PredictionRequestDto;
import com.predict_app.predictionservice.dtos.PredictionResponseDto;

import java.util.List;
import java.util.UUID;

public interface PredictionService {
    PredictionResponseDto createPrediction(PredictionRequestDto request, UUID staffId);
    PredictionResponseDto getPredictionById(UUID predictionId, UUID staffId, String role);
    List<PredictionResponseDto> getPredictionsByCustomerId(UUID customerId);
    List<PredictionResponseDto> getPredictionsByEmployeeId(UUID employeeId);
    List<PredictionResponseDto> getAllPredictions();
    PredictionResponseDto updatePredictionStatus(UUID predictionId, String status);
    void setInputData(UUID predictionId, String inputData);
    void setPredictionResult(UUID predictionId, Boolean resultLabel, Double probability);
}
