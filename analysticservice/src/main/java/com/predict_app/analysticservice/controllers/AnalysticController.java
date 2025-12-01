package com.predict_app.analysticservice.controllers;

import com.predict_app.analysticservice.services.AnalysticService;
import com.predict_app.analysticservice.dtos.AnalysticResponseDto;
import com.predict_app.analysticservice.dtos.AnalysticStatDto;
import com.predict_app.analysticservice.enums.PredictionStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import java.util.UUID;
import java.util.List;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/analystics")
@RequiredArgsConstructor
public class AnalysticController {
    private final AnalysticService analysticService;

    @GetMapping("/{predictionId}")
    public ResponseEntity<AnalysticResponseDto> getAnalysticDataById(
        @PathVariable UUID predictionId,
        @RequestHeader("X-User-Role") String role) {
        if (role.equals("ROLE_RISK_ANALYST")) {
            return ResponseEntity.ok(analysticService.getAnalysticDataById(predictionId));
        } else {
            throw new RuntimeException("You are not authorized to get this analystic data");
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AnalysticResponseDto>> getAnalysticsDataByCustomerId(
        @PathVariable UUID customerId,
        @RequestHeader("X-User-Role") String role) {
        if (role.equals("ROLE_RISK_ANALYST")) {
            return ResponseEntity.ok(analysticService.getAnalysticsDataByCustomerId(customerId));
        } else {
            throw new RuntimeException("You are not authorized to get this analystic data");
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AnalysticResponseDto>> getAnalysticsDataByEmployeeId(@PathVariable UUID employeeId,
        @RequestHeader("X-User-Role") String role) {
        if (role.equals("ROLE_RISK_ANALYST")) {
            return ResponseEntity.ok(analysticService.getAnalysticsDataByEmployeeId(employeeId));
        } else {
            throw new RuntimeException("You are not authorized to get this analystic data");
        }
    }

    @GetMapping("/customer/{customerId}/employee/{employeeId}")
    public ResponseEntity<List<AnalysticResponseDto>> getAnalysticsDataByCustomerIdAndEmployeeId(@PathVariable UUID customerId, @PathVariable UUID employeeId,
        @RequestHeader("X-User-Role") String role) {
        if (role.equals("ROLE_RISK_ANALYST")) {
            return ResponseEntity.ok(analysticService.getAnalysticsDataByCustomerIdAndEmployeeId(customerId, employeeId));
        } else {
            throw new RuntimeException("You are not authorized to get this analystic data");
        }
    }

    @GetMapping("/date-range/from={startDate}&to={endDate}")
    public ResponseEntity<List<AnalysticResponseDto>> getAnalysticsDataByDateRange(@PathVariable LocalDateTime startDate, @PathVariable LocalDateTime endDate,
        @RequestHeader("X-User-Role") String role) {
        if (role.equals("ROLE_RISK_ANALYST")) {
            return ResponseEntity.ok(analysticService.getAnalysticsDataByDateRange(startDate, endDate));
        } else {
            throw new RuntimeException("You are not authorized to get this analystic data");
        }
    }

    @GetMapping("/result-label/{resultLabel}")
    public ResponseEntity<List<AnalysticResponseDto>> getAnalysticsDataByResultLabel(@PathVariable Boolean resultLabel,
        @RequestHeader("X-User-Role") String role) {
        if (role.equals("ROLE_RISK_ANALYST")) {
            return ResponseEntity.ok(analysticService.getAnalysticsDataByResultLabel(resultLabel));
        } else {
            throw new RuntimeException("You are not authorized to get this analystic data");
        }
    }

    @GetMapping("/probability-range/from={minProbability}&to={maxProbability}")
    public ResponseEntity<List<AnalysticResponseDto>> getAnalysticsDataByProbabilityRange(@PathVariable Double minProbability, @PathVariable Double maxProbability,
        @RequestHeader("X-User-Role") String role) {
        if (role.equals("ROLE_RISK_ANALYST")) {
            return ResponseEntity.ok(analysticService.getAnalysticsDataByProbabilityRange(minProbability, maxProbability));
        } else {
            throw new RuntimeException("You are not authorized to get this analystic data");
        }
    }

    @GetMapping("/prediction-status/{predictionStatus}")
    public ResponseEntity<List<AnalysticResponseDto>> getAnalysticsDataByPredictionStatus(@PathVariable PredictionStatus predictionStatus,
        @RequestHeader("X-User-Role") String role) {
        if (role.equals("ROLE_RISK_ANALYST")) {
            return ResponseEntity.ok(analysticService.getAnalysticsDataByPredictionStatus(predictionStatus));
        } else {
            throw new RuntimeException("You are not authorized to get this analystic data");
        }
    }

    @GetMapping("/stat/overview")
    public ResponseEntity<AnalysticStatDto> getAnalysticStatDataOverview(@RequestHeader("X-User-Role") String role) {
        if (role.equals("ROLE_RISK_ANALYST")) {
            return ResponseEntity.ok(analysticService.getAnalysticStatDataOverview());
        } else {
            throw new RuntimeException("You are not authorized to get this analystic data");
        }
    }

    @GetMapping("/stat/date-range/from={startDate}&to={endDate}")
    public ResponseEntity<AnalysticStatDto> getAnalysticStatDataByDateRange(@PathVariable LocalDateTime startDate, @PathVariable LocalDateTime endDate,
        @RequestHeader("X-User-Role") String role) {
        if (role.equals("ROLE_RISK_ANALYST")) {
            return ResponseEntity.ok(analysticService.getAnalysticStatDataByDateRange(startDate, endDate));
        } else {
            throw new RuntimeException("You are not authorized to get this analystic data");
        }
    }
}
