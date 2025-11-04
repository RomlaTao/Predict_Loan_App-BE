package com.predict_app.customerservice.listeners;

import com.predict_app.customerservice.dtos.events.PredictionRequestedEventDto;
import com.predict_app.customerservice.dtos.events.PredictionCompletedEventDto;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;

public interface PredictionListener {
    void handlePredictionRequestedEvent(PredictionRequestedEventDto event,
                                    Channel channel,
                                    @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag);
    void handlePredictionCompletedEvent(PredictionCompletedEventDto event,
                                    Channel channel,
                                    @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag);
}
