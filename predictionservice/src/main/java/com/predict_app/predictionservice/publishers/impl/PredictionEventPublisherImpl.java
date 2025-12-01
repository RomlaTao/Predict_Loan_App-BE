package com.predict_app.predictionservice.publishers.impl;

import com.predict_app.predictionservice.publishers.PredictionEventPublisher;
import com.predict_app.predictionservice.dtos.events.PredictionRequestedEventDto;
import com.predict_app.predictionservice.dtos.events.ModelPredictRequestedEventDto;
import com.predict_app.predictionservice.dtos.events.PredictionCompletedCusomterEventDto;
import com.predict_app.predictionservice.dtos.events.PredictionCompletedAnalysticEventDto;

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

    @Value("${rabbitmq.routing-key.customer-profile-requested}")
    private String customerProfileRequestedRoutingKey;

    @Value("${rabbitmq.exchange.model-predict-requested}")
    private String modelPredictRequestedExchangeName;

    @Value("${rabbitmq.routing-key.model-predict-requested}")
    private String modelPredictRequestedRoutingKey;

    @Value("${rabbitmq.exchange.model-predict-completed}")
    private String modelPredictCompletedExchangeName;

    @Value("${rabbitmq.routing-key.model-predict-completed}")
    private String modelPredictCompletedRoutingKey;

    @Value("${rabbitmq.exchange.prediction-completed}")
    private String predictionCompletedExchangeName;

    @Value("${rabbitmq.routing-key.prediction-completed-customer}")
    private String predictionCompletedCustomerRoutingKey;

    @Value("${rabbitmq.routing-key.prediction-completed-analytics}")
    private String predictionCompletedAnalyticsRoutingKey;

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
    public void publishPredictionCompletedEvent(PredictionCompletedCusomterEventDto predictionCompletedEventDto) {
        String predictionId = predictionCompletedEventDto.getPredictionId() != null 
            ? predictionCompletedEventDto.getPredictionId().toString() 
            : "null";
        String customerId = predictionCompletedEventDto.getCustomerId() != null 
            ? predictionCompletedEventDto.getCustomerId().toString() 
            : "null";
        
        logger.info("📤 [PREDICTION→CUSTOMER] Publishing PredictionCompletedEvent - PredictionId: {}, CustomerId: {}", 
            predictionId, customerId);

        try {
            PredictionCompletedCusomterEventDto completedEventDto = PredictionCompletedCusomterEventDto.builder()
                .predictionId(predictionCompletedEventDto.getPredictionId())
                .customerId(predictionCompletedEventDto.getCustomerId())
                .resultLabel(predictionCompletedEventDto.getResultLabel())
                .build();

            rabbitTemplate.convertAndSend(predictionCompletedExchangeName, predictionCompletedCustomerRoutingKey, completedEventDto);

            logger.info("✅ [PREDICTION→CUSTOMER] Successfully published PredictionCompletedEvent - PredictionId: {}, CustomerId: {}", 
                predictionId, customerId);
        }
        catch (Exception e) {
            logger.error("❌ [PREDICTION→CUSTOMER] Failed to publish PredictionCompletedEvent - PredictionId: {}, CustomerId: {}, Error: {}", 
                predictionId, customerId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void publishPredictionCompletedAnalyticsEvent(PredictionCompletedAnalysticEventDto predictionCompletedEventDto) {
        String predictionId = predictionCompletedEventDto.getPredictionId() != null
            ? predictionCompletedEventDto.getPredictionId().toString()
            : "null";
        String customerId = predictionCompletedEventDto.getCustomerId() != null
            ? predictionCompletedEventDto.getCustomerId().toString()
            : "null";

        logger.info("📤 [PREDICTION→ANALYTICS] Publishing PredictionCompletedAnalyticsEvent - PredictionId: {}, CustomerId: {}",
            predictionId, customerId);

        try {
            rabbitTemplate.convertAndSend(
                predictionCompletedExchangeName,
                predictionCompletedAnalyticsRoutingKey,
                predictionCompletedEventDto
            );

            logger.info("✅ [PREDICTION→ANALYTICS] Successfully published PredictionCompletedAnalyticsEvent - PredictionId: {}, CustomerId: {}",
                predictionId, customerId);
        } catch (Exception e) {
            logger.error("❌ [PREDICTION→ANALYTICS] Failed to publish PredictionCompletedAnalyticsEvent - PredictionId: {}, CustomerId: {}, Error: {}",
                predictionId, customerId, e.getMessage(), e);
            throw e;
        }
    }
}
