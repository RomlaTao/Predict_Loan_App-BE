package com.predict_app.analysticservice.listeners;

import com.predict_app.analysticservice.dtos.events.PredictionCompletedEventDto;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;

public interface PredictionListener {
    void handlePredictionCompletedEvent(PredictionCompletedEventDto event,
        Channel channel,
        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag);
}
