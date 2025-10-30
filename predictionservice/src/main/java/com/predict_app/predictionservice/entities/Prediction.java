package com.predict_app.predictionservice.entities;

import com.predict_app.predictionservice.enums.PredictionStatus;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "predictions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Prediction {

    @Id
    private UUID predictionId;

    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = false)
    private UUID employeeId; // Nhân viên tạo prediction

    @Column
    private PredictionStatus status;

    @Column(columnDefinition = "TEXT")
    private String inputData; // Dữ liệu đầu vào

    @Column(columnDefinition = "TEXT")
    private String predictionResult; // Kết quả từ ML model

    @Column
    private Double confidence; // Độ tin cậy của prediction

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
