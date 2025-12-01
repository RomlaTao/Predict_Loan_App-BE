package com.predict_app.analysticservice.entities;

import com.predict_app.analysticservice.enums.PredictionStatus;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "prediction_analystic")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionAnalystic {

    @Id
    private UUID predictionId;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "employee_id")
    private UUID employeeId;
    
    @Column(name = "status")
    private PredictionStatus status;
    
    @Column(name = "result_label")
    private Boolean resultLabel;
    
    @Column(name = "probability")
    private Double probability;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // Snapshot of key customer attributes at prediction time
    @Column(name = "age")
    private Integer age;

    @Column(name = "experience")
    private Integer experience;

    @Column(name = "income")
    private Double income;

    @Column(name = "family")
    private Integer family;
    
    @Column(name = "education")
    private Integer education;

    @Column(name = "mortgage")
    private Double mortgage;
    
    @Column(name = "securities_account")
    private Boolean securitiesAccount;

    @Column(name = "cd_account")
    private Boolean cdAccount;

    @Column(name = "online")
    private Boolean online;

    @Column(name = "credit_card")
    private Boolean creditCard;

    @Column(name = "cc_avg")
    private Double ccAvg;
}
