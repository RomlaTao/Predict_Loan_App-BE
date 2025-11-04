package com.predict_app.predictionservice.listeners.impl;

import com.predict_app.predictionservice.dtos.events.ModelPredictCompletedEventDto;
import com.predict_app.predictionservice.services.PredictionService;
import com.predict_app.predictionservice.listeners.ModelListener;
import com.predict_app.predictionservice.publishers.PredictionEventPublisher;
import com.predict_app.predictionservice.dtos.events.PredictionCompletedEventDto;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

@Service
public class ModelListenerImpl implements ModelListener {

    @Autowired
    private final PredictionService predictionService;

    @Autowired
    private final PredictionEventPublisher predictionEventPublisher;

    private static final Logger logger = LoggerFactory.getLogger(ModelListenerImpl.class);

    public ModelListenerImpl(PredictionService predictionService, PredictionEventPublisher predictionEventPublisher) {
        this.predictionService = predictionService;
        this.predictionEventPublisher = predictionEventPublisher;
    }

    @RabbitListener(queues = "${rabbitmq.queue.model-predict-completed}")
    @Override
    public void handleModelPredictCompletedEvent(ModelPredictCompletedEventDto event,
                                          Channel channel,
                                          @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            long startTime = System.currentTimeMillis();

            String predictionId = event != null && event.getPredictionId() != null
                    ? event.getPredictionId().toString() : "null";

            logger.info("📥 [PREDICTION] Received ModelPredictCompletedEvent - PredictionId: {}, DeliveryTag: {}",
                    predictionId, deliveryTag);

            if (event == null) {
                throw new RuntimeException("Event payload is null");
            }
            if (event.getPredictionId() == null) {
                throw new RuntimeException("Prediction ID is required");
            }

            if (event.getResult() == null) {
                throw new RuntimeException("Prediction result is required");
            }

            Boolean label = event.getResult().getLabel();
            Double probability = event.getResult().getProbability();

            logger.debug("🔎 [PREDICTION] Updating prediction result - PredictionId: {}, Label: {}, Probability: {}",
                    predictionId, label.toString(), probability);

            predictionService.setPredictionResult(event.getPredictionId(), label, probability);

            long processingTime = System.currentTimeMillis() - startTime;

            if (channel != null && channel.isOpen()) {
                channel.basicAck(deliveryTag, false);
                logger.info("✅ [PREDICTION] Successfully processed ModelPredictCompletedEvent - PredictionId: {}, ProcessingTime: {}ms, DeliveryTag: {}",
                        predictionId, processingTime, deliveryTag);
            } else {
                logger.warn("⚠️ [PREDICTION] Channel is not open when acknowledging - PredictionId: {}, DeliveryTag: {}",
                        predictionId, deliveryTag);
            }

            PredictionCompletedEventDto predictionCompletedEventDto = PredictionCompletedEventDto.builder()
                .predictionId(event.getPredictionId())
                .customerId(event.getCustomerId())
                .resultLabel(label)
                .probability(probability)
                .completedAt(event.getPredictedAt())
                .build();

            predictionEventPublisher.publishPredictionCompletedEvent(predictionCompletedEventDto);
            
            logger.info("📤 [PREDICTION→CUSTOMER] Publishing PredictionCompletedEvent - PredictionId: {}, CustomerId: {}", 
                predictionId, event.getCustomerId());
        } catch (Exception e) {
            try {
                String predictionId = (event != null && event.getPredictionId() != null)
                        ? event.getPredictionId().toString() : "null";
                logger.error("❌ [PREDICTION] Failed to process ModelPredictCompletedEvent - PredictionId: {}, DeliveryTag: {}, Error: {}",
                        predictionId, deliveryTag, e.getMessage(), e);

                if (channel != null && channel.isOpen()) {
                    // Reject và không requeue
                    channel.basicNack(deliveryTag, false, false);
                } else {
                    logger.warn("⚠️ [PREDICTION] Channel is not open when nacking - PredictionId: {}, DeliveryTag: {}",
                            predictionId, deliveryTag);
                }
            } catch (IOException ioException) {
                throw new RuntimeException("Failed to acknowledge message", ioException);
            }
        }
    } 
}
