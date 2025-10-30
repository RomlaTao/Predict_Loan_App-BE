package com.predict_app.predictionservice.listeners.impl;

import com.predict_app.predictionservice.dtos.events.ModelPredictCompletedEventDto;
import com.predict_app.predictionservice.services.PredictionService;
import com.predict_app.predictionservice.listeners.ModelListener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;

@Service
public class ModelListenerImpl implements ModelListener {

    @Autowired
    private final PredictionService predictionService;

    public ModelListenerImpl(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.model-predict-completed}")
    @Override
    public void handleModelPredictCompletedEvent(ModelPredictCompletedEventDto event,
                                          Channel channel,
                                          @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            if (event.getPredictionId() == null) {
                throw new RuntimeException("Prediction ID is required");
            }

            predictionService.setPredictionResult(event.getPredictionId(), event.getResult().getLabel(), event.getResult().getProbability());

            // Acknowledge thành công
            channel.basicAck(deliveryTag, false);
            
        } catch (Exception e) {
            try {
                // Reject và không requeue
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ioException) {
                // Log error
                throw new RuntimeException("Failed to acknowledge message", ioException);
            }
        }
    } 
}
