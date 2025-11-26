package com.predict_app.predictionservice.controllers;

import com.predict_app.predictionservice.dtos.PredictionRequestDto;
import com.predict_app.predictionservice.dtos.PredictionResponseDto;
import com.predict_app.predictionservice.services.PredictionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/predictions")
public class PredictionController {

    private final PredictionService predictionService;

    PredictionController(PredictionService predictionService){
        this.predictionService = predictionService;
    }

    @PostMapping
    public ResponseEntity<PredictionResponseDto> createPrediction(
            @Valid @RequestBody PredictionRequestDto request,
            @RequestHeader("X-User-Id") UUID staffId,
            @RequestHeader("X-User-Role") String role) {
        if (role.equals("ROLE_STAFF")) {
            return ResponseEntity.ok(predictionService.createPrediction(request, staffId));
        } else {
            throw new RuntimeException("You are not authorized to create a prediction");
        }
    }

    @GetMapping("/{predictionId}")
    public ResponseEntity<PredictionResponseDto> getPredictionById(
        @PathVariable UUID predictionId,
        @RequestHeader("X-User-Id") UUID staffId,
        @RequestHeader("X-User-Role") String role) {
        if (role.equals("ROLE_STAFF") || role.equals("ROLE_RISK_ANALYST")) {
            return ResponseEntity.ok(predictionService.getPredictionById(predictionId, staffId, role));
        } else {
            throw new RuntimeException("You are not authorized to get this prediction");
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<PredictionResponseDto>> getPredictionsByCustomerId(
        @PathVariable UUID customerId,
        @RequestHeader("X-User-Role") String role) {
        if (role.equals("ROLE_STAFF") || role.equals("ROLE_RISK_ANALYST")) {
            return ResponseEntity.ok(predictionService.getPredictionsByCustomerId(customerId));
        } else {
            throw new RuntimeException("You are not authorized to get this prediction");
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<PredictionResponseDto>> getPredictionsByEmployeeId(
        @PathVariable UUID employeeId,
        @RequestHeader("X-User-Role") String role) {
        if (role.equals("ROLE_STAFF") || role.equals("ROLE_RISK_ANALYST")) {
            return ResponseEntity.ok(predictionService.getPredictionsByEmployeeId(employeeId));
        } else {
            throw new RuntimeException("You are not authorized to get this prediction");
        }
    }


    @GetMapping("/employee/me")
    public ResponseEntity<List<PredictionResponseDto>> getCurrentEmployeePredictions(
        @RequestHeader("X-User-Id") UUID employeeId,
        @RequestHeader("X-User-Role") String role) {
        if (role.equals("ROLE_STAFF")) {
            return ResponseEntity.ok(predictionService.getPredictionsByEmployeeId(employeeId));
        } else {
            throw new RuntimeException("You are not authorized to get this prediction");
        }
    }

    @GetMapping
    public ResponseEntity<List<PredictionResponseDto>> getAllPredictions(@RequestHeader("X-User-Role") String role) {
        if (role.equals("ROLE_STAFF") || role.equals("ROLE_RISK_ANALYST")) {
            return ResponseEntity.ok(predictionService.getAllPredictions());
        } else {
            throw new RuntimeException("You are not authorized to get all predictions");
        }
    }
}
