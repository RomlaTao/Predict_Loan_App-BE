package com.predict_app.predictionservice.publishers.impl;

import com.predict_app.predictionservice.publishers.PredictionEventPublisher;
import com.predict_app.predictionservice.dtos.events.PredictionRequestedEventDto;
import com.predict_app.predictionservice.dtos.events.ModelPredictRequestedEventDto;
import com.predict_app.predictionservice.dtos.events.PredictionCompletedEventDto;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PredictionEventPublisherImpl implements PredictionEventPublisher {

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

    @Autowired
    private final RabbitTemplate rabbitTemplate;

    public PredictionEventPublisherImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishCustomerProfileRequestedEvent(PredictionRequestedEventDto predictionRequestedEventDto) {

        PredictionRequestedEventDto requestEventDto = PredictionRequestedEventDto.builder()
            .predictionId(predictionRequestedEventDto.getPredictionId())
            .customerId(predictionRequestedEventDto.getCustomerId())
            .employeeId(predictionRequestedEventDto.getEmployeeId())
            .requestedAt(predictionRequestedEventDto.getRequestedAt())
            .build();

        rabbitTemplate.convertAndSend(customerProfileRequestedExchangeName, customerProfileRequestedRoutingKey, requestEventDto);
    }

    @Override
    public void publishModelPredictRequestedEvent(ModelPredictRequestedEventDto modelPredictRequestedEventDto) {
        
        ModelPredictRequestedEventDto requestEventDto = ModelPredictRequestedEventDto.builder()
            .predictionId(modelPredictRequestedEventDto.getPredictionId())
            .customerId(modelPredictRequestedEventDto.getCustomerId())
            .input(modelPredictRequestedEventDto.getInput())
            .build();

        rabbitTemplate.convertAndSend(modelPredictRequestedExchangeName, modelPredictRequestedRoutingKey, requestEventDto);
    }

    @Override
    public void publishPredictionCompletedEvent(PredictionCompletedEventDto predictionCompletedEventDto) {
    }
}
