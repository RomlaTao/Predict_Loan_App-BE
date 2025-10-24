package com.predict_app.predictionservice.services.impl;

import com.predict_app.predictionservice.dtos.MLModelRequestDto;
import com.predict_app.predictionservice.dtos.MLModelResponseDto;
import com.predict_app.predictionservice.services.MLModelService;
import com.predict_app.predictionservice.services.RabbitMQProducerService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MLModelServiceImpl implements MLModelService {

    private final RabbitMQProducerService rabbitMQProducerService;

    public MLModelServiceImpl(RabbitMQProducerService rabbitMQProducerService) {
        this.rabbitMQProducerService = rabbitMQProducerService;
    }

    @Override
    public MLModelResponseDto predict(MLModelRequestDto request) {
        try {
            log.info("Processing prediction request via RabbitMQ");
            
            // Send request to ML Model via RabbitMQ
            MLModelResponseDto response = rabbitMQProducerService.sendPredictionRequest(request);
            
            log.info("Received prediction response: {}", response.getPrediction());
            return response;

        } catch (Exception e) {
            log.error("Error in ML Model prediction: {}", e.getMessage(), e);
            return MLModelResponseDto.builder()
                    .prediction(false)
                    .confidence(0.0)
                    .message("Error calling ML Model via RabbitMQ: " + e.getMessage())
                    .build();
        }
    }
}
