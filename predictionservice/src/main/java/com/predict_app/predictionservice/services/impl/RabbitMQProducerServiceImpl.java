package com.predict_app.predictionservice.services.impl;

import com.predict_app.predictionservice.dtos.MLModelRequestDto;
import com.predict_app.predictionservice.dtos.MLModelResponseDto;
import com.predict_app.predictionservice.dtos.PredictionMessageDto;
import com.predict_app.predictionservice.services.RabbitMQProducerService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class RabbitMQProducerServiceImpl implements RabbitMQProducerService {

    private final RabbitTemplate rabbitTemplate;
    
    @Value("${rabbitmq.exchange}")
    private String exchangeName;
    
    @Value("${rabbitmq.queue.request}")
    private String requestQueueName;
    
    @Value("${rabbitmq.queue.response}")
    private String responseQueueName;
    
    @Value("${rabbitmq.timeout:30000}")
    private long timeoutMs;

    public RabbitMQProducerServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public MLModelResponseDto sendPredictionRequest(MLModelRequestDto request) {
        try {
            String correlationId = UUID.randomUUID().toString();
            String requestId = UUID.randomUUID().toString();
            
            // Create message
            PredictionMessageDto message = PredictionMessageDto.builder()
                    .correlationId(correlationId)
                    .requestId(requestId)
                    .request(request)
                    .timestamp(LocalDateTime.now())
                    .status("PENDING")
                    .build();

            log.info("Sending prediction request with correlationId: {}", correlationId);

            // Send message to request queue
            rabbitTemplate.convertAndSend(
                    exchangeName, 
                    "prediction.request", 
                    message
            );

            // Wait for response
            return waitForResponse(correlationId);

        } catch (Exception e) {
            log.error("Error sending prediction request: {}", e.getMessage(), e);
            return MLModelResponseDto.builder()
                    .prediction(false)
                    .confidence(0.0)
                    .message("Error sending prediction request: " + e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }

    @Override
    public MLModelResponseDto waitForResponse(String correlationId) {
        try {
            // In a real implementation, you would use a correlation-based response mechanism
            // For now, we'll simulate waiting and return a default response
            // This should be replaced with actual correlation-based response handling
            
            log.info("Waiting for response for correlationId: {}", correlationId);
            
            // Simulate waiting (in production, this would be handled by response listeners)
            Thread.sleep(2000);
            
            // For demonstration, return a mock response
            // In production, this would come from the actual ML model response
            return MLModelResponseDto.builder()
                    .prediction(true)
                    .confidence(0.85)
                    .probabilities(java.util.Map.of(
                        "Không chấp nhận", 0.15,
                        "Chấp nhận", 0.85
                    ))
                    .message("Prediction completed via RabbitMQ")
                    .timestamp(LocalDateTime.now())
                    .build();
                    
        } catch (Exception e) {
            log.error("Error waiting for response: {}", e.getMessage(), e);
            return MLModelResponseDto.builder()
                    .prediction(false)
                    .confidence(0.0)
                    .message("Timeout waiting for ML Model response")
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }
}
