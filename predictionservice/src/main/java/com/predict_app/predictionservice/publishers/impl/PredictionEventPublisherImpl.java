package com.predict_app.predictionservice.publishers.impl;

import com.predict_app.predictionservice.publishers.PredictionEventPublisher;
import com.predict_app.predictionservice.dtos.events.PredictionRequestedEventDto;
import com.predict_app.predictionservice.dtos.events.ModelPredictRequestedEventDto;
import com.predict_app.predictionservice.dtos.events.PredictionCompletedEventDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PredictionEventPublisherImpl implements PredictionEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(PredictionEventPublisherImpl.class);

    @Value("${rabbitmq.exchange.customer-profile-requested}")
    private String customerProfileRequestedExchangeName;

    @Value("${rabbitmq.queue.customer-profile-requested}")
    private String customerProfileRequestedQueueName;

    @Value("${rabbitmq.routing-key.customer-profile-requested}")
    private String customerProfileRequestedRoutingKey;

    @Value("${rabbitmq.exchange.model-predict-requested}")
    private String modelPredictRequestedExchangeName;

    @Value("${rabbitmq.queue.model-predict-requested}")
    private String modelPredictRequestedQueueName;

    @Value("${rabbitmq.routing-key.model-predict-requested}")
    private String modelPredictRequestedRoutingKey;

    @Value("${rabbitmq.exchange.model-predict-completed}")
    private String modelPredictCompletedExchangeName;

    @Value("${rabbitmq.queue.model-predict-completed}")
    private String modelPredictCompletedQueueName;

    @Value("${rabbitmq.routing-key.model-predict-completed}")
    private String modelPredictCompletedRoutingKey;

    @Value("${rabbitmq.exchange.prediction-completed}")
    private String predictionCompletedExchangeName;

    @Value("${rabbitmq.queue.prediction-completed}")
    private String predictionCompletedQueueName;

    @Value("${rabbitmq.routing-key.prediction-completed}")
    private String predictionCompletedRoutingKey;

    @Autowired
    private final RabbitTemplate rabbitTemplate;

    public PredictionEventPublisherImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishCustomerProfileRequestedEvent(PredictionRequestedEventDto predictionRequestedEventDto) {
        String predictionId = predictionRequestedEventDto.getPredictionId() != null 
            ? predictionRequestedEventDto.getPredictionId().toString() 
            : "null";
        String customerId = predictionRequestedEventDto.getCustomerId() != null 
            ? predictionRequestedEventDto.getCustomerId().toString() 
            : "null";
        
        logger.info("📤 [PREDICTION→CUSTOMER] Publishing CustomerProfileRequestedEvent - PredictionId: {}, CustomerId: {}", 
            predictionId, customerId);
        logger.debug("📤 [PREDICTION→CUSTOMER] Exchange: {}, RoutingKey: {}, EmployeeId: {}, RequestedAt: {}", 
            customerProfileRequestedExchangeName, customerProfileRequestedRoutingKey, 
            predictionRequestedEventDto.getEmployeeId(), predictionRequestedEventDto.getRequestedAt());

        try {
            PredictionRequestedEventDto requestEventDto = PredictionRequestedEventDto.builder()
                .predictionId(predictionRequestedEventDto.getPredictionId())
                .customerId(predictionRequestedEventDto.getCustomerId())
                .employeeId(predictionRequestedEventDto.getEmployeeId())
                .requestedAt(predictionRequestedEventDto.getRequestedAt())
                .build();

            rabbitTemplate.convertAndSend(customerProfileRequestedExchangeName, customerProfileRequestedRoutingKey, requestEventDto);
            
            logger.info("✅ [PREDICTION→CUSTOMER] Successfully published CustomerProfileRequestedEvent - PredictionId: {}, CustomerId: {}", 
                predictionId, customerId);
        } catch (Exception e) {
            logger.error("❌ [PREDICTION→CUSTOMER] Failed to publish CustomerProfileRequestedEvent - PredictionId: {}, CustomerId: {}, Error: {}", 
                predictionId, customerId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void publishModelPredictRequestedEvent(ModelPredictRequestedEventDto modelPredictRequestedEventDto) {
        String predictionId = modelPredictRequestedEventDto.getPredictionId() != null 
            ? modelPredictRequestedEventDto.getPredictionId().toString() 
            : "null";
        String customerId = modelPredictRequestedEventDto.getCustomerId() != null 
            ? modelPredictRequestedEventDto.getCustomerId().toString() 
            : "null";
        
        logger.info("📤 [PREDICTION→ML_MODEL] Publishing ModelPredictRequestedEvent - PredictionId: {}, CustomerId: {}", 
            predictionId, customerId);
        logger.debug("📤 [PREDICTION→ML_MODEL] Exchange: {}, RoutingKey: {}", 
            modelPredictRequestedExchangeName, modelPredictRequestedRoutingKey);

        try {
            ModelPredictRequestedEventDto requestEventDto = ModelPredictRequestedEventDto.builder()
                .predictionId(modelPredictRequestedEventDto.getPredictionId())
                .customerId(modelPredictRequestedEventDto.getCustomerId())
                .input(modelPredictRequestedEventDto.getInput())
                .build();

            rabbitTemplate.convertAndSend(modelPredictRequestedExchangeName, modelPredictRequestedRoutingKey, requestEventDto);
            
            logger.info("✅ [PREDICTION→ML_MODEL] Successfully published ModelPredictRequestedEvent - PredictionId: {}, CustomerId: {}", 
                predictionId, customerId);
        } catch (Exception e) {
            logger.error("❌ [PREDICTION→ML_MODEL] Failed to publish ModelPredictRequestedEvent - PredictionId: {}, CustomerId: {}, Error: {}", 
                predictionId, customerId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void publishPredictionCompletedEvent(PredictionCompletedEventDto predictionCompletedEventDto) {
        String predictionId = predictionCompletedEventDto.getPredictionId() != null 
            ? predictionCompletedEventDto.getPredictionId().toString() 
            : "null";
        String customerId = predictionCompletedEventDto.getCustomerId() != null 
            ? predictionCompletedEventDto.getCustomerId().toString() 
            : "null";
        
        logger.info("📤 [PREDICTION→CUSTOMER] Publishing PredictionCompletedEvent - PredictionId: {}, CustomerId: {}", 
            predictionId, customerId);

        try {
            PredictionCompletedEventDto completedEventDto = PredictionCompletedEventDto.builder()
                .predictionId(predictionCompletedEventDto.getPredictionId())
                .customerId(predictionCompletedEventDto.getCustomerId())
                .resultLabel(predictionCompletedEventDto.getResultLabel())
                .probability(predictionCompletedEventDto.getProbability())
                .completedAt(predictionCompletedEventDto.getCompletedAt())
                .build();

            rabbitTemplate.convertAndSend(predictionCompletedExchangeName, predictionCompletedRoutingKey, completedEventDto);

            logger.info("✅ [PREDICTION→CUSTOMER] Successfully published PredictionCompletedEvent - PredictionId: {}, CustomerId: {}", 
                predictionId, customerId);
        }
        catch (Exception e) {
            logger.error("❌ [PREDICTION→CUSTOMER] Failed to publish PredictionCompletedEvent - PredictionId: {}, CustomerId: {}, Error: {}", 
                predictionId, customerId, e.getMessage(), e);
            throw e;
        }
    }
}
