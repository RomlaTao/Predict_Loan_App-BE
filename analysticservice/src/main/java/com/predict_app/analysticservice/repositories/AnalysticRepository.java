package com.predict_app.analysticservice.repositories;

import com.predict_app.analysticservice.entities.PredictionAnalystic;
import com.predict_app.analysticservice.dtos.EmployeePredictionCountDto;
import com.predict_app.analysticservice.enums.PredictionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AnalysticRepository extends JpaRepository<PredictionAnalystic, UUID> {
    Optional<PredictionAnalystic> findById(UUID predictionId);
    List<PredictionAnalystic> findAll();
    List<PredictionAnalystic> findByCustomerId(UUID customerId);
    List<PredictionAnalystic> findByEmployeeId(UUID employeeId);
    List<PredictionAnalystic> findByCustomerIdAndEmployeeId(UUID customerId, UUID employeeId);
    List<PredictionAnalystic> findByCompletedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<PredictionAnalystic> findByResultLabel(Boolean resultLabel);
    List<PredictionAnalystic> findByProbabilityBetween(Double minProbability, Double maxProbability);
    List<PredictionAnalystic> findByPredictionStatus(PredictionStatus predictionStatus);
    Long countByResultLabel(Boolean resultLabel);
    Long countByCompletedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Long> countByEmployeeId(UUID employeeId);
    List<Long> countByEmployeeIdAndCompletedAtBetween(UUID employeeId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Thống kê số lượng prediction (accepted / rejected) theo từng employee.
     */
    @org.springframework.data.jpa.repository.Query(
        "SELECT new com.predict_app.analysticservice.dtos.EmployeePredictionCountDto(" +
        "  p.employeeId, " +
        "  SUM(CASE WHEN p.resultLabel = TRUE THEN 1 ELSE 0 END), " +
        "  SUM(CASE WHEN p.resultLabel = FALSE THEN 1 ELSE 0 END)" +
        ") " +
        "FROM PredictionAnalystic p " +
        "GROUP BY p.employeeId"
    )
    List<EmployeePredictionCountDto> getEmployeePredictionCounts();
}
