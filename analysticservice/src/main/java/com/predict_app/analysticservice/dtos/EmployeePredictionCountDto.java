package com.predict_app.analysticservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Thống kê số lượng prediction (chấp nhận / từ chối) theo từng employee.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePredictionCountDto {

    private UUID employeeId;

    /**
     * Số lượng prediction có resultLabel = true (chấp nhận).
     */
    private Long acceptedCount;

    /**
     * Số lượng prediction có resultLabel = false (từ chối).
     */
    private Long rejectedCount;
}


