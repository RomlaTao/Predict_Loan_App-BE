package com.predict_app.customerservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customer_profiles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProfile {

    @Id
    private UUID customerId;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email; // từ authservice

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private Integer experience; // số năm kinh nghiệm

    @Column(nullable = false)
    private Double income; // thu nhập hằng năm (nghìn USD)

    @Column(nullable = false)
    private Integer family; // số thành viên gia đình

    @Column(nullable = false)
    private Integer education; // 1=Undergrad, 2=Graduate, 3=Doctoral

    @Column(nullable = false)
    private Double mortgage; // khoản vay thế chấp (nghìn USD)

    @Column(nullable = false, name = "securities_account")
    private Boolean securitiesAccount;

    @Column(nullable = false, name = "cd_account")
    private Boolean cdAccount;

    @Column(nullable = false)
    private Boolean online;

    @Column(nullable = false, name = "credit_card")
    private Boolean creditCard;

    @Column(nullable = false, name = "cc_avg")
    private Double ccAvg; // chi tiêu trung bình hàng tháng bằng thẻ tín dụng

    @Column(name = "personal_loan")
    private Boolean personalLoan; // mục tiêu dự đoán: có chấp nhận khoản vay cá nhân hay không

    @Column(name = "staff_id")
    private UUID staffId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
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
