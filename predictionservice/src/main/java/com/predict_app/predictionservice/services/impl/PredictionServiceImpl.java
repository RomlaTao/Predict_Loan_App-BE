package com.predict_app.predictionservice.services.impl;

import com.predict_app.predictionservice.dtos.PredictionRequestDto;
import com.predict_app.predictionservice.dtos.PredictionResponseDto;
import com.predict_app.predictionservice.enums.PredictionStatus;
import com.predict_app.predictionservice.repositories.PredictionRepository;
import com.predict_app.predictionservice.services.PredictionService;
import com.predict_app.predictionservice.entities.Prediction;
import com.predict_app.predictionservice.dtos.events.PredictionRequestedEventDto;
import com.predict_app.predictionservice.publishers.PredictionEventPublisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PredictionServiceImpl implements PredictionService {

    @Autowired
    private final PredictionRepository predictionRepository;

    @Autowired
    private final PredictionEventPublisher predictionEventPublisher;
    
    public PredictionServiceImpl(PredictionRepository predictionRepository, PredictionEventPublisher predictionEventPublisher) {
        this.predictionRepository = predictionRepository;
        this.predictionEventPublisher = predictionEventPublisher;
    }

    @Override
    public PredictionResponseDto createPrediction(PredictionRequestDto request) {

        // Validate request
        if (request.getCustomerId() == null) {
            throw new RuntimeException("Customer ID is required");
        }
        if (request.getEmployeeId() == null) {
            throw new RuntimeException("Employee ID is required");
        }

        // Generate prediction ID
        UUID predictionId = UUID.randomUUID();

        Prediction prediction = Prediction.builder()
            .predictionId(predictionId)
            .customerId(request.getCustomerId())
            .employeeId(request.getEmployeeId())
            .status(PredictionStatus.PENDING)
            .build();
        
        prediction.onCreate();
        predictionRepository.save(prediction);

        // Publish prediction requested event
        PredictionRequestedEventDto predictionRequestedEventDto = PredictionRequestedEventDto.builder()
            .predictionId(predictionId)
            .customerId(request.getCustomerId())
            .employeeId(request.getEmployeeId())
            .requestedAt(LocalDateTime.now())
            .build();

        predictionEventPublisher.publishCustomerProfileRequestedEvent(predictionRequestedEventDto);

        return mapToResponseDto(prediction);
    }

    @Override
    public PredictionResponseDto getPredictionById(UUID predictionId) {
        return predictionRepository.findById(predictionId)
            .map(this::mapToResponseDto)
            .orElseThrow(() -> new RuntimeException("Prediction not found with id: " + predictionId));
    }

    @Override
    public List<PredictionResponseDto> getPredictionsByCustomerId(UUID customerId) {
        return predictionRepository.findByCustomerId(customerId)
            .stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<PredictionResponseDto> getPredictionsByEmployeeId(UUID employeeId) {
        return predictionRepository.findByEmployeeId(employeeId)
            .stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<PredictionResponseDto> getAllPredictions() {
        return predictionRepository.findAll()
            .stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public PredictionResponseDto updatePredictionStatus(UUID predictionId, String status) {
        Prediction prediction = predictionRepository.findById(predictionId)
            .orElseThrow(() -> new RuntimeException("Prediction not found with id: " + predictionId));
        prediction.setStatus(PredictionStatus.valueOf(status));
        predictionRepository.save(prediction);
        return mapToResponseDto(prediction);
    }

    @Transactional
    @Override
    public void setInputData(UUID predictionId, String inputData) {
        Prediction prediction = predictionRepository.findById(predictionId)
            .orElseThrow(() -> new RuntimeException("Prediction not found with id: " + predictionId));
        prediction.setInputData(inputData);
        predictionRepository.save(prediction);
    }

    @Transactional
    @Override
    public void setPredictionResult(UUID predictionId, String resultLabel, Double probability) {
        Prediction prediction = predictionRepository.findById(predictionId)
            .orElseThrow(() -> new RuntimeException("Prediction not found with id: " + predictionId));
        prediction.setPredictionResult(resultLabel);
        prediction.setConfidence(probability);
        prediction.setStatus(PredictionStatus.COMPLETED);
        prediction.setCompletedAt(LocalDateTime.now());
        predictionRepository.save(prediction);
    }

    private PredictionResponseDto mapToResponseDto(Prediction prediction) {
        return PredictionResponseDto.builder()
            .predictionId(prediction.getPredictionId())
            .customerId(prediction.getCustomerId())
            .employeeId(prediction.getEmployeeId())
            .status(prediction.getStatus())
            .predictionResult(prediction.getPredictionResult())
            .confidence(prediction.getConfidence())
            .createdAt(prediction.getCreatedAt())
            .completedAt(prediction.getCompletedAt())
            .build();
    }
}   
