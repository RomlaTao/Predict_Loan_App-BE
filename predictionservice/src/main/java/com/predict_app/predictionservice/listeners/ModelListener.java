package com.predict_app.predictionservice.listeners;

import com.predict_app.predictionservice.dtos.events.ModelPredictCompletedEventDto;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;

public interface ModelListener {
    void handleModelPredictCompletedEvent(ModelPredictCompletedEventDto event,
                                    Channel channel,
                                    @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag);
}
