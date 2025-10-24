package com.predict_app.predictionservice.controllers;

import com.predict_app.predictionservice.dtos.PredictionRequestDto;
import com.predict_app.predictionservice.dtos.PredictionResponseDto;
import com.predict_app.predictionservice.services.PredictionService;
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
    public ResponseEntity<PredictionResponseDto> createPrediction(@RequestBody PredictionRequestDto request) {
        return ResponseEntity.ok(predictionService.createPrediction(request));
    }

    @GetMapping("/{predictionId}")
    public ResponseEntity<PredictionResponseDto> getPrediction(@PathVariable UUID predictionId) {
        return ResponseEntity.ok(predictionService.getPredictionById(predictionId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<PredictionResponseDto>> getPredictionsByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(predictionService.getPredictionsByCustomerId(customerId));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<PredictionResponseDto>> getPredictionsByEmployee(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(predictionService.getPredictionsByEmployeeId(employeeId));
    }

    @GetMapping
    public ResponseEntity<List<PredictionResponseDto>> getAllPredictions() {
        return ResponseEntity.ok(predictionService.getAllPredictions());
    }

    @PutMapping("/{predictionId}/status")
    public ResponseEntity<PredictionResponseDto> updatePredictionStatus(
            @PathVariable UUID predictionId,
            @RequestParam String status) {
        return ResponseEntity.ok(predictionService.updatePredictionStatus(predictionId, status));
    }
}
