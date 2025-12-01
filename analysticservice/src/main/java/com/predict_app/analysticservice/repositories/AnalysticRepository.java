package com.predict_app.analysticservice.repositories;

import com.predict_app.analysticservice.entities.PredictionAnalystic;
import com.predict_app.analysticservice.enums.PredictionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AnalysticRepository extends JpaRepository<PredictionAnalystic, UUID> {
    Optional<PredictionAnalystic> findById(UUID predictionId);
    List<PredictionAnalystic> findByCustomerId(UUID customerId);
    List<PredictionAnalystic> findByEmployeeId(UUID employeeId);
    List<PredictionAnalystic> findByCustomerIdAndEmployeeId(UUID customerId, UUID employeeId);
    List<PredictionAnalystic> findByCompletedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<PredictionAnalystic> findByResultLabel(Boolean resultLabel);
    List<PredictionAnalystic> findByProbabilityBetween(Double minProbability, Double maxProbability);
    List<PredictionAnalystic> findByPredictionStatus(PredictionStatus predictionStatus);
    Long countByResultLabel(Boolean resultLabel);
    Long countByCompletedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
