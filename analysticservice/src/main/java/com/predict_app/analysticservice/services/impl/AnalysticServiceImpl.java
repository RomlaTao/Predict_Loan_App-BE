package com.predict_app.analysticservice.services.impl;

import com.predict_app.analysticservice.repositories.AnalysticRepository;
import com.predict_app.analysticservice.dtos.AnalysticResponseDto;
import com.predict_app.analysticservice.dtos.AnalysticStatDto;
import com.predict_app.analysticservice.entities.PredictionAnalystic;
import com.predict_app.analysticservice.enums.PredictionStatus;
import com.predict_app.analysticservice.services.AnalysticService;
import com.predict_app.analysticservice.exceptions.GlobalException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalysticServiceImpl implements AnalysticService {

    private final AnalysticRepository analysticRepository;

    @Override
    public AnalysticResponseDto getAnalysticDataById(UUID predictionId) {
        PredictionAnalystic predictionAnalystic = analysticRepository.findById(predictionId)
            .orElseThrow(() -> new GlobalException.AnalysticNotFoundException("Analystic data not found"));
        return buildAnalysticResponseDto(predictionAnalystic);
    }
    
    @Override
    public List<AnalysticResponseDto> getAnalysticsDataByCustomerId(UUID customerId) {
        List<PredictionAnalystic> predictionAnalystics = analysticRepository.findByCustomerId(customerId);
        return predictionAnalystics.stream()
            .map(this::buildAnalysticResponseDto)
            .toList();
    }
    
    @Override
    public List<AnalysticResponseDto> getAnalysticsDataByEmployeeId(UUID employeeId) {
        List<PredictionAnalystic> predictionAnalystics = analysticRepository.findByEmployeeId(employeeId);
        return predictionAnalystics.stream()
            .map(this::buildAnalysticResponseDto)
            .toList();
    }

    @Override
    public List<AnalysticResponseDto> getAnalysticsDataByCustomerIdAndEmployeeId(UUID customerId, UUID employeeId) {
        List<PredictionAnalystic> predictionAnalystics = analysticRepository.findByCustomerIdAndEmployeeId(customerId, employeeId);
        return predictionAnalystics.stream()
            .map(this::buildAnalysticResponseDto)
            .toList();
    }

    @Override
    public AnalysticStatDto getAnalysticStatDataOverview() {
        Long totalPredictions = analysticRepository.count();
        Long totalAcceptedPredictions = analysticRepository.countByResultLabel(true);
        Long totalRejectedPredictions = analysticRepository.countByResultLabel(false);

        return AnalysticStatDto.builder()
            .totalPredictions(totalPredictions)
            .totalAcceptedPredictions(totalAcceptedPredictions)
            .totalRejectedPredictions(totalRejectedPredictions)
            .build();
    }

    @Override
    public AnalysticStatDto getAnalysticStatDataByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<PredictionAnalystic> predictionAnalystics = analysticRepository.findByCompletedAtBetween(startDate, endDate);
        Long totalAcceptedPredictions = 0L;
        Long totalRejectedPredictions = 0L;
        Long totalPredictions = (long) predictionAnalystics.size();
        ;
        for (PredictionAnalystic predictionAnalystic : predictionAnalystics) {
            if (predictionAnalystic.getResultLabel()) {
                totalAcceptedPredictions++;
            } else {
                totalRejectedPredictions++;
            }
        }

        return AnalysticStatDto.builder()
            .totalPredictions(totalPredictions)
            .totalAcceptedPredictions(totalAcceptedPredictions)
            .totalRejectedPredictions(totalRejectedPredictions)
            .build();
    }

    @Override
    public List<AnalysticResponseDto> getAnalysticsDataByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<PredictionAnalystic> predictionAnalystics = analysticRepository.findByCompletedAtBetween(startDate, endDate);
        return predictionAnalystics.stream()
            .map(this::buildAnalysticResponseDto)
            .toList();
    }

    @Override
    public List<AnalysticResponseDto> getAnalysticsDataByResultLabel(Boolean resultLabel) {
        List<PredictionAnalystic> predictionAnalystics = analysticRepository.findByResultLabel(resultLabel);
        return predictionAnalystics.stream()
            .map(this::buildAnalysticResponseDto)
            .toList();
    }

    @Override
    public List<AnalysticResponseDto> getAnalysticsDataByProbabilityRange(Double minProbability, Double maxProbability) {
        List<PredictionAnalystic> predictionAnalystics = analysticRepository.findByProbabilityBetween(minProbability, maxProbability);
        return predictionAnalystics.stream()
            .map(this::buildAnalysticResponseDto)
            .toList();
    }

    @Override
    public List<AnalysticResponseDto> getAnalysticsDataByPredictionStatus(PredictionStatus predictionStatus) {
        List<PredictionAnalystic> predictionAnalystics = analysticRepository.findByPredictionStatus(predictionStatus);
        return predictionAnalystics.stream()
            .map(this::buildAnalysticResponseDto)
            .toList();
    }

    private AnalysticResponseDto buildAnalysticResponseDto(PredictionAnalystic predictionAnalystic) {
        return AnalysticResponseDto.builder()
            .predictionId(predictionAnalystic.getPredictionId())
            .customerId(predictionAnalystic.getCustomerId())
            .employeeId(predictionAnalystic.getEmployeeId())
            .status(predictionAnalystic.getStatus())
            .resultLabel(predictionAnalystic.getResultLabel())
            .probability(predictionAnalystic.getProbability())
            .createdAt(predictionAnalystic.getCreatedAt())
            .completedAt(predictionAnalystic.getCompletedAt())
            .age(predictionAnalystic.getAge())
            .experience(predictionAnalystic.getExperience())
            .income(predictionAnalystic.getIncome())
            .family(predictionAnalystic.getFamily())
            .education(predictionAnalystic.getEducation())
            .mortgage(predictionAnalystic.getMortgage())
            .securitiesAccount(predictionAnalystic.getSecuritiesAccount())
            .cdAccount(predictionAnalystic.getCdAccount())
            .online(predictionAnalystic.getOnline())
            .creditCard(predictionAnalystic.getCreditCard())
            .ccAvg(predictionAnalystic.getCcAvg())
            .build();
    }
}
