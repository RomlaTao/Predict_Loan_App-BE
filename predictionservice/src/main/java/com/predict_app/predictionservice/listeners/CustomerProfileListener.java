package com.predict_app.predictionservice.listeners;

import com.predict_app.predictionservice.dtos.events.CustomerEnrichedEventDto;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;

public interface CustomerProfileListener {
    void handleCustomerProfileResponse(CustomerEnrichedEventDto event,
                                    Channel channel,
                                    @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag);
}
