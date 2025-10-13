package com.predict_app.userservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    @Id
    private UUID userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private Integer experience;

    @Column(nullable = false)
    private Double income; // Annual income in $K

    @Column(nullable = false)
    private Integer family;

    @Column(nullable = false, name = "cc_avg")
    private Double ccAvg; // Average monthly credit card spend

    @Column(nullable = false)
    private Integer education; // 1=Undergrad, 2=Graduate, 3=Doctoral

    @Column(nullable = false)
    private Double mortgage; // Mortgage loan amount in $K

    @Column(nullable = false, name = "securities_account")
    private Boolean securitiesAccount;

    @Column(nullable = false, name = "cd_account")
    private Boolean cdAccount;

    @Column(nullable = false)
    private Boolean online;

    @Column(nullable = false, name = "credit_card")
    private Boolean creditCard;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;

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
