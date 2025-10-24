package com.predict_app.predictionservice.entities;

import com.predict_app.predictionservice.enums.PredictionStatus;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "predictions")
@Getter
@Setter
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

    @Column(nullable = false)
    private PredictionStatus status; // PENDING, COMPLETED, FAILED

    @Column(columnDefinition = "TEXT")
    private String inputData; // JSON data gửi đến ML model

    @Column(columnDefinition = "TEXT")
    private String predictionResult; // JSON kết quả từ ML model

    @Column
    private Double confidence; // Độ tin cậy của prediction

    @Column
    private String errorMessage; // Lỗi nếu có

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
