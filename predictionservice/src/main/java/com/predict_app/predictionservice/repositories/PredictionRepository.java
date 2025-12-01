package com.predict_app.predictionservice.repositories;

import com.predict_app.predictionservice.entities.Prediction;
import com.predict_app.predictionservice.enums.PredictionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

@Repository
public interface PredictionRepository extends JpaRepository<Prediction, UUID> {
    List<Prediction> findByCustomerId(UUID customerId);
    List<Prediction> findByEmployeeId(UUID employeeId);
    List<Prediction> findByStatus(PredictionStatus status);
    List<Prediction> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    Long countPredictions();
    Long countApprovedPredictions();
    Long countRejectedPredictions();
}
