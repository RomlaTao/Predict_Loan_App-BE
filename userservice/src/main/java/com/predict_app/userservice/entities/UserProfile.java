package com.predict_app.userservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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

    @Column(length = 100)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email; // từ authservice

    @Column(length = 50)
    private String department; // Credit, Risk, Operations, etc.

    @Column(length = 50)
    private String position; // Manager, Analyst, Officer, etc.

    @Column
    private LocalDate hireDate;

    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 200)
    private String address;

    @Column
    @Builder.Default
    private Boolean isActive = true;

    @Column
    @Builder.Default
    private Boolean profileCompleted = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", updatable = true)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
