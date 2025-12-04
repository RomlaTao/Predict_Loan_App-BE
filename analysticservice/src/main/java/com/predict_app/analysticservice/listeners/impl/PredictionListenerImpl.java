package com.predict_app.analysticservice.listeners.impl;

import com.predict_app.analysticservice.repositories.AnalysticRepository;
import com.predict_app.analysticservice.dtos.events.PredictionCompletedEventDto;
import com.predict_app.analysticservice.listeners.PredictionListener;
import com.predict_app.analysticservice.enums.PredictionStatus;
import com.predict_app.analysticservice.entities.PredictionAnalystic;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PredictionListenerImpl implements PredictionListener {

    private static final Logger logger = LoggerFactory.getLogger(PredictionListenerImpl.class);

    private final AnalysticRepository analysticRepository;

    @RabbitListener(queues = "${rabbitmq.queue.prediction-completed-analytics}")
    @Override
    public void handlePredictionCompletedEvent(PredictionCompletedEventDto event,
                                    Channel channel,
                                    @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        long startTime = System.currentTimeMillis();
        String predictionId = event.getPredictionId() != null ? event.getPredictionId().toString() : "null";
        String customerId = event.getCustomerId() != null ? event.getCustomerId().toString() : "null";

        logger.info("📥 [ANALYTIC] Received PredictionCompletedEvent - PredictionId: {}, CustomerId: {}, DeliveryTag: {}", 
            predictionId, customerId, deliveryTag);

        try {
            // Validation
            if (event.getPredictionId() == null) {
            logger.error("❌ [ANALYTIC] Validation failed: Prediction ID is required - DeliveryTag: {}", deliveryTag);
                throw new RuntimeException("Prediction ID is required");
            }

            logger.info("✅ [ANALYTIC] PredictionCompletedEvent received - PredictionId: {}, CustomerId: {}, ResultLabel: {}, Probability: {}, CompletedAt: {}", 
                predictionId, customerId, event.getResultLabel(), event.getProbability(), event.getCompletedAt());

            PredictionAnalystic predictionAnalystic = PredictionAnalystic.builder()
                .predictionId(event.getPredictionId())
                .customerId(event.getCustomerId())
                .employeeId(event.getEmployeeId())
                .predictionStatus(event.getPredictionStatus() != null ? PredictionStatus.valueOf(event.getPredictionStatus()) : null)
                .resultLabel(event.getResultLabel())
                .probability(event.getProbability())
                .createdAt(event.getCreatedAt())
                .completedAt(event.getCompletedAt())
                .age(event.getAge())
                .experience(event.getExperience())
                .income(event.getIncome())
                .family(event.getFamily())
                .education(event.getEducation())
                .mortgage(event.getMortgage())
                .securitiesAccount(event.getSecuritiesAccount())
                .cdAccount(event.getCdAccount())
                .online(event.getOnline())
                .creditCard(event.getCreditCard())
                .ccAvg(event.getCcAvg())
                .build();

            analysticRepository.save(predictionAnalystic);
            logger.info("✅ [ANALYTIC] PredictionAnalystic saved - PredictionId: {}, CustomerId: {}, ResultLabel: {}, Probability: {}, CompletedAt: {}", 
                predictionId, customerId, event.getResultLabel(), event.getProbability(), event.getCompletedAt());

            channel.basicAck(deliveryTag, false);
            logger.info("✅ [ANALYTIC] Message acknowledged - DeliveryTag: {}", deliveryTag);

        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            logger.error("❌ [ANALYTIC] Failed to process PredictionCompletedEvent - PredictionId: {}, CustomerId: {}, ProcessingTime: {}ms, DeliveryTag: {}, Error: {}", 
                predictionId, customerId, processingTime, deliveryTag, e.getMessage(), e);
            try {
                // Reject và không requeue
                channel.basicNack(deliveryTag, false, false);
                logger.warn("⚠️ [ANALYTIC] Message rejected and not requeued - DeliveryTag: {}", deliveryTag);
            } catch (Exception e2) {
                logger.error("❌ [ANALYTIC] Failed to acknowledge/reject message - DeliveryTag: {}, Error: {}", 
                    deliveryTag, e2.getMessage(), e2);
                throw new RuntimeException("Failed to acknowledge message", e);
            }
        }
    }
}
