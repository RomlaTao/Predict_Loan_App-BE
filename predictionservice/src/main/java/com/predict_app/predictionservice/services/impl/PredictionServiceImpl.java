package com.predict_app.predictionservice.services.impl;

import com.predict_app.predictionservice.dtos.PredictionRequestDto;
import com.predict_app.predictionservice.dtos.PredictionResponseDto;
import com.predict_app.predictionservice.enums.PredictionStatus;
import com.predict_app.predictionservice.repositories.PredictionRepository;
import com.predict_app.predictionservice.services.PredictionService;
import com.predict_app.predictionservice.entities.Prediction;
import com.predict_app.predictionservice.dtos.events.PredictionRequestedEventDto;
import com.predict_app.predictionservice.publishers.PredictionEventPublisher;
import com.predict_app.predictionservice.dtos.events.PredictionCompletedAnalysticEventDto;
import com.predict_app.predictionservice.dtos.events.CustomerEnrichedEventDto;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    @Autowired
    private final ObjectMapper objectMapper;
    
    public PredictionServiceImpl(
            PredictionRepository predictionRepository,
            PredictionEventPublisher predictionEventPublisher,
            ObjectMapper objectMapper) {
        this.predictionRepository = predictionRepository;
        this.predictionEventPublisher = predictionEventPublisher;
        this.objectMapper = objectMapper;
    }

    @Override
    public PredictionResponseDto createPrediction(PredictionRequestDto request, UUID staffId) {
        // Validation is handled by @Valid annotation in controller
        // Only business logic validation needed here

        // Generate prediction ID
        UUID predictionId = UUID.randomUUID();

        Prediction prediction = Prediction.builder()
            .predictionId(predictionId)
            .customerId(request.getCustomerId())
            .employeeId(staffId)
            .status(PredictionStatus.PENDING)
            .build();
        
        prediction.onCreate();
        predictionRepository.save(prediction);

        // Publish prediction requested event
        PredictionRequestedEventDto predictionRequestedEventDto = PredictionRequestedEventDto.builder()
            .predictionId(predictionId)
            .customerId(request.getCustomerId())
            .employeeId(staffId)
            .requestedAt(LocalDateTime.now())
            .build();

        predictionEventPublisher.publishCustomerProfileRequestedEvent(predictionRequestedEventDto);

        return mapToResponseDto(prediction);
    }

    @Override
    public PredictionResponseDto getPredictionById(UUID predictionId, UUID staffId, String role) {
        Prediction prediction = predictionRepository.findById(predictionId)
            .orElseThrow(() -> new RuntimeException("Prediction not found with id: " + predictionId));
        if (role.equals("ROLE_STAFF")) {
            if (!prediction.getEmployeeId().equals(staffId)) {
                throw new RuntimeException("You are not authorized to get this prediction");
            }
        }
        return mapToResponseDto(prediction);
    }

    @Override
    public List<PredictionResponseDto> getPredictionsByCustomerId(UUID customerId) {
        List<Prediction> predictions = predictionRepository.findByCustomerId(customerId);
        return predictions.stream()
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
    public void setPredictionResult(UUID predictionId, Boolean resultLabel, Double probability) {
        Prediction prediction = predictionRepository.findById(predictionId)
            .orElseThrow(() -> new RuntimeException("Prediction not found with id: " + predictionId));
        prediction.setPredictionResult(resultLabel);
        prediction.setConfidence(probability);
        prediction.setStatus(PredictionStatus.COMPLETED);
        prediction.setCompletedAt(LocalDateTime.now());
        predictionRepository.save(prediction);
    }

    @Override
    public PredictionCompletedAnalysticEventDto buildAnalyticsEvent(UUID predictionId) {
        Prediction prediction = predictionRepository.findById(predictionId)
            .orElseThrow(() -> new RuntimeException("Prediction not found with id: " + predictionId));

        // Parse JSON inputData (snapshot của CustomerDto)
        CustomerEnrichedEventDto.CustomerDto snapshot = null;
        if (prediction.getInputData() != null) {
            try {
                snapshot = objectMapper.readValue(
                    prediction.getInputData(),
                    CustomerEnrichedEventDto.CustomerDto.class
                );
            } catch (Exception e) {
                throw new RuntimeException("Error parsing input data: " + e.getMessage(), e);
            }
        }

        PredictionCompletedAnalysticEventDto.PredictionCompletedAnalysticEventDtoBuilder builder =
            PredictionCompletedAnalysticEventDto.builder()
                .predictionId(prediction.getPredictionId())
                .customerId(prediction.getCustomerId())
                .employeeId(prediction.getEmployeeId())
                .status(prediction.getStatus() != null ? PredictionStatus.valueOf(prediction.getStatus().name()) : null)
                .resultLabel(prediction.getPredictionResult())
                .probability(prediction.getConfidence())
                .createdAt(prediction.getCreatedAt())
                .completedAt(prediction.getCompletedAt());
        if (snapshot != null) {
            builder
                .age(snapshot.getAge())
                .experience(snapshot.getExperience())
                .income(snapshot.getIncome())
                .family(snapshot.getFamily())
                .education(snapshot.getEducation())
                .mortgage(snapshot.getMortgage())
                .securitiesAccount(snapshot.getSecuritiesAccount())
                .cdAccount(snapshot.getCdAccount())
                .online(snapshot.getOnline())
                .creditCard(snapshot.getCreditCard())
                .ccAvg(snapshot.getCcAvg());
        }

        return builder.build();
    }

    /**
     * Maps Prediction entity to PredictionResponseDto
     * Handles null values safely for fields that may be null when status is PENDING
     * 
     * @param prediction The prediction entity to map
     * @return PredictionResponseDto with null-safe values
     */
    private PredictionResponseDto mapToResponseDto(Prediction prediction) {
        return PredictionResponseDto.builder()
            .predictionId(prediction.getPredictionId())
            .customerId(prediction.getCustomerId())
            .employeeId(prediction.getEmployeeId())
            .status(prediction.getStatus())
            // Null-safe: predictionResult is null when status is PENDING
            .predictionResult(prediction.getPredictionResult())
            // Null-safe: confidence is null when status is PENDING
            .confidence(prediction.getConfidence())
            .createdAt(prediction.getCreatedAt())
            // Null-safe: completedAt is null when status is PENDING
            .completedAt(prediction.getCompletedAt())
            .build();
    }
}   
