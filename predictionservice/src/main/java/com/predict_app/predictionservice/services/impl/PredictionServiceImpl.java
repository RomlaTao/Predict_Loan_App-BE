package com.predict_app.predictionservice.services.impl;

import com.predict_app.predictionservice.dtos.PredictionRequestDto;
import com.predict_app.predictionservice.dtos.PredictionResponseDto;
import com.predict_app.predictionservice.dtos.MLModelRequestDto;
import com.predict_app.predictionservice.dtos.MLModelResponseDto;
import com.predict_app.predictionservice.enums.PredictionStatus;
import com.predict_app.predictionservice.repositories.PredictionRepository;
import com.predict_app.predictionservice.services.PredictionService;
import com.predict_app.predictionservice.services.CustomerDataService;
import com.predict_app.predictionservice.services.MLModelService;
import com.predict_app.predictionservice.entities.Prediction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PredictionServiceImpl implements PredictionService {

    private final PredictionRepository predictionRepository;
    private final CustomerDataService customerDataService;
    private final MLModelService mlModelService;

    @Override
    public PredictionResponseDto createPrediction(PredictionRequestDto request) {
        try {
            log.info("Creating prediction for customerId: {}", request.getCustomerId());
            
            // 1. Lấy customer data từ CustomerService
            MLModelRequestDto customerMLData = customerDataService.getCustomerMLData(request.getCustomerId());
            
            // 2. Gửi đến ML model qua RabbitMQ
            MLModelResponseDto mlResponse = mlModelService.predict(customerMLData);
            
            // 3. Tạo prediction record
            Prediction prediction = Prediction.builder()
                .predictionId(UUID.randomUUID())
                .customerId(request.getCustomerId())
                .employeeId(request.getEmployeeId())
                .status(PredictionStatus.COMPLETED)
                .inputData(customerMLData.toString())
                .predictionResult(mlResponse.toString())
                .confidence(mlResponse.getConfidence())
                .errorMessage(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .build();
            
            // 4. Lưu vào database
            Prediction savedPrediction = predictionRepository.save(prediction);
            
            log.info("Prediction created successfully with id: {}", savedPrediction.getPredictionId());
            return mapToResponseDto(savedPrediction);
            
        } catch (Exception e) {
            log.error("Error creating prediction for customerId: {}", request.getCustomerId(), e);
            
            // Tạo prediction record với status FAILED
            Prediction failedPrediction = Prediction.builder()
                .predictionId(UUID.randomUUID())
                .customerId(request.getCustomerId())
                .employeeId(request.getEmployeeId())
                .status(PredictionStatus.FAILED)
                .inputData(request.toString())
                .predictionResult(null)
                .confidence(0.0)
                .errorMessage(e.getMessage())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .build();
            
            Prediction savedPrediction = predictionRepository.save(failedPrediction);
            return mapToResponseDto(savedPrediction);
        }
    }

    @Override
    public PredictionResponseDto getPredictionById(UUID predictionId) {
        return predictionRepository.findById(predictionId)
            .map(this::mapToResponseDto)
            .orElseThrow(() -> new RuntimeException("Prediction not found with id: " + predictionId));
    }
    @Override
    public List<PredictionResponseDto> getPredictionsByCustomerId(UUID customerId) {
        return null;
    }
    @Override
    public List<PredictionResponseDto> getPredictionsByEmployeeId(UUID employeeId) {
        return null;
    }
    @Override
    public List<PredictionResponseDto> getAllPredictions() {
        return null;
    }
    @Override
    public PredictionResponseDto updatePredictionStatus(UUID predictionId, String status) {
        return null;
    }

    private PredictionResponseDto mapToResponseDto(Prediction prediction) {
        return PredictionResponseDto.builder()
            .predictionId(prediction.getPredictionId())
            .customerId(prediction.getCustomerId())
            .employeeId(prediction.getEmployeeId())
            .status(prediction.getStatus())
            .inputData(prediction.getInputData())
            .predictionResult(prediction.getPredictionResult())
            .confidence(prediction.getConfidence())
            .errorMessage(prediction.getErrorMessage())
            .createdAt(prediction.getCreatedAt())
            .updatedAt(prediction.getUpdatedAt())
            .completedAt(prediction.getCompletedAt())
            .build();
    }
}   
